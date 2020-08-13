package com.u3coding.audiovideo.camerapart

import android.content.ContentValues.TAG
import android.content.Context
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

class CameraPreview(context: Context,val mCamera: Camera) :SurfaceView(context),SurfaceHolder.Callback{
    private var mHolder: SurfaceHolder = holder
    init{
        mHolder.addCallback(this)
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }
    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
         if (mHolder.surface == null){
             return;
         }
        try {
            mCamera.stopPreview()
            mCamera.setPreviewDisplay(mHolder)
            mCamera.startPreview()
        }catch (e:Exception){

        }

    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        try {
            mCamera.setPreviewDisplay(p0)
            mCamera.startPreview()
        }catch (e:IOException){
            Log.d(TAG, "Error setting camera preview: " + e.message)
        }
    }

}