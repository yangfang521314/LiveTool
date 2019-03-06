package com.cgtn.minor.liveminority.utils

import java.lang.reflect.Field
import java.util.*

/**
 * 目的是为了对字段进行反射操作
 */
class FieldUtil{
    companion object {
        fun <T>getField(clazz: Class<T>, target: Any?, name:String): Any? {
             val filed = clazz.getDeclaredField(name)
             filed.isAccessible = true
             return filed.get(target)
         }

        fun <T> getField(clazz: Class<T>,name: String): Field {
            val filed = clazz.getDeclaredField(name)
            filed.isAccessible = true
            return filed
        }

        fun <T>setField(clazz: Class<T>,target: Any,name: String,value: Any){
            val filed = clazz.getDeclaredField(name)
            filed.isAccessible =true
            filed.set(target,value      )
        }
    }
}