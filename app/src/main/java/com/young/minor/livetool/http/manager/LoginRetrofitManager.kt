package com.young.minor.livetool.http.manager

import com.young.minor.livetool.http.base.BaseRetrofit

/**
 * created by yf on 2019/1/7.
 * 登录的逻辑接口
 */
class LoginRetrofitManager private constructor() : BaseRetrofit() {
    /**
     * 设置baseUrl的地址
     */
    override fun getBaseUrl(): String {
        return ""
    }

    /**
     * 单利模式
     */
    companion object {
        val loginRetrofitManager: LoginRetrofitManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            LoginRetrofitManager()
        }

    }

}