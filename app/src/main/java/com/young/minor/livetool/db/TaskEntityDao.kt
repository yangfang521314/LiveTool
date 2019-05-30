package com.young.minor.livetool.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.young.minor.livetool.mvp.model.TaskEntity

/**
 * created by yf on 2019/1/17.
 */
@Dao
interface TaskEntityDao {
    /**
     * 添加信息
     */
    @Insert
    fun addTaskData(taskEntity: TaskEntity)

    @Update
    fun updateTaskData(taskEntity: TaskEntity)

    @Query("select * from TASK")
    fun getTaskData(): List<TaskEntity>

}