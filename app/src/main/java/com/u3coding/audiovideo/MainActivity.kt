package com.u3coding.audiovideo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.u3coding.audiovideo.opengl.OpenGLES20Activity
import com.u3coding.audiovideo.showpic.ShowPicActivity
import kotlinx.android.synthetic.main.main_activity_layout.*

class MainActivity :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)
    }
    fun onClick(view: View){
        var mIntent:Intent? = null
        when(view.id){
            R.id.goPic ->{
                mIntent = Intent(this,ShowPicActivity::class.java)
            }
            R.id.goOpenGl ->{
                mIntent = Intent(this,OpenGLES20Activity::class.java)
            }
        }
        startActivity(mIntent)
    }

}