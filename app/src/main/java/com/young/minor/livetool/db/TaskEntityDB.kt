package com.young.minor.livetool.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.young.minor.livetool.mvp.model.TaskEntity


/**
 * created by yf on 2019/1/17.
 */
@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskEntityDB : RoomDatabase() {

    abstract fun getTaskEntityDao(): TaskEntityDao
}
