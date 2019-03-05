package com.cgtn.minor.liveminority.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.cgtn.minor.liveminority.mvp.model.TaskEntity


/**
 * created by yf on 2019/1/17.
 */
@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskEntityDB : RoomDatabase() {

    abstract fun getTaskEntityDao(): TaskEntityDao
}
