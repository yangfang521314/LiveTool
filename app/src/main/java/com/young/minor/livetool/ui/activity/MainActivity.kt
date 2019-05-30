package com.young.minor.livetool.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.yanyusong.y_divideritemdecoration.Y_Divider
import com.yanyusong.y_divideritemdecoration.Y_DividerBuilder
import com.yanyusong.y_divideritemdecoration.Y_DividerItemDecoration
import com.young.minor.livetool.R
import com.young.minor.livetool.db.DBHelper
import com.young.minor.livetool.mvp.model.BaseStreamConfig
import com.young.minor.livetool.mvp.model.TaskEntity
import com.young.minor.livetool.ui.adapter.TaskAdapter
import com.young.minor.livetool.utils.toast
import com.young.minor.livetool.widget.OnItemClickListener
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, OnItemClickListener {

    private var mExitTime = 0L

    private var mConfig: String? = null

    private var mTaskAdapter: TaskAdapter? = null

    private var mDispose: Disposable? = null

    private var rtmp_url: String? = null

    var showWeb: String? = null
    var showUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initView()
    }

    private fun initView() {
        set_iv.setOnClickListener(this)
        live.setOnClickListener(this)
        recy_url.layoutManager = LinearLayoutManager(this)
        recy_url.addItemDecoration(DividerItemDecoration(this))
    }

    private fun initData() {

        /**
         * 默认视频参数
         */
        mConfig = BaseStreamConfig().toJson()
        initAdapter()
    }

    private fun initAdapter() {
        mDispose = Observable.create(ObservableOnSubscribe<List<TaskEntity>> {
            val taskList = DBHelper.mInstance.getTaskDao().getTaskData()
            it.onNext(taskList)
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mTaskAdapter = TaskAdapter(it)
                mTaskAdapter!!.setOnClickListener(this)
                recy_url.adapter = mTaskAdapter
            }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.set_iv -> {
                startActivityForResult(Intent(this@MainActivity, SettingActivity::class.java), 2)
            }
            R.id.live -> {
                if (TextUtils.isEmpty(rtmp_url)) {
                    toast(this, "请选择推流地址")
                } else {
                    //0 跳转到本地
                    val baseStreamConfig: BaseStreamConfig = BaseStreamConfig().fromJson(mConfig)
                    baseStreamConfig.mUrl = rtmp_url
                    BaseCameraActivity.startActivity(
                        this@MainActivity,
                        baseStreamConfig.toJson(),
                        BaseCameraActivity::class.java
                    )
                }
            }
        }

    }

    /**
     * 任务栏推流url的回调处理,推流地址
     */
    override fun onClickListener(view: View, json: Any) {
        Log.e("Tag", json.toString())
        rtmp_url = json as String
    }

    /**
     * 回掉设置的处理
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2) {
            mConfig = data!!.extras!!.getString("config")
        }
    }

    inner class DividerItemDecoration(context: Context) : Y_DividerItemDecoration(context) {

        override fun getDivider(itemPosition: Int): Y_Divider? {
            var divider: Y_Divider? = null
            when (itemPosition % 2) {
                0 ->
                    //每一行第一个显示rignt和bottom
                    divider = Y_DividerBuilder()
                        .setLeftSideLine(true, Color.parseColor("#dddddd"), 1f, 0f, 0f)
                        .setTopSideLine(true, Color.parseColor("#dddddd"), 1f, 0f, 0f)
                        .setRightSideLine(true, Color.parseColor("#dddddd"), 1f, 0f, 0f)
                        .create()
                1 ->
                    //每一行第一个显示rignt和bottom
                    divider = Y_DividerBuilder()
                        .setLeftSideLine(true, Color.parseColor("#dddddd"), 1f, 0f, 0f)
                        .setTopSideLine(true, Color.parseColor("#dddddd"), 1f, 0f, 0f)
                        .setRightSideLine(true, Color.parseColor("#dddddd"), 1f, 0f, 0f)
                        .create()
                else -> {
                }
            }
            if (mTaskAdapter!!.itemCount - 1 == itemPosition) {
                //每一行第一个显示rignt和bottom
                divider = Y_DividerBuilder()
                    .setTopSideLine(true, Color.parseColor("#dddddd"), 1f, 0f, 0f)
                    .setLeftSideLine(true, Color.parseColor("#dddddd"), 1f, 0f, 0f)
                    .setBottomSideLine(true, Color.parseColor("#dddddd"), 1f, 0f, 0f)
                    .setRightSideLine(true, Color.parseColor("#dddddd"), 1f, 0f, 0f)
                    .create()
            }
            return divider
        }
    }

    /**
     * 退出程序
     */
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
        if (mDispose != null && !mDispose!!.isDisposed) {
            mDispose!!.dispose()
        }
    }

}
