package com.cgtn.minor.liveminority.ui.adapter

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.cgtn.minor.liveminority.R
import com.cgtn.minor.liveminority.db.DBHelper
import com.cgtn.minor.liveminority.mvp.model.TaskEntity
import com.cgtn.minor.liveminority.utils.toast
import com.cgtn.minor.liveminority.widget.OnItemClickListener
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

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (getItemViewType(position)) {
            HEADER -> {
                with(holder.itemView) {
                    task_number.text = "ID"
                    task_url.text = "标题"
                    task_choice.visibility = View.INVISIBLE
                }
            }
            FOOT -> {
                with(holder.itemView) {
                    task_create_url.requestFocus()
                    task_create_url.isFocusable = true
                    task_new_choice.setOnClickListener {
                        if (preView != null) {
                            preView!!.setImageResource(R.mipmap.choice_not_address)

                        }
                        preView = task_new_choice
                        task_new_choice.setImageResource(R.mipmap.choice_address)
                        if (!TextUtils.isEmpty(task_create_url.text.toString())) {
                            onItemClickListener!!.onClickListener(
                                holder.itemView,
                                task_create_url.text.toString().trim().replace("", "")
                            )
                            Log.d("tag",task_create_url.text.toString())
                            //添加进入数据库
                            Completable.complete()
                                .observeOn(Schedulers.io())
                                .subscribe {
                                    DBHelper.mInstance.getTaskDao()
                                        .addTaskData(TaskEntity(position - 1, task_create_url.text.toString()))
                                }
                        } else {
                            toast(context, "没有填入新的推流地址")
                        }
                    }
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

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemViewHolder {
        var holder: RecyclerView.ViewHolder? = null
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


