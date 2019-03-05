package com.cgtn.minor.liveminority.mvp.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * created by yf on 2019/1/16.
 *  id 任务序列号
 *  title 推流地址
 */
@Entity(tableName = "task")
data class TaskEntity(@PrimaryKey var id: Int, val title: String)