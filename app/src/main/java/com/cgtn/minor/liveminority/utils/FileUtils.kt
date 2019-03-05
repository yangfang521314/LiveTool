package com.cgtn.minor.liveminority.utils

import android.content.Context
import android.os.Environment

/**
 * 缓存路径
 */
fun getCachePath(context: Context): String =
    if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
        if (context.externalCacheDir == null) context.cacheDir.path else context.externalCacheDir.path
    } else {
        context.cacheDir.path
    }

