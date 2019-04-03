package com.young.minor.livetool.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.young.minor.livetool.R
import com.young.minor.livetool.widget.CommonWebView
import kotlinx.android.synthetic.main.activity_third.*

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
        initData()
    }

    private fun initData() {
        val url = intent.getStringExtra("url")
        //apk和普通的url区别
        val comWebView: CommonWebView = findViewById(R.id.common_webview)
        comWebView.loadUrl(url)
    }

    /**
     * 退出程序
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (common_webview.canBack()) {
                common_webview.back()
                return true
            } else {
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
