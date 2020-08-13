package com.u3coding.audiovideo.mp4

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

class Mp4Handler {
    //分离mp4文件音视频
    fun exactor(mediaExtractor: MediaExtractor) {
        var videoOutputStream: FileOutputStream? = null
        var audioOutputStream: FileOutputStream? = null
        try {
            val videoFile = File(
                Environment.getExternalStorageDirectory().absoluteFile,
                "output_video.mp4"
            )
            val audioFile = File(
                Environment.getExternalStorageDirectory().absoluteFile,
                "output_audio"
            )
            videoOutputStream = FileOutputStream(videoFile)
            audioOutputStream = FileOutputStream(audioFile)
            mediaExtractor.setDataSource(
                Environment.getExternalStorageDirectory().absoluteFile
                    .toString() + "/input.mp4"
            )
            val trackCount = mediaExtractor.trackCount
            var audioTrackIndex = -1
            var videoTrackIndex = -1
            for (i in 0 until trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(i)
                val mineType = trackFormat.getString(MediaFormat.KEY_MIME)
                //视频信道
                if (mineType.startsWith("video/")) {
                    videoTrackIndex = i
                }
                //音频信道
                if (mineType.startsWith("audio/")) {
                    audioTrackIndex = i
                }
            }
            val byteBuffer = ByteBuffer.allocate(500 * 1024)
            //切换到视频信道
            //切换到视频信道
            mediaExtractor.selectTrack(videoTrackIndex)
            while (true) {
                val readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleCount < 0) {
                    break
                }
                //保存视频信道信息
                val buffer = ByteArray(readSampleCount)
                byteBuffer[buffer]
                videoOutputStream.write(buffer)
                byteBuffer.clear()
                mediaExtractor.advance()
            }
            //切换到音频信道
            mediaExtractor.selectTrack(audioTrackIndex)
            while (true) {
                val readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleCount < 0) {
                    break
                }
                //保存音频信息
                val buffer = ByteArray(readSampleCount)
                byteBuffer[buffer]
                audioOutputStream.write(buffer)
                byteBuffer.clear()
                mediaExtractor.advance()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaExtractor.release()
            try {
                videoOutputStream!!.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    //合成视频信息
    fun muxerMediaVideo() {
        try {
            val mediaExtractor: MediaExtractor? = getMediaExtractor(
                Environment.getExternalStorageDirectory().absoluteFile
                    .toString() + "/input.mp4"
            )
            val videoIndex: Int = getIndex(mediaExtractor!!, "video/")
            //切换道视频信号的信道
            //切换道视频信号的信道
            mediaExtractor.selectTrack(videoIndex)
            val trackFormat = mediaExtractor.getTrackFormat(videoIndex)
            val mediaMuxer = MediaMuxer(
                Environment.getExternalStorageDirectory().absoluteFile
                    .toString() + "/output_video.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
            )
            val videoSampleTime: Int
            val byteBuffer = ByteBuffer.allocate(500 * 1024)
            videoSampleTime = trackFormat.getInteger(MediaFormat.KEY_FRAME_RATE)
            val trackIndex = mediaMuxer.addTrack(trackFormat)
            val bufferInfo = MediaCodec.BufferInfo()
            mediaMuxer.start()
            bufferInfo.presentationTimeUs = 0
            while (true) {
                val readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleSize < 0) {
                    break
                }
                mediaExtractor.advance()
                bufferInfo.size = readSampleSize
                bufferInfo.offset = 0
                bufferInfo.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME
                bufferInfo.presentationTimeUs += 1000 * 1000 / videoSampleTime.toLong()
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo)
            }
            //release
            //release
            mediaMuxer.stop()
            mediaExtractor.release()
            mediaMuxer.release()

            Log.e("TAG", "finish")
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    //合成音频
    fun muxerMediaAudio() {
        try {
            val mediaExtractor: MediaExtractor? = getMediaExtractor(
                Environment.getExternalStorageDirectory().absoluteFile
                    .toString() + "/input.mp4"
            )
            val audioIndex1: Int = getIndex(mediaExtractor!!, "audio/")
            mediaExtractor.selectTrack(audioIndex1)
            val trackFormat = mediaExtractor.getTrackFormat(audioIndex1)
            val mediaMuxer = MediaMuxer(
                Environment.getExternalStorageDirectory().absoluteFile
                    .toString() + "/output_audio.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
            )
            val byteBuffer = ByteBuffer.allocate(500 * 1024)
            val trackIndex = mediaMuxer.addTrack(trackFormat)
            val bufferInfo = MediaCodec.BufferInfo()
            mediaMuxer.start()
            bufferInfo.presentationTimeUs = 0
            val stampTime: Long = getStampTime(mediaExtractor, byteBuffer)
            mediaExtractor.unselectTrack(audioIndex1)
            mediaExtractor.selectTrack(audioIndex1)
            while (true) {
                val readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0)
                if (readSampleSize < 0) {
                    break
                }
                mediaExtractor.advance()
                bufferInfo.size = readSampleSize
                bufferInfo.offset = 0
                bufferInfo.flags = mediaExtractor.sampleFlags
                bufferInfo.presentationTimeUs += stampTime
                mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo)
            }
            //release
            mediaMuxer.stop()
            mediaExtractor.release()
            mediaMuxer.release()
            Log.e("TAG", "finish")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun getMediaExtractor(source: String): MediaExtractor? {
        val mediaExtractor = MediaExtractor()
        mediaExtractor.setDataSource(source)
        return mediaExtractor
    }

    fun getStampTime(
        mediaExtractor: MediaExtractor,
        byteBuffer: ByteBuffer
    ): Long {
        var stampTime: Long = 0
        //获取帧之间的间隔时间
        run({
            mediaExtractor.readSampleData(byteBuffer, 0)
            if (mediaExtractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                mediaExtractor.advance()
            }
            mediaExtractor.readSampleData(byteBuffer, 0)
            val secondTime: Long = mediaExtractor.getSampleTime()
            mediaExtractor.advance()
            mediaExtractor.readSampleData(byteBuffer, 0)
            val thirdTime: Long = mediaExtractor.getSampleTime()
            stampTime = Math.abs(thirdTime - secondTime)
            Log.e("audio111", stampTime.toString() + "")
        })
        return stampTime
    }

    private fun getIndex(mediaExtractor: MediaExtractor, channal: String): Int {
        var index = -1
        val trackCount = mediaExtractor.trackCount
        for (i in 0 until trackCount) {
            val trackFormat = mediaExtractor.getTrackFormat(i)
            if (trackFormat.getString(MediaFormat.KEY_MIME).startsWith(channal)) {
                index = i
            }
        }
        return index
    }
}