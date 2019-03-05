package com.cgtn.minor.liveminority.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.widget.Toast

/**
 * created by yf on 2018/7/6.
 */


fun Activity.toast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}

fun Any.toast(context: Context, text: String){
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
}


//开启事务
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
    beginTransaction().commitAllowingStateLoss()
}

//增加fragment
fun FragmentActivity.addFragment(fragment: Fragment, content: Int, flag: String) {
    supportFragmentManager.inTransaction { add(content, fragment, flag).hide(fragment) }
}

fun FragmentActivity.replaceFragment(fragment: Fragment, content: Int, flag: String) {
    supportFragmentManager.inTransaction { replace(content, fragment, flag) }
}

fun FragmentActivity.showFragment(fragment: Fragment) {
    supportFragmentManager.inTransaction { show(fragment) }
}

fun FragmentActivity.hideFragment(fragment: Fragment) {
    supportFragmentManager.inTransaction { hide(fragment) }
}

fun commonStartActivity(forActivity: Activity, toActivity: Class<Any>) {
    val intent = Intent()
    intent.setClass(forActivity, toActivity)
    forActivity.startActivity(intent)
}




