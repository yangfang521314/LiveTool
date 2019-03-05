package com.cgtn.minor.liveminority.ui.activity

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import com.cgtn.minor.liveminority.R
import com.cgtn.minor.liveminority.base.BaseActivity
import com.cgtn.minor.liveminority.mvp.model.BaseStreamConfig
import com.cgtn.minor.liveminority.utils.LogUtil
import com.ksyun.media.streamer.kit.StreamerConstants
import com.ksyun.media.streamer.util.device.DeviceInfo
import com.ksyun.media.streamer.util.device.DeviceInfoTools
import kotlinx.android.synthetic.main.activity_set.*


class SettingActivity : BaseActivity() {


    private var baseConfig: BaseStreamConfig? = null

    override fun setLayoutId(): Int {
        return R.layout.activity_set
    }

    override fun initData() {
        super.initData()
        baseConfig = BaseStreamConfig()

    }

    override fun initView() {

        back.setOnClickListener {
            backMain()
        }

    }

    private fun backMain() {
        setConfig()
        val bundle = Bundle()
        bundle.putString("config", baseConfig!!.toJson())
        setResult(Activity.RESULT_CANCELED, this.intent.putExtras(bundle))
        this.finish()
    }

    private fun setConfig() {
        /**
         * 推流分辨率
         */
        when (preview_res_group.checkedRadioButtonId) {
            R.id.preview_480 -> {
                baseConfig!!.mTargetResolution = StreamerConstants.VIDEO_RESOLUTION_360P
            }
            R.id.preview_720 -> {
                baseConfig!!.mTargetResolution = StreamerConstants.VIDEO_RESOLUTION_540P
            }
            R.id.preview_1080 -> {
                baseConfig!!.mTargetResolution = StreamerConstants.VIDEO_RESOLUTION_720P
            }
        }

        when (videoBitrate_res_group.checkedRadioButtonId) {
            R.id.videoBitrate_500 -> {
                baseConfig!!.mVideoKBitrate = 500
            }
            R.id.videoBitrate_800 -> {
                baseConfig!!.mVideoKBitrate = 800
            }
            R.id.videoBitrate_1500 -> {
                baseConfig!!.mVideoKBitrate = 1500
            }
        }

        when (frameRate_res_group.checkedRadioButtonId) {
            R.id.frameRate_20 -> {
                baseConfig!!.mFrameRate = 20F
            }
            R.id.frameRate_25 -> {
                baseConfig!!.mFrameRate = 25f
            }
            R.id.frameRate_30 -> {
                baseConfig!!.mFrameRate = 30f
            }
        }

        if (isHw264EncoderSupported()) {
            LogUtil.e( "Encode - HandWare")
            baseConfig!!.mEncodeMethod = StreamerConstants.ENCODE_METHOD_HARDWARE
        } else {
            baseConfig!!.mEncodeMethod = StreamerConstants.ENCODE_METHOD_SOFTWARE
            LogUtil.e("Encode - SoftWare")
        }
    }

    private fun isHw264EncoderSupported(): Boolean {
        val deviceInfo = DeviceInfoTools.getInstance().deviceInfo
        if (deviceInfo != null) {
            LogUtil.e("deviceInfo:" + deviceInfo.printDeviceInfo())
            return deviceInfo.encode_h264 == DeviceInfo.ENCODE_HW_SUPPORT
        }
        return false
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // 是否触发按键为back键
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            backMain()
            true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }


}
