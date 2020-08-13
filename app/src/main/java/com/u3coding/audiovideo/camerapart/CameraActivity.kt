package com.u3coding.audiovideo.camerapart

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.view.TextureView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.u3coding.audiovideo.R
import kotlinx.android.synthetic.main.video_layout.*

class CameraActivity : AppCompatActivity(),TextureView.SurfaceTextureListener{
    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_layout)
        if (checkCameraHardware(this)) {
            mCamera =getCameraInstance()
        }
        mPreview = CameraPreview(this, mCamera!!)
        //FrameLayout frameLayout = (FrameLayout)findViewById(R.id.camera_preview);
        texture.surfaceTextureListener = this
        //frameLayout.addView(mPreview);
        val parameters = mCamera!!.parameters
        parameters.previewFormat = ImageFormat.NV21
        mCamera!!.parameters = parameters
        mCamera!!.setPreviewCallback { bytes: ByteArray?, camera: Camera? -> }
    }
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }
    fun getCameraInstance(): Camera? {
        var c: Camera? = null
        try {
            c = Camera.open() // attempt to get a Camera instance
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
        }
        return c // returns null if camera is unavailable
    }

    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {
    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
        try {
            mCamera!!.setPreviewTexture(p0)
            mCamera!!.startPreview()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}