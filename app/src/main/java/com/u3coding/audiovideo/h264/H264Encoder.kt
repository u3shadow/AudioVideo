package com.u3coding.playerandrecoder.videorecoder

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Environment
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class H264Encoder(private val width: Int, private val height: Int) {
   private var bitrate = 0
   private var mMediaCodec: MediaCodec? = null
   private var outputStream: BufferedOutputStream? = null
   private var fileTemp: File? = null
   private var isRecording = false
   private fun initMediaCodec(width: Int, height: Int) {
      bitrate = 5 * width * height //码率
      try {
         mMediaCodec = MediaCodec.createEncoderByType("video/avc")
         val mediaFormat = MediaFormat.createVideoFormat(
            "video/avc",
            height,
            width
         ) //height和width一般都是照相机的height和width。
         //描述平均位速率（以位/秒为单位）的键。 关联的值是一个整数
         mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitrate)
         mediaFormat.setInteger(
            MediaFormat.KEY_BITRATE_MODE,
            MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ
         )
         //描述视频格式的帧速率（以帧/秒为单位）的键。
         mediaFormat.setInteger(
            MediaFormat.KEY_FRAME_RATE,
            FRAMERATE
         ) //帧率，一般在15至30之内，太小容易造成视频卡顿。
         mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar
         ) //色彩格式，具体查看相关API，不同设备支持的色彩格式不尽相同
         //关键帧间隔时间，单位是秒
         mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
         mMediaCodec!!.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
         mMediaCodec!!.start() //开始编码
      } catch (e: IOException) {
         e.printStackTrace()
      }
   }

   fun startRecord() {
      isRecording = true
   }

   fun encode(datas: Queue<ByteArray>) {
      Thread(Runnable { codeData(datas) }).start()
   }

   fun stopRecord() {
      isRecording = false
   }

   private fun computePresentationTime(frameIndex: Long): Long {
      return 132 + frameIndex * 1000000 / FRAMERATE
   }

   private fun codeData(datas: Queue<ByteArray>) {
      while (datas.size > 0) {
         var input = datas.poll()
         val yuv420sp = ByteArray(width * height * 3 / 2)
         NV21ToNV12(input, yuv420sp, width, height)
         input = yuv420sp
         var pts: Long = 0
         var generateIndex: Long = 0
         if (input != null) {
            try {
               val inputBuffers =
                  mMediaCodec!!.inputBuffers //拿到输入缓冲区,用于传送数据进行编码
               val outputBuffers =
                  mMediaCodec!!.outputBuffers //拿到输出缓冲区,用于取到编码后的数据
               val inputBufferIndex = mMediaCodec!!.dequeueInputBuffer(-1)
               if (inputBufferIndex >= 0) { //当输入缓冲区有效时,就是>=0
                  pts = computePresentationTime(generateIndex)
                  val inputBuffer = inputBuffers[inputBufferIndex]
                  inputBuffer.clear()
                  inputBuffer.put(input) //往输入缓冲区写入数据,
                  //                    //五个参数，第一个是输入缓冲区的索引，第二个数据是输入缓冲区起始索引，第三个是放入的数据大小，第四个是时间戳，保证递增就是
                  mMediaCodec!!.queueInputBuffer(inputBufferIndex, 0, input.size, pts, 0)
                  generateIndex++
               }
               val bufferInfo = MediaCodec.BufferInfo()
               var outputBufferIndex = mMediaCodec!!.dequeueOutputBuffer(
                  bufferInfo,
                  TIMEOUT_USEC
               ) //拿到输出缓冲区的索引
               while (outputBufferIndex >= 0) {
                  val outputBuffer = outputBuffers[outputBufferIndex]
                  val outData = ByteArray(bufferInfo.size)
                  outputBuffer[outData]
                  //outData就是输出的h264数据
                  outputStream!!.write(outData, 0, outData.size) //将输出的h264数据保存为文件，用vlc就可以播放
                  mMediaCodec!!.releaseOutputBuffer(outputBufferIndex, false)
                  outputBufferIndex = mMediaCodec!!.dequeueOutputBuffer(
                     bufferInfo,
                     TIMEOUT_USEC
                  )
               }
            } catch (t: Throwable) {
               t.printStackTrace()
            }
         }
      }
   }

   fun setName(name: String): Boolean {
      val fileTo = File(
         Environment.getExternalStorageDirectory().absolutePath + "/" + name
      )
      return fileTemp!!.renameTo(fileTo)
   }

   private fun createfile() {
      fileTemp = File(
         Environment.getExternalStorageDirectory().absolutePath + "/temp.h264"
      )
      if (fileTemp!!.exists()) {
         fileTemp!!.delete()
      }
      try {
         outputStream = BufferedOutputStream(FileOutputStream(fileTemp))
      } catch (e: Exception) {
         e.printStackTrace()
      }
   }

   private fun NV21ToNV12(
      nv21: ByteArray?,
      nv12: ByteArray?,
      width: Int,
      height: Int
   ) {
      if (nv21 == null || nv12 == null) return
      val framesize = width * height
      var i: Int
      var j: Int
      System.arraycopy(nv21, 0, nv12, 0, framesize)
      i = 0
      while (i < framesize) {
         nv12[i] = nv21[i]
         i++
      }
      j = 0
      while (j < framesize / 2) {
         nv12[framesize + j - 1] = nv21[j + framesize]
         j += 2
      }
      j = 0
      while (j < framesize / 2) {
         nv12[framesize + j] = nv21[j + framesize - 1]
         j += 2
      }
   }

   companion object {
      private const val FRAMERATE = 30
      private const val TIMEOUT_USEC: Long = 12000
   }

   init {
      initMediaCodec(width, height)
      createfile()
   }
}