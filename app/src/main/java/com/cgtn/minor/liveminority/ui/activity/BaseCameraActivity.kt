package com.cgtn.minor.liveminority.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.AudioManager.STREAM_MUSIC
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.cgtn.minor.liveminority.R
import com.cgtn.minor.liveminority.mvp.model.BaseStreamConfig
import com.cgtn.minor.liveminority.utils.LogUtil
import com.cgtn.minor.liveminority.utils.NetworkUtils
import com.cgtn.minor.liveminority.utils.toast
import com.cgtn.minor.liveminority.widget.NetWorkListener
import com.example.yangfang.kotlindemo.util.SharedPreferenceUtil
import com.ksyun.media.streamer.encoder.VideoEncodeFormat
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilterBase
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilterMgt
import com.ksyun.media.streamer.kit.KSYStreamer
import com.ksyun.media.streamer.kit.StreamerConstants
import com.ksyun.media.streamer.kit.StreamerConstants.KSY_STREAMER_FILE_PUBLISHER_FORMAT_NOT_SUPPORTED
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_live.*
import kotlinx.android.synthetic.main.base_camera_actionbar.*
import java.util.*
import java.util.concurrent.TimeUnit

class BaseCameraActivity : AppCompatActivity(), NetWorkListener {

    private var mStreaming: Boolean = false
    private var mIsLandscape: Boolean = false
    private var mConfig: BaseStreamConfig? = null
    private lateinit var mStreamer: KSYStreamer
    private var mHWEncoderUnsupported: Boolean = false
    private var mSWEncoderUnsupported: Boolean = false
    private var mIsFlashOpened: Boolean = false
    private var isSilent = true
    private lateinit var mAudioManager: AudioManager
    private lateinit var mDebugInfo: String
    private var compositeDisposable: CompositeDisposable? = null


    /**
     * 音量值
     */
    private var mCurrentVolume by SharedPreferenceUtil("volume", 0)

    companion object {
        fun startActivity(context: Context, config: String?, cls: Class<*>) {
            val intent = Intent(context, cls)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("config", config)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_live)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mStreamer = KSYStreamer(this)
        mConfig = getConfig(intent.extras)
        initConfig()
        enableBeautyFilter()
        initView()
        setVolume()
        compositeDisposable = CompositeDisposable()
        // 是否显示调试信息
        if (mConfig!!.mShowDebugInfo) {
            startDebugInfoTimer()
        }
    }

    private fun getConfig(bundle: Bundle?): BaseStreamConfig {
        return BaseStreamConfig().fromJson(bundle!!.getString("config"))
    }

    /**
     * 推流参数的设定
     */
    private fun initConfig() {
        // 设置推流URL地址
        if (!TextUtils.isEmpty(mConfig!!.mUrl)) {
            mStreamer.url = mConfig!!.mUrl
        }
        LogUtil.e(mConfig!!.toJson())

        /**
         * 设置预览分辨率
         * 推流分辨路
         */
        mStreamer.setPreviewResolution(mConfig!!.mTargetResolution)
        mStreamer.setTargetResolution(mConfig!!.mTargetResolution)


        // 设置推流帧率
        if (mConfig!!.mFrameRate > 0) {
            mStreamer.previewFps = mConfig!!.mFrameRate
            mStreamer.targetFps = mConfig!!.mFrameRate
        }
        // 设置音频码率
        if (mConfig!!.mAudioKBitrate > 0) {
            mStreamer.setAudioKBitrate(mConfig!!.mAudioKBitrate)
        }
        // 设置推流视频码率，三个参数分别为初始码率、最高码率、最低码率
        if (mConfig!!.mVideoKBitrate != 0) {
            mStreamer.setVideoKBitrate(800, 1500, 500)
        } else {
            mStreamer.setVideoKBitrate(mConfig!!.mVideoKBitrate)
        }

        // 设置编码方式（硬编、软编）
        mStreamer.setEncodeMethod(mConfig!!.mEncodeMethod)
        // 硬编模式下默认使用高性能模式(high profile)
        if (mConfig!!.mEncodeMethod == StreamerConstants.ENCODE_METHOD_HARDWARE) {
            mStreamer.videoEncodeProfile = VideoEncodeFormat.ENCODE_PROFILE_HIGH_PERFORMANCE
        }

        // 设置视频方向（横屏、竖屏）
        if (mConfig!!.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mIsLandscape = true
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            mStreamer.rotateDegrees = 90
        }

        //设置摄像头的正反
        mStreamer.cameraFacing = mConfig!!.mCameraFacing

        //设置预览View
        setDisplayPreview()
        // 设置Streamer推流的回调处理函数
        mStreamer.onInfoListener = mOnInfoListener
        mStreamer.onErrorListener = mOnErrorListener
        // 禁用后台推流时重复最后一帧的逻辑（这里我们选择切后台使用背景图推流的方式）
        mStreamer.enableRepeatLastFrame = false
    }


    private fun initView() {
        NetworkUtils.initNetWorkCallBack(this, this)
        start_stream_tv.setOnClickListener {
            if (NetworkUtils.isConnected(this@BaseCameraActivity)) {
                if (mStreaming) {
                    stopStream()
                } else {
                    startStream()
                }
            } else {
                toast(this@BaseCameraActivity, "无网络连接，请连接网络重试")
                start_stream_tv.setImageResource(R.mipmap.reset)
            }
        }
        switch_cam.setOnClickListener {
            // 切换前后摄像头
            mStreamer.switchCamera()
        }

        flash.setOnClickListener {
            mIsFlashOpened = if (mIsFlashOpened) {
                //关闭闪光灯
                mStreamer.toggleTorch(false)
                false
            } else {
                //打开闪光灯
                mStreamer.toggleTorch(true)
                true
            }
        }
    }

    /**
     * 网络判断的监听 on or off
     */
    override fun onNetOn() {

    }

    override fun onNetOff() {
        toast(this, "网络连接断开")
    }


    /**
     * 开始推流
     */
    private fun startStream() {
        mStreamer.startStream()
        start_stream_tv.setImageResource(R.mipmap.stop)
        mStreaming = true
    }

    /**
     * stop live
     */
    private fun stopStream() {
        mStreamer.stopStream()
        start_stream_tv.setImageResource(R.mipmap.play)
        mStreaming = false
    }

    /**
     * 设置音量
     */
    private fun setVolume() {
        mAudioManager = getSystemService(Service.AUDIO_SERVICE) as AudioManager
        mCurrentVolume = getVolume()
        if (mCurrentVolume == 0) {
            setVolumeOff()
        } else {
            setVolumeOn()
        }
        volume_switch.setOnClickListener {
            LogUtil.e(getVolume())
            isSilent = if (isSilent) {
                setVolumeOff()
                mCurrentVolume = getVolume()
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                LogUtil.e(getVolume())
                false
            } else {
                setVolumeOn()
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume, 0)
                LogUtil.e(getVolume())
                true
            }
        }
    }

    private fun setVolumeOn() {
        volume_switch.setImageResource(R.mipmap.volume_up)

    }

    private fun setVolumeOff() {
        volume_switch.setImageResource(R.mipmap.volume_off)

    }

    override fun onResume() {
        super.onResume()
        handleStartVideo()
    }

    override fun onPause() {
        super.onPause()
        handlePauseVideo()
    }


    private fun handlePauseVideo() {
        // 调用KSYStreamer的onPause接口
        mStreamer.onPause()
        // 停止摄像头采集，然后开启背景图采集，以实现后台背景图推流功能
        mStreamer.stopCameraPreview()
//        mStreamer.startImageCapture(mBgImagePath)
        // 如果希望App切后台后，停止录制主播端的声音，可以在此切换为DummyAudio采集，
        // 该模块会代替mic采集模块产生静音数据，同时释放占用的mic资源
        mStreamer.setUseDummyAudioCapture(true)
    }

    /**
     * 开始采集
     */
    private fun handleStartVideo() {
        //调用流onResume
        mStreamer.onResume()
        //停止采集图像
        mStreamer.stopImageCapture()
        //开启头像采集
        if (requestPermission()) {
            mStreamer.startCameraPreview()
        }
        // 如果onPause中切到了DummyAudio模块，可以在此恢复
        mStreamer.setUseDummyAudioCapture(false)
    }

    @SuppressLint("CheckResult")
    private fun requestPermission(): Boolean {
        var isGranted = false
        val rxPermissions = RxPermissions(this)
        if (!rxPermissions.isGranted(Manifest.permission.CAMERA) && !rxPermissions.isGranted(Manifest.permission.RECORD_AUDIO)) {
            rxPermissions.requestEach(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
                .subscribe {
                    when {
                        it.granted -> isGranted = true
                        it.shouldShowRequestPermissionRationale -> {
                            isGranted = false
                            toast(this, this.getString(R.string.permissions_1))
                            setDialog(it.name)
                        }
                        else -> {
                            isGranted = false
                            setDialog(it.name)
                        }
                    }
                }
            return isGranted
        } else {
            return true
        }

    }

    /**
     * 设置权限的dialog
     */
    private fun setDialog(name: String) {
        val builder = AlertDialog.Builder(this@BaseCameraActivity)
        builder.setMessage("No Camera or AudioRecord permission,Please setting")
        builder.setNegativeButton(
            "Not now"
        ) { _, _ ->
            finish()
        }
        builder.setPositiveButton("Go go to setting") { _, _ ->
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.show()
    }

    /**
     * Stream 流的信息监听
     */
    private val mOnInfoListener =
        KSYStreamer.OnInfoListener { what, msg1, msg2 -> onStreamerInfo(what, msg1, msg2) }

    private fun onStreamerInfo(what: Int, msg1: Int, msg2: Int) {
        LogUtil.e("OnInfo: $what msg1: $msg1 msg2: $msg2")
        when (what) {
            StreamerConstants.KSY_STREAMER_CAMERA_INIT_DONE ->
                LogUtil.e("KSY_STREAMER_CAMERA_INIT_DONE")
            StreamerConstants.KSY_STREAMER_CAMERA_FACING_CHANGED -> {
                LogUtil.e("KSY_STREAMER_CAMERA_FACING_CHANGED")
                // check is flash torch mode supported
                flash.isEnabled = mStreamer.cameraCapture.isTorchSupported
            }
            StreamerConstants.KSY_STREAMER_OPEN_STREAM_SUCCESS -> {
                toast(this, "推流成功")
                LogUtil.e("KSY_STREAMER_OPEN_STREAM_SUCCESS")
                start_stream_tv.setImageResource(R.mipmap.stop)
//                startChronometer()
            }
            StreamerConstants.KSY_STREAMER_FRAME_SEND_SLOW -> {
                LogUtil.e("KSY_STREAMER_FRAME_SEND_SLOW " + msg1 + "ms")
                Toast.makeText(
                    applicationContext, "Network not good!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            StreamerConstants.KSY_STREAMER_EST_BW_RAISE -> LogUtil.e("BW raise to " + msg1 / 1000 + "kbps")
            StreamerConstants.KSY_STREAMER_EST_BW_DROP -> LogUtil.e("BW drop to " + msg1 / 1000 + "kpbs")
            else -> {
            }
        }
    }

    /**
     * 处理回掉错误信息
     */
    private val mOnErrorListener =
        KSYStreamer.OnErrorListener { what, msg1, msg2 -> onStreamerError(what, msg1, msg2) }

    private fun onStreamerError(what: Int, msg1: Int, msg2: Int) {
        LogUtil.e("streaming error: what=$what msg1=$msg1 msg2=$msg2")
        when (what) {
            //音频错误
            StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED,
            StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN -> {
                toast(this, "音频打开错误")
            }
            //相机错误
            StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN,
            StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED,
            StreamerConstants.KSY_STREAMER_CAMERA_ERROR_EVICTED,
            StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED -> {
                toast(this, "相机服务异常")
                mStreamer.stopCameraPreview()
            }
            //编码错误
            StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED,
            StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN -> {
                handleEncodeError()
            }
            //推流问题
            StreamerConstants.KSY_STREAMER_RTMP_ERROR_UNKNOWN -> {
                LogUtil.e("RTMP推流未知错误")
                reStreaming("推流错误")
            }
            StreamerConstants.KSY_STREAMER_ERROR_DNS_PARSE_FAILED -> {
                reStreaming("url域名解析失败，请检查推流地址")
                LogUtil.e("url域名解析失败")
            }
            StreamerConstants.KSY_STREAMER_ERROR_CONNECT_FAILED -> {
                netErrorReset("网络连接失败，无法建立连接，请检查网络")

//                reStreaming(what)
                LogUtil.e("网络连接失败，无法建立连接")
            }
            StreamerConstants.KSY_STREAMER_ERROR_PUBLISH_FAILED -> {
                LogUtil.e("推流失败")
                reStreaming("推流失败，请重试")
            }
            StreamerConstants.KSY_STREAMER_ERROR_CONNECT_BREAKED -> {
                LogUtil.e("网络断开,正在重试")
                netErrorReset("网络断开,请重试")
            }
            KSY_STREAMER_FILE_PUBLISHER_FORMAT_NOT_SUPPORTED -> {
                reStreaming("推流地址解析错误，请检查推流地址")
            }
            else -> reStreaming("推流失败，请重试")
        }
    }

    private fun netErrorReset(s: String) {
        mStreamer.stopStream()
        mStreaming = false
        start_stream_tv.setImageResource(R.mipmap.reset)
        toast(this, s)
    }

    /**
     * 推流发生错误，重试
     */
    private fun reStreaming(s: String) {
        toast(this, s)
        mStreamer.stopStream()
        start_stream_tv.setImageResource(R.mipmap.reset)
        mStreaming = false
//        mHandler.postDelayed({
//            startStream()
//        }
//            , 3000)

//        Observable.timer(3000L, TimeUnit.MILLISECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                startStream()
    }


    /*
     * 编码错误的处理
     */
    private fun handleEncodeError() {
        val encodeMethod = mStreamer.videoEncodeMethod
        if (encodeMethod == StreamerConstants.ENCODE_METHOD_HARDWARE) {
            mHWEncoderUnsupported = true
            if (mSWEncoderUnsupported) {
                mStreamer.setEncodeMethod(
                    StreamerConstants.ENCODE_METHOD_SOFTWARE_COMPAT
                )
                LogUtil.e("Got HW encoder error, switch to SOFTWARE_COMPAT mode")
            } else {
                mStreamer.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE)
                LogUtil.e("Got HW encoder error, switch to SOFTWARE mode")
            }
        } else if (encodeMethod == StreamerConstants.ENCODE_METHOD_SOFTWARE) {
            mSWEncoderUnsupported = true
            mSWEncoderUnsupported = true
            mStreamer.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE_COMPAT)
            LogUtil.e("Got SW encoder error, switch to SOFTWARE_COMPAT mode")
        }

    }

    /**
     * 滤镜设置
     */
    private fun enableBeautyFilter() {
        mStreamer.imgTexFilterMgt.setOnErrorListener { _: ImgTexFilterBase, _: Int ->
            Toast.makeText(
                applicationContext, "当前机型不支持该滤镜",
                Toast.LENGTH_SHORT
            ).show()
            mStreamer.imgTexFilterMgt.setFilter(
                mStreamer.glRender,
                ImgTexFilterMgt.KSY_FILTER_BEAUTY_DISABLE
            )
        }
        /**
         * 滤镜还可以重新配置
         */
        mStreamer.imgTexFilterMgt.setFilter(mStreamer.glRender, ImgTexFilterMgt.KSY_FILTER_BEAUTY_PRO3)

    }


    /**
     * debug模式显示推流成功时候的部分信息
     */
    private fun startDebugInfoTimer() {
        val disposable = Flowable.interval(1000, TimeUnit.MILLISECONDS)
            .subscribe {
                getDebugStreamInfo()
                    .doOnNext {
                        LogUtil.e(it)
                    }
                    .doOnError {
                        LogUtil.e("${it.message}")
                    }
                    //失败下，5s后重试
                    .delay(5L, TimeUnit.MILLISECONDS, true)
                    .subscribeOn(
                        Schedulers.io()
                    )
                    .repeat()
                    .retry()//失败后重新订阅
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        debug_info.text = it
                    }, {
                        LogUtil.e("推流信息出错")
                    })

            }

        compositeDisposable!!.add(disposable)
    }


    /**
     * 获取推流的数据信息
     *
     * debug下显示
     */
    private fun getDebugStreamInfo(): Flowable<String> {
        return Flowable.create({
            val encodeMethod: String = when (mStreamer.videoEncodeMethod) {
                StreamerConstants.ENCODE_METHOD_HARDWARE -> "HW"
                StreamerConstants.ENCODE_METHOD_SOFTWARE -> "SW"
                else -> "SW1"
            }
            mDebugInfo = String.format(
                Locale.getDefault(), " " +
                        "EncodeMethod=%s PreviewFps=%.2f \n " +
                        "RtmpHostIP()=%s DroppedFrameCount()=%d \n " +
                        "ConnectTime()=%dms DnsParseTime()=%dms \n " +
                        "UploadedKB()=%d EncodedFrames()=%d \n " +
                        "CurrentKBitrate=%d Version()=%s",
                encodeMethod, mStreamer.currentPreviewFps,
                mStreamer.rtmpHostIP, mStreamer.droppedFrameCount,
                mStreamer.connectTime, mStreamer.dnsParseTime,
                mStreamer.uploadedKBytes, mStreamer.encodedFrames,
                mStreamer.currentUploadKBitrate, KSYStreamer.getVersion()
            )
            it.onNext(mDebugInfo)
        }, BackpressureStrategy.BUFFER)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (getVolume() >= 0) {
                    setVolumeOn()
                }
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                LogUtil.d("${mAudioManager.getStreamVolume(STREAM_MUSIC)}")
                if (getVolume() < 1) {
                    setVolumeOff()
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun getVolume(): Int {
        return mAudioManager.getStreamVolume(STREAM_MUSIC)
    }

    private fun setDisplayPreview() {
        mStreamer.setDisplayPreview(surfaceView)
    }

    override fun onDestroy() {
        super.onDestroy()
        mStreamer.release()
        compositeDisposable!!.dispose()
    }

}
