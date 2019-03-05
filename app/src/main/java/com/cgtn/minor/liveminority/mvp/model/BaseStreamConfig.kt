package com.cgtn.minor.liveminority.mvp.model

import android.content.pm.ActivityInfo
import com.cgtn.minor.liveminority.BuildConfig
import com.google.gson.GsonBuilder
import com.ksyun.media.streamer.capture.CameraCapture
import com.ksyun.media.streamer.kit.StreamerConstants

/**
 * created by yf on 2019/1/7.
 */
class BaseStreamConfig {
    var mUrl: String? = null
    //摄像头设置
    var mCameraFacing: Int = CameraCapture.FACING_BACK
    //帧率
    var mFrameRate: Float = 25f
    //码率
    var mVideoKBitrate: Int = 800
    //音频
    var mAudioKBitrate: Int = 48
    //推流分辨率
    var mTargetResolution: Int = StreamerConstants.VIDEO_RESOLUTION_720P
    //方向
    var mOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    //编码方式
    var mEncodeMethod: Int = StreamerConstants.ENCODE_METHOD_SOFTWARE

    var mAutoStart: Boolean = false

    //debug设置显示推流视频的信息
    var mShowDebugInfo: Boolean = BuildConfig.DEBUG

    //设置摄像头采集分辨率

    //视频采集率
    var mCaptureResolution: Int = 0
    //预览分辨率
    var mPreviewResolution: Int = StreamerConstants.VIDEO_RESOLUTION_720P
    var mPreviewViewType: Int = 0
    var mBgSwitchMode: Int = 0
    var mVideoCodecId: Int = 0
    var mVideoEncodeScene: Int = 0
    var mVideoEncodeProfile: Int = 0
    var mAudioEncodeProfile: Int = 0
    var mZoomFocus: Boolean = false
    var mStereoStream: Boolean = false
    var mBluetoothMicFirst: Boolean = false

    fun fromJson(json: String?): BaseStreamConfig {
        return GsonBuilder().create().fromJson(json, this.javaClass)
    }

    fun toJson(): String {
        return GsonBuilder().create().toJson(this)
    }


}