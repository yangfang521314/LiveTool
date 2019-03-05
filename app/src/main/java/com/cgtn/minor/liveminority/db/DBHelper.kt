package com.cgtn.minor.liveminority.db

import android.arch.persistence.room.Room
import com.cgtn.minor.liveminority.app.LiveApplication

/**
 * created by yf on 2019/1/20.
 */
class DBHelper {
    private var taskEntityDB: TaskEntityDB? = null

    companion object {
        //单利模式
        val mInstance: DBHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DBHelper()
        }
    }

    init {
        taskEntityDB = Room.databaseBuilder(LiveApplication.mInstance!!, TaskEntityDB::class.java, "task.db").build()

    }

    /**
     * 操作数据库dao
     */
    fun getTaskDao(): TaskEntityDao {
        return taskEntityDB!!.getTaskEntityDao()
    }
}