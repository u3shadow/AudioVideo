package com.u3coding.audiovideo.h264;
/**
 * @author zhangsutao
 * @file filename
 * @brief 一帧的封装
 * @date 2016/8/11
 */
public class Frame {
    public byte[] mData;
    public int offset;
    public int length;
    public Frame(byte[] data,int offset,int size){
        mData=data;
        this.offset=offset;
        this.length=size;
    }
    public void setFrame(byte[] data,int offset,int size){
        mData=data;
        this.offset=offset;
        this.length=size;
    }
}
