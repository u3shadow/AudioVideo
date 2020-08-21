package com.u3coding.audiovideo.opengl

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer : GLSurfaceView.Renderer {
    private lateinit var mTriangle: Triangle


    override fun onSurfaceCreated(var1: GL10?,
        var2: EGLConfig?){
        mTriangle = Triangle()
    }


    override fun onDrawFrame(p0: GL10?) {
        mTriangle.draw()
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
    }

}
