package com.young.minor.livetool.app

import android.app.Application
import android.content.Context
import com.avos.avoscloud.AVOSCloud


/**
 * created by yf on 2019/1/4.
 */
class LiveApplication : Application() {
    companion object {
        var mInstance: LiveApplication? = null
        //操作数据库

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        try {
//            HookHelper.hookAMS()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        AVOSCloud.initialize(this,"LgzqMLF2l7xpdaEpk3c5tHDc-gzGzoHsz","2RGdo4I63Y9fFD0yV7VelWXY")

    }

}