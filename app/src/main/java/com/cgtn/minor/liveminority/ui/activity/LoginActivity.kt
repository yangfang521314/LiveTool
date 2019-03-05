package com.cgtn.minor.liveminority.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.DashPathEffect
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.view.KeyEvent
import com.cgtn.minor.liveminority.base.BaseMVPActivity
import com.cgtn.minor.liveminority.db.DBHelper
import com.cgtn.minor.liveminority.mvp.contract.LoginContract
import com.cgtn.minor.liveminority.mvp.model.TaskEntity
import com.cgtn.minor.liveminority.mvp.presenter.LoginPresenter
import com.cgtn.minor.liveminority.utils.toast
import com.example.yangfang.kotlindemo.util.SharedPreferenceUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseMVPActivity<LoginContract.LoginView, LoginPresenter>(), LoginContract.LoginView {
    private var isLogin by SharedPreferenceUtil("login", false)

    private var mExitTime = 0L
    private var circleAngle: Int = 0


    override fun loginSuccess() {

    }

    private var mUserName: String? = null
    private var mPwd: String? = null
    override fun initData() {
        requestPermission()
        mPresenter = LoginPresenter()
        mPresenter!!.attach(this)
        //添加进入数据库

    }


    private lateinit var effect: DashPathEffect

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun initView() {
        mUserName = login_username.text.toString()
        mPwd = login_pwd.text.toString()
        login.setOnClickListener {
            isLogin = true
            Completable.complete()
                .observeOn(Schedulers.io())
                .subscribe {
                    DBHelper.mInstance.getTaskDao()
                        .addTaskData(TaskEntity(1, "rtmp://54.223.252.143:1935/tv/Android1"))
                }
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
//            if (checkUtils()) {
//                mPresenter!!.login()
//            }
        }
    }


    @SuppressLint("CheckResult")
    private fun requestPermission() {
        val permissions = RxPermissions(this)
        permissions.requestEach(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
        )
            .subscribe({ }, { })
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
        return com.cgtn.minor.liveminority.R.layout.activity_login
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
