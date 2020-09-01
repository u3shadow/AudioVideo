package com.u3coding.audiovideo.h264;

/**
 * @author zhangsutao
 * @file VideoCodec.java
 * @brief 视频编解码器基类
 * @date 2016/8/7
 */
public interface VideoCodec {

    String MIME_TYPE = "video/avc";
    int VIDEO_FRAME_PER_SECOND = 15;
    int VIDEO_I_FRAME_INTERVAL = 5;
    int VIDEO_BITRATE = 500 * 8 * 1000;
}