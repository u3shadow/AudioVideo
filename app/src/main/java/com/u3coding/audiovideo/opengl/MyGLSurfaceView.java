package com.u3coding.audiovideo.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.u3coding.audiovideo.R;

/**
 * Created by u3-linux on 18-3-1.
 */

class MyGLSurfaceView extends GLSurfaceView {

    // private final MyGLRenderer mRenderer;
    private final MyGLRenderer mRenderer;
    private final OpenGLPic mPicRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer();
        Drawable db = getResources().getDrawable(R.drawable.logopic);
        BitmapDrawable drawable = (BitmapDrawable) db;
        Bitmap bitmap = drawable.getBitmap();
        mPicRenderer = new OpenGLPic(bitmap);
        setRenderer(mPicRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

}
