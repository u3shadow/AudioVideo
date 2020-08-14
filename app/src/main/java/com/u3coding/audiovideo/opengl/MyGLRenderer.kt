package com.u3coding.audiovideo.opengl

import android.opengl.EGLConfig
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {
    private lateinit var mTriangle: Triangle

    fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // initialize a triangle
        mTriangle = Triangle()

    }


    override fun onDrawFrame(p0: GL10?) {
        mTriangle.draw()
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    override fun onSurfaceCreated(p0: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        TODO("Not yet implemented")
    }
}
