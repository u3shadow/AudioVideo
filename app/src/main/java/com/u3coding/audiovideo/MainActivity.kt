package com.u3coding.audiovideo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.u3coding.audiovideo.opengl.OpenGLES20Activity
import com.u3coding.audiovideo.showpic.ShowPicActivity
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout)
        currentTest()
    }

    fun onClick(view: View) {
        var mIntent: Intent? = null
        when (view.id) {
            R.id.goPic -> {
                mIntent = Intent(this, ShowPicActivity::class.java)
            }
            R.id.goOpenGl -> {
                mIntent = Intent(this, OpenGLES20Activity::class.java)
            }
        }
        startActivity(mIntent)
    }

    /**
     * 通过反射获取类的所有变量
     */
    private fun printFields() {
        //1.获取并输出类的名称
        val mClass: Class<*> = SonClass::class.java
        println("类的名称：" + mClass.name)

        //2.1 获取所有 public 访问权限的变量
        // 包括本类声明的和从父类继承的
        val fields: Array<Field> = mClass.fields

        //2.2 获取所有本类声明的变量（不问访问权限）
        //Field[] fields = mClass.getDeclaredFields();

        //3. 遍历变量并输出变量信息
        for (field in fields) {
            //获取访问权限并输出
            val modifiers: Int = field.modifiers
            print(Modifier.toString(modifiers).toString() + " ")
            //输出变量的类型及变量名
            println(
                field.getType().getName()
                    .toString() + " " + field.name
            )
        }
    }

    fun currentTest() {
        val count = ConcurrentHashMap<String, AtomicInteger>()
        val countDown = CountDownLatch(2)
       val task = Runnable {
           var oldValue:AtomicInteger?
           for (i in 0..5){
               oldValue = count["a"]
               if (null == oldValue){
                   val zero = AtomicInteger(0)
                   oldValue = count.putIfAbsent("a",zero)
                   if (null == oldValue){
                        oldValue = zero
                   }
               }
               oldValue.incrementAndGet()
           }
           countDown.countDown()
       }
        Thread(task).start()
        Thread(task).start()
        try {
            countDown.await()
            Toast.makeText(this, ""+count["a"], Toast.LENGTH_LONG).show()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}