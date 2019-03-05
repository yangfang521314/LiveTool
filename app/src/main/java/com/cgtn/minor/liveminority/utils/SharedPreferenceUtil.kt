package com.example.yangfang.kotlindemo.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.cgtn.minor.liveminority.app.LiveApplication
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPreferenceUtil<T>(val name: String, private val default: T) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getSharedPreferences(name, default)
    }

    private fun getSharedPreferences(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Float -> getFloat(name, default)
            is Boolean -> getBoolean(name, default)
            else -> {
                    throw IllegalArgumentException("sp can't save this type")
            }
        }
        res as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putSharedPreferences(name, value)
    }

    @SuppressLint("CommitPrefEdits")
    private fun <A> putSharedPreferences(name: String, value: A) =
        with(prefs.edit()) {
            when (value) {
                is Long -> putLong(name, value)
                is String -> putString(name, value)
                is Int -> putInt(name, value)
                is Boolean -> putBoolean(name, value)
                is Float -> putFloat(name, value)
                else -> {
                    putString(name, value.toString())
                }
            }.apply()


        }

    private val prefs: SharedPreferences by lazy {
        LiveApplication.mInstance!!.getSharedPreferences(
            "sp",
            Context.MODE_PRIVATE
        )
    }

    companion object {
        //双重锁单列模
//        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
//            SharedPreferenceUtil()
//
//        }
    }
}