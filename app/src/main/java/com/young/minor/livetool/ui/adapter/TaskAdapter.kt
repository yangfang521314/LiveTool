package com.young.minor.livetool.ui.adapter

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.young.minor.livetool.R
import com.young.minor.livetool.db.DBHelper
import com.young.minor.livetool.mvp.model.TaskEntity
import com.young.minor.livetool.utils.toast
import com.young.minor.livetool.widget.OnItemClickListener
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_new_task.view.*
import kotlinx.android.synthetic.main.item_task.view.*

class TaskAdapter(
    taskList: List<TaskEntity>
) : RecyclerView.Adapter<TaskAdapter.ItemViewHolder>() {

    companion object {
        //头部
        var HEADER = 0
        //尾部
        var FOOT = 1

        var NORMAL = 2
    }

    private var preView: ImageView? = null
    private var onItemClickListener: OnItemClickListener? = null
    private var rtmpUrl: String? = null


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (getItemViewType(position)) {
            HEADER -> {
                with(holder.itemView) {
                    task_number.text = "ID"
                    task_url.text = "rtmp推流地址设置"
                    task_choice.visibility = View.INVISIBLE
                }
            }
            FOOT -> {
                with(holder.itemView) {

                    task_create_url.requestFocus()
                    task_create_url.isFocusable = true
                    textChange(task_create_url, task_new_choice,position)

                }
            }

            NORMAL -> {
                with(holder.itemView) {

                    task_create_number.text = mTaskEntity!![position - 1].id.toString()
                    task_create_url.setText(mTaskEntity!![position - 1].title)
                    task_new_choice.setOnClickListener {
                        if (preView != null) {
                            preView!!.setImageResource(R.mipmap.choice_not_address)
                        }
                        preView = task_new_choice
                        task_new_choice.setImageResource(R.mipmap.choice_address)
                        if (!TextUtils.isEmpty(task_create_url.text.toString())) {
                            onItemClickListener!!.onClickListener(holder.itemView, task_create_url.text.toString())
                        } else {
                            toast(context, "没有推流地址")
                        }

                    }
                }

            }

        }

    }

    private fun textChange(
        task_create_url: EditText?,
        task_new_choice: ImageView,
        position: Int
    ) {
        task_create_url!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.e("after", "$s")
                rtmpUrl = s.toString()
                if (s.toString() != "") {
                    task_new_choice.setOnClickListener {
                        if (preView != null) {
                            preView!!.setImageResource(R.mipmap.choice_not_address)

                        }
                        preView = task_new_choice
                        task_new_choice.setImageResource(R.mipmap.choice_address)
                        onItemClickListener!!.onClickListener(
                            task_new_choice, rtmpUrl!!
                        )
                        Log.d("tag", task_create_url.text.toString())
                        //添加进入数据库
                        Completable.complete()
                            .observeOn(Schedulers.io())
                            .subscribe {
                                DBHelper.mInstance.getTaskDao()
                                    .addTaskData(TaskEntity(position, task_create_url.text.toString()))
                            }
                    }


                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                rtmpUrl = s.toString()
                Log.e("changed", "$s")

            }

        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemViewHolder {
        val holder: RecyclerView.ViewHolder?
        holder = when (p1) {
            FOOT -> {
                ItemViewHolder(View.inflate(parent.context, R.layout.item_new_task, null))

            }
            HEADER -> {
                ItemViewHolder(View.inflate(parent.context, R.layout.item_task, null))

            }
            else -> {
                ItemViewHolder(View.inflate(parent.context, R.layout.item_new_task, null))
            }
        }
        return holder

    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER
            itemCount - 1 -> FOOT
            else -> NORMAL
        }
    }

    private var mTaskEntity: List<TaskEntity>? = taskList

    override fun getItemCount(): Int {
        return if (mTaskEntity!!.isEmpty()) 2 else mTaskEntity!!.size + 2
    }

    fun setOnClickListener(clickListener: OnItemClickListener) {
        onItemClickListener = clickListener

    }

    class ItemViewHolder(item: View) : RecyclerView.ViewHolder(item)


}


