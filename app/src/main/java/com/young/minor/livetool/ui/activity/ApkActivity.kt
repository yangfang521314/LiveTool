package com.young.minor.livetool.ui.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.allenliu.versionchecklib.callback.APKDownloadListener
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder
import com.allenliu.versionchecklib.v2.builder.UIData
import com.ksy.statlibrary.util.NetworkUtil
import com.young.minor.livetool.R
import com.young.minor.livetool.app.LiveApplication
import kotlinx.android.synthetic.main.activity_apk.*
import java.io.File


class ApkActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ApkActivity"
    }

    private var mDownApkUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apk)
        mDownApkUrl = intent.getStringExtra("url")
        if (NetworkUtil.isNetworkAvailable(this)) {
            downloadNewApk()
        }else{
            Toast.makeText(this,"网络连接失败，请检查网络",Toast.LENGTH_LONG).show()
        }
    }

    private fun downloadNewApk() {
        val builder: DownloadBuilder = AllenVersionChecker
                .getInstance()
                .downloadOnly(
                        UIData.create().setDownloadUrl(mDownApkUrl)

                )
                .setNewestVersionCode(null)
                .setForceRedownload(true)
                .setShowNotification(false)
                .setDirectDownload(true)
                .setShowDownloadingDialog(false)
                .setApkDownloadListener(object : APKDownloadListener {
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onDownloading(progress: Int) {
                        update_progress.text = "更新中。已下载$progress%"
                        progress_bar.max = 100
                        if (Build.VERSION.SDK_INT > 22) {
                            progress_bar.setProgress(progress, true)
                        }else {
                            progress_bar.progress = progress
                        }
                    }

                    override fun onDownloadFail() {
                        Log.e(TAG,"下载失败")

                    }

                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun onDownloadSuccess(file: File?) {
                        if (appExist(LiveApplication.mInstance!!, applicationContext.packageName)) {
                            uninstallApp(applicationContext.packageName)
                        }
                        finish()
                    }
                })

        builder.executeMission(this)

    }

    /**
     * 静默卸载App
     *
     * @param packageName 包名
     * @return 是否卸载成功
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun uninstallApp(packageName: String): Boolean {
        if (TextUtils.isEmpty(packageName)) {
            return false
        }
        val i = Intent(Intent.ACTION_DELETE, Uri.parse("package:$packageName"))
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
        return true
    }

    private fun appExist(context: Context, packageName: String): Boolean {
        try {
            val packageInfoList = context.packageManager.getInstalledPackages(0)
            for (packageInfo in packageInfoList) {
                if (packageInfo.packageName.equals(packageName, ignoreCase = true)) {
                    return true
                }
            }
        } catch (e: Exception) {

        }

        return false
    }


}


