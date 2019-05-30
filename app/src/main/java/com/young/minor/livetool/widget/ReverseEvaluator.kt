package com.young.minor.livetool.widget

import android.animation.TypeEvaluator
import com.young.minor.livetool.utils.LogUtil


/**
 * created by yf on 2019/2/9.
 */
class ReverseEvaluator:TypeEvaluator<Int>{
    override fun evaluate(fraction: Float, startValue: Int?, endValue: Int?): Int {
        LogUtil.e(fraction)
        return (endValue!! - fraction * (endValue - startValue!!)).toInt()
    }

}