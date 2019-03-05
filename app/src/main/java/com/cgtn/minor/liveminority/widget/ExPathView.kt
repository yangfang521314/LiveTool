package com.cgtn.minor.liveminority.widget

import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View


/**
 * created by yf on 2019/2/12.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ExPathView : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet!!, 0)
    constructor(mContext: Context, attrs: AttributeSet, defStyleAttr: Int) : super(mContext, attrs, defStyleAttr) {
    }

    private var path: Path = Path()
    private var mPaint = Paint()

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val rectF = RectF()
        rectF.left = 0f
        rectF.top = 0f
        rectF.right = width.toFloat()
        rectF.bottom = height.toFloat()

        val saved = canvas!!.saveLayer(null, null, Canvas.ALL_SAVE_FLAG)

        val xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas!!.drawBitmap(BitmapFactory.decodeResource(resources, com.cgtn.minor.liveminority.R.mipmap.mm)
        ,null,rectF,mPaint)
        mPaint.xfermode = xfermode
        canvas.drawBitmap(BitmapFactory.decodeResource(resources, com.cgtn.minor.liveminority.R.mipmap.quan)
        ,null,rectF,mPaint)
        mPaint.xfermode = null
        canvas.restoreToCount(saved)

    }
}