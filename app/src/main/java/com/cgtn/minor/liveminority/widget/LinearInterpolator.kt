package com.cgtn.minor.liveminority.widget

import android.view.animation.Interpolator

/**
 * created by yf on 2019/2/8.
 */
class LinearInterpolator : Interpolator{
    override fun getInterpolation(input: Float): Float {
        return input
    }

}