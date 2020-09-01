package com.u3coding.audiovideo.h264;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author zhangsutao
 * @file VideoDecoder
 * @brief h264视频解码器
 * @date 2016/7/29
 */
public class VideoDecoder implements VideoCodec{
    private Surface mSurface;
    private static final int TYPE_SPS=7;
    private static final int TYPE_PPS=8;
    private static final int TYPE_FRAME_DATA=5;
    private static final int NO_FRAME_DATA=-1;
    private final int TIMEOUT_US=10000;
    private static final String TAG="VideoDecoder";
    private Worker mWorker;
    private byte[] mSps;
    private byte[] mPps;
    public VideoDecoder(Surface surface){
        mSurface=surface;
    }


    public void start() {
        if (mWorker == null) {
            mWorker = new Worker();
            mWorker.setRunning(true);
            mWorker.start();
        }
    }
    public void stop() {
        if (mWorker != null) {
            mWorker.setRunning(false);
            mWorker = null;
        }

    }
    private class Worker extends Thread{
        volatile boolean isRunning;
        private MediaCodec decoder;
        private int mWidth;
        private int mHeight;
        MediaCodec.BufferInfo mBufferInfo;

        /**
         * 等待客户端连接，解码器配置
         * @return
         */
        public boolean  prepare(){
            mBufferInfo = new MediaCodec.BufferInfo();
            //首先读取编码的视频的长度和宽度

            //编码器那边会先发sps和pps来，头一帧就由sps和pps组成
            byte[] spspps= new byte[10];//mServer.readFrame();
            if(spspps==null){
                return false;
            }
            //找到sps与pps的分隔处
            int pos=0;
            if(!((pos+3<spspps.length)&&(spspps[pos]==0&&spspps[pos+1]==0&&spspps[pos+2]==0&&spspps[pos+3]==1))){
                return false;
            }else {
                //00 00 00 01开始标志后的一位
                pos=4;
            }
            while((pos+3<spspps.length)&&!(spspps[pos]==0&&spspps[pos+1]==0&&spspps[pos+2]==0&&spspps[pos+3]==1)){
                pos++;
            }
            if(pos+3>=spspps.length){
                return false;
            }
            mSps= Arrays.copyOfRange(spspps,0,pos);
            mPps=Arrays.copyOfRange(spspps,pos,spspps.length);
            MediaFormat format = MediaFormat.createVideoFormat("video/avc", mWidth, mHeight);
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, mHeight*mWidth);
            format.setInteger(MediaFormat.KEY_MAX_HEIGHT, mHeight);
            format.setInteger(MediaFormat.KEY_MAX_WIDTH,mWidth);
            format.setByteBuffer("csd-0", ByteBuffer.wrap(mSps));
            format.setByteBuffer("csd-1", ByteBuffer.wrap(mPps));
            try {
                decoder = MediaCodec.createDecoderByType("video/avc");
            } catch (IOException e) {
                e.printStackTrace();
            }
            decoder.configure(format,mSurface, null, 0);
            decoder.start();
            return true;
        }

        public void setRunning(boolean running){
            isRunning =running;
        }
        @Override
        public void run() {
            if(!prepare()){
                Log.d(TAG,"视频解码器初始化失败");
                isRunning=false;
            }
            while (isRunning) {
                decode();
            }
            release();

        }

        private void decode() {

            boolean isEOS = false;
            while(!isEOS){
                //判断是否是流的结尾
                int inIndex = decoder.dequeueInputBuffer(TIMEOUT_US);
                if (inIndex >= 0) {
                    /**
                     * 测试
                     */
//                    byte[] frame=mServer.readFrame();
                    Frame frame= null;//mServer.readFrameWidthCache();
                    ByteBuffer buffer =decoder.getInputBuffer(inIndex);
                    if(buffer==null){
                        return ;
                    }
                    buffer.clear();
                    if (frame== null) {
                        Log.d(TAG, "InputBuffer BUFFER_FLAG_END_OF_STREAM");
                        decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        isEOS = true;
                        isRunning=false;
                        //服务已经断开，释放服务端
                      //  mServer.release();
                    } else {
                        buffer.put(frame.mData, 0, frame.length);
                        buffer.clear();
                        buffer.limit(frame.length);
                        decoder.queueInputBuffer(inIndex, 0, frame.length, 0, MediaCodec.BUFFER_FLAG_SYNC_FRAME);
                    }
                }else {
                    isEOS=true;
                }
                int outIndex = decoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);
                Log.d(TAG,"video decoding .....");
                while (outIndex >= 0) {
//                        ByteBuffer buffer = decoder.getOutputBuffer(outIndex);
                    decoder.releaseOutputBuffer(outIndex, true);
                    outIndex = decoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);//再次获取数据，如果没有数据输出则outIndex=-1 循环结束
                }
            }

        }

        /**
         * 释放资源
         */
        private void release(){
            if(decoder!=null){
                decoder.stop();
                decoder.release();
            }
        }
    }
}