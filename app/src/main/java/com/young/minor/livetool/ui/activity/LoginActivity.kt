package com.young.minor.livetool.ui.activity

import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import com.young.minor.livetool.R
import com.young.minor.livetool.base.BaseMVPActivity
import com.young.minor.livetool.mvp.contract.LoginContract
import com.young.minor.livetool.mvp.presenter.LoginPresenter
import com.young.minor.livetool.utils.SharedPreferenceUtil
import com.young.minor.livetool.utils.toast
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseMVPActivity<LoginContract.LoginView, LoginPresenter>(), LoginContract.LoginView {
    private var isLogin by SharedPreferenceUtil("login", false)

    private var mExitTime = 0L


    override fun loginSuccess() {

    }

    private var mUserName: String? = null
    private var mPwd: String? = null
    override fun initData() {
        mPresenter = LoginPresenter()
        mPresenter!!.attach(this)
        //添加进入数据库
    }
    
    override fun initView() {
        mUserName = login_username.text.toString()
        mPwd = login_pwd.text.toString()
        login.setOnClickListener {
            isLogin = true
//            Completable.complete()
//                .observeOn(Schedulers.io())
//                .subscribe {
//                    DBHelper.mInstance.getTaskDao()
//                        .addTaskData(TaskEntity(1, "rtmp://54.223.252.143:1935/tv/Android1"))
//                }
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
//            if (checkUtils()) {
//                mPresenter!!.login()
//            }

        }
    }



    /**
     * 判断用户名和密码的相关正确性
     */
    private fun checkUtils(): Boolean {
        if (TextUtils.isEmpty(mUserName)) {
            toast(this, "请填写用户名")
            return false
        }
        if (TextUtils.isEmpty(mPwd)) {
            toast(this, "请填写密码")
            return false
        }
        return true
    }

    override fun setLayoutId(): Int {
        return R.layout.activity_login
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                toast(this, "再次点击退出App")
                mExitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}
