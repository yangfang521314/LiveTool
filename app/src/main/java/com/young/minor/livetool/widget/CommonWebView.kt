package com.young.minor.livetool.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.webkit.*
import android.widget.LinearLayout
import com.young.minor.livetool.R
import java.util.*




/**
 * created by yf on 2019/3/30.
 */
class CommonWebView : LinearLayout {

    private var mWebView: WebView? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var mContext: Context

    //    private lateinit var mProgressBar: ProgressBar
    private var loadingDialog: LoadingDialog? = null

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        this.mContext = context
        initView(context)
    }

    private fun initView(context: Context?) {
        View.inflate(context, com.young.minor.livetool.R.layout.view_web_progress, this)
        mWebView = findViewById(com.young.minor.livetool.R.id.web_view)

        loadingDialog = LoadingDialog(context, R.style.add_dialog)
        val window = loadingDialog!!.window
        val lp = window!!.attributes
        lp.gravity = Gravity.CENTER
        lp.width = LinearLayout.LayoutParams.WRAP_CONTENT//宽高可设置具体大小
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        loadingDialog!!.window.attributes = lp
//        mProgressBar = findViewById(R.id.loadingProgressBar)
//        mProgressBar.visibility = View.VISIBLE
        initWebView()
    }


    /**
     * 带header
     *
     * @param url
     * @param header
     */
    fun loadUrl(url: String, header: String) {
        load(url, header)
    }

    /**
     * 不带header
     *
     * @param url
     */
    fun loadUrl(url: String) {
        load(url, "")
    }


    private fun load(url: String?, header: String) {
        var url = url
        if (!url!!.startsWith("http")) {
            url = "https://$url"
        }
        if (TextUtils.isEmpty(header)) {
            mWebView!!.loadUrl(url)
        } else if (!TextUtils.isEmpty(header)) {
            val extraHeaders = HashMap<String, String>()
            extraHeaders["Authorization"] = header
            mWebView!!.loadUrl(url, extraHeaders)
        }
    }


    /**
     * 初始化webview
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val webSettings = mWebView!!.settings
        webSettings.javaScriptEnabled = true
        // 设置可以访问文件
        webSettings.allowFileAccess = true
        // 设置可以支持缩放
        webSettings.setSupportZoom(true)
        // 支持保存数据
        webSettings.saveFormData = false
        // 设置默认缩放方式尺寸是far
        webSettings.defaultZoom = WebSettings.ZoomDensity.MEDIUM
        // 设置出现缩放工具
        webSettings.builtInZoomControls = false
        //设置 缓存模式
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        // 开启 DOM storage API 功能
        webSettings.domStorageEnabled = true
        //http https混合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webSettings.pluginState = WebSettings.PluginState.ON
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        // 清除缓存
        mWebView!!.clearCache(true)
        // 清除历史记录
        mWebView!!.clearHistory()
        mWebView!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        mWebView!!.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                loadingDialog!!.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (loadingDialog!!.isShowing) loadingDialog!!.dismiss()

            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                super.onReceivedError(view, request, error)
                if (loadingDialog!!.isShowing) loadingDialog!!.dismiss()

            }

        }
    }

    /**
     * can back
     *
     * @return
     */
    fun canBack(): Boolean {
        return mWebView!!.canGoBack()
    }

    /**
     * can forward
     *
     * @return
     */
    fun canForward(): Boolean {
        return mWebView!!.canGoForward()
    }

    /**
     * forward
     */
    fun goForward() {
        mWebView!!.goForward()
    }

    /**
     * back
     */
    fun back() {
        mWebView!!.goBack()
    }

    /**
     * destroy
     */
    fun destroy() {
        if (mWebView != null) {
            mWebView!!.removeAllViews()
            mWebView!!.destroy()
            mWebView = null
        }
    }

}