package com.cgtn.minor.liveminority.utils

import android.os.Build
import java.lang.reflect.Proxy

class HookHelper {
    companion object {
        const val TARGET_INTENT = "target_intent"
        fun <T> hookAMS() {
            var defaultSingleton: Any? = null
            defaultSingleton = if (Build.VERSION.SDK_INT >= 26) {//1
                val activityManageClazz: Class<T> = Class.forName("android.app.ActivityManager") as Class<T>
                //获取activityManager中的IActivityManagerSingleton字段
                FieldUtil.getField(activityManageClazz, null, "IActivityManagerSingleton")
            } else {
                val activityManagerNativeClazz: Class<T> =
                    Class.forName("android.app.ActivityManagerNative") as Class<T>
                //获取ActivityManagerNative中的gDefault字段
                FieldUtil.getField(activityManagerNativeClazz, null, "gDefault")
            }
            val singletonClazz = Class.forName("android.util.Singleton")
            val mInstanceField = FieldUtil.getField(singletonClazz, "mInstance")
            //获取ActivityManager
            val iActivityManager = mInstanceField.get(defaultSingleton)
            val iActivityManagerClazz = Class.forName("android.app.IActivityManager")
            val proxy = Proxy.newProxyInstance(
                Thread.currentThread().contextClassLoader,
                arrayOf(iActivityManagerClazz), IActivityManagerProxy(iActivityManager)
            )
            mInstanceField.set(defaultSingleton, proxy)

        }
    }
}
