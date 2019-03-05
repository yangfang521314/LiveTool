package com.cgtn.minor.liveminority.base

/**
 * created by yf on 2019/1/10.
 */
class BaseContract {
    interface View {
        fun showErrorMsg(s: String)

        fun showLoading(s: String)
    }


    interface Presenter<in T> {
        fun subscribe()
        fun unsubscribe()
        fun attach(view: T)
        fun detach()
    }
}