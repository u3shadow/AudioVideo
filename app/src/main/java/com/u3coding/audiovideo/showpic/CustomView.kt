package com.u3coding.audiovideo.showpic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.u3coding.audiovideo.R

class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    var paint: Paint = Paint()
    private lateinit var bitmap: Bitmap

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.isAntiAlias = true;
        paint.style = Paint.Style.STROKE;
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.logopic);
        canvas?.drawBitmap(bitmap,0f,0f,paint)
    }

}