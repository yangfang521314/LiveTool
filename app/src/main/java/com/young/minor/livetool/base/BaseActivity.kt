package com.young.minor.livetool.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * created by yf on 2019/1/10.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setLayoutId())
        initData()
        initView()

    }

    /**
     * 初始化数据
     */
    open fun initData() {}

    /**
     * 初始化view
     */
    abstract fun initView()

    abstract fun setLayoutId(): Int

    override fun onDestroy() {
        super.onDestroy()
    }
}