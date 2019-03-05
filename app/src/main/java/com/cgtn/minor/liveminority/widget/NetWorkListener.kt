package com.cgtn.minor.liveminority.widget

/**
 * created by yf on 2019/1/22.
 * 网络监听的回调
 */

interface NetWorkListener {

    /**
     * 网络打开
     */
    fun onNetOn()

    /**
     * 网络关闭
     */
    fun onNetOff()

}