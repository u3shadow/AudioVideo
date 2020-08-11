package com.u3coding.audiovideo.showpic

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.u3coding.audiovideo.R
import kotlinx.android.synthetic.main.showpic_activity_layout.*


class ShowPicActivity :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.showpic_activity_layout)
        //imageview 绘制
        mImageView.setBackgroundResource(R.mipmap.ic_launcher);
        //surface view 绘制图片
        mSurface.holder.addCallback(object :SurfaceHolder.Callback{
            override fun surfaceChanged(surfaceHolder: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                if (surfaceHolder == null) {
                    return
                }
                val paint = Paint()
                paint.isAntiAlias = true
                paint.style = Paint.Style.STROKE
                val bitmap = BitmapFactory.decodeResource(resources, R.drawable.logopic)
                val canvas: Canvas = surfaceHolder.lockCanvas()
                canvas.drawBitmap(bitmap, 0F, 0F, paint)
                surfaceHolder.unlockCanvasAndPost(canvas)
            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
            }

            override fun surfaceCreated(p0: SurfaceHolder?) {
            }
        })

    }
}