package com.u3coding.audiovideo

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
fun main(args: Array<String>) {
    val cachedThreadPool: ExecutorService = Executors.newCachedThreadPool()
    for (i in 0..999) {
        try {
            Thread.sleep(i * 1000.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        cachedThreadPool.execute(Runnable { println(Thread.currentThread().name + ":" + i) })
    }
}
