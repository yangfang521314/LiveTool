package com.young.minor.livetool.utils

import android.content.Context
import android.util.TypedValue

/**
 * created by yf on 2018/7/13.
 */


fun dp2px(dp: Int, context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
}

