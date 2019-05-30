package com.young.minor.livetool.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.GetCallback
import com.ksy.statlibrary.util.NetworkUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import com.young.minor.livetool.R
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        requestPermission()
        if (NetworkUtil.isNetworkConnected(this)) {
            initWebData()
        } else {
            Toast.makeText(this, "网络连接失败，请检查网络", 0).show()

        }

    }

    @SuppressLint("CheckResult")
    private fun requestPermission() {
        val permissions = RxPermissions(this)
        permissions.request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
        )
            .subscribe({ }, { })
    }

    private fun initWebData() {
        val keys = "Url,ShowWeb"// 指定刷新的 key 字符串
        val theTodo = AVObject.createWithoutData("AppConfig", "5c9ec60217b54d0070b43649")
        theTodo.fetchInBackground(keys, object : GetCallback<AVObject>() {
            override fun done(avObject: AVObject?, e: AVException?) {
                if (avObject != null) {
                    //0  和 1
                    val showWeb = avObject.getString("ShowWeb")
                    val showUrl = avObject.getString("Url")
                    if (showUrl != null && showWeb != null) {
                        startOtherActivity(showUrl, showWeb)
                    } else {
                        startMainActivity()
                    }
                }
            }

            override fun internalDone(avException: AVException?) {
                super.internalDone(avException)
                Log.e("TAG", "${avException!!.message}")
            }
        })
    }

    private fun startMainActivity() {
        startCommonActivity(Intent(this@SplashActivity, MainActivity::class.java))
    }

    /**
     * 跳转到其他Activity
     */
    @SuppressLint("CheckResult")
    private fun startOtherActivity(showUrl: String, showWeb: String) {
        //0 跳转到本地
        if ("0" == showWeb) {
            //跳转到apk下载地址或者web端
            startMainActivity()
        } else if ("1" == showWeb) {
            val intent = Intent()
            Log.e("SplashActivity", showUrl)
            if (showUrl.contains(".apk")) {
                intent.putExtra("url", showUrl)
                intent.setClass(this@SplashActivity, ApkActivity::class.java)
            } else {
                intent.putExtra("url", showUrl)
                intent.setClass(this@SplashActivity, ThirdActivity::class.java)
            }
            startCommonActivity(intent)

        }

    }

    @SuppressLint("CheckResult")
    private fun startCommonActivity(intent: Intent) {
        Observable.timer(3L, TimeUnit.SECONDS)
            .subscribe {
                startActivity(intent)
                finish()
            }
    }

}
