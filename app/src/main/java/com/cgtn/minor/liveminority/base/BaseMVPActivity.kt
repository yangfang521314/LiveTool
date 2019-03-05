package com.cgtn.minor.liveminority.base

/**
 * created by yf on 2019/1/4.
 * 基类Activity
 */
abstract class BaseMVPActivity<V, T : BaseContract.Presenter<V>> : BaseActivity(), BaseContract.View {
    protected var mPresenter: T? = null


    override fun showErrorMsg(s: String) {

    }

    override fun showLoading(s: String) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter?.detach()
        mPresenter = null
    }

}