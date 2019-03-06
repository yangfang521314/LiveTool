package com.cgtn.minor.liveminority.utils

import android.app.ActivityManager
import android.app.IntentService
import android.content.Intent
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class IActivityManagerProxy(activityManager: Any) :InvocationHandler{
    var mActivityManager:Any? = activityManager

    override fun invoke(proxy: Any?, method: Method?, args: Array<in Any>?): Any {
        if ("startActivity" == method!!.name){
            var intent:Intent?= null
            var index = 0
            for (i in 0..args!!.size){
                if (args[i] is Intent){
                    index = i
                    break
                }
            }
            intent = args[index] as Intent
            val subIntent = Intent()
            //todo 需要启动的Activity
            val packageName = "com.cgtn...."
            subIntent.setClassName(packageName, "$packageName.StubActivity")
            subIntent.putExtra(HookHelper.TARGET_INTENT,intent)
            args[index] = subIntent
        }
        return method.invoke(mActivityManager,args)
    }

}