package com.cgtn.minor.liveminority.base

/**
 * created by yf on 2019/1/4.
 */
abstract class BasePresenter<T : BaseContract.View> : BaseContract.Presenter<T> {
    protected var mView: T? = null

    override fun subscribe() {
    }

    override fun unsubscribe() {
    }

    override fun attach(view: T) {
        mView = view
    }

    override fun detach() {
        if (mView != null) {
            mView = null
        }
    }

}