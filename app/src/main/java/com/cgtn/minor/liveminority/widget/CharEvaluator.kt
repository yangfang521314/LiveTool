package com.cgtn.minor.liveminority.widget

import android.animation.TypeEvaluator


class CharEvaluator : TypeEvaluator<Char> {
    override fun evaluate(fraction: Float, startValue: Char?, endValue: Char?): Char {
        val startInt = startValue!!.toInt()
        val endInt = endValue!!.toInt()
        val curInt = (startInt + fraction * (endInt - startInt)).toInt()
        return curInt.toChar()
    }

}
