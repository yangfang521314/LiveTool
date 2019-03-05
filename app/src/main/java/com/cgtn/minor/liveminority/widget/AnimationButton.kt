package com.cgtn.minor.liveminority.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator


/**
 * created by yf on 2019/2/11.
 */
class AnimationButton : View {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs!!, 0)

    constructor(mContext: Context, attrs: AttributeSet, defStyleAttr: Int) : super(mContext, attrs, defStyleAttr) {
        initPaint()

        this.setOnClickListener {
            if (animationButtonListener != null) {
                animationButtonListener!!.onClickListener()
            }
        }
    }

    /**
     * 点击事件及动画事件2完成回调
     */
    private var animationButtonListener: AnimationButtonListener? = null

    fun setAnimationButtonListener(listener: AnimationButtonListener) {
        animationButtonListener = listener
    }


    /**
     * View 的宽度
     */
    private var mWidth: Int = 0
    /**
     * View的高度
     */
    private var mHeight: Int = 0


    //动画完成时间
    private val duration = 1000L
    //圆角画笔
    private lateinit var mCirPaint: Paint
    // move distance
    private var distance = 0

    //default distance
    private var mDefaultInstance = 0

    private var animatorSet: AnimatorSet? = null

    /**
     * 矩形动画到圆形动画
     */
    private var animatorAngle: ValueAnimator? = null
    //圆角的半径变化长度
    private var circleAngle: Int = 0
    //设置矩形的位置
    private var rectF: RectF? = null
    // Angle change to Circle
    private var animatorCircle: ValueAnimator? = null

    private var animatorMoveUp: ObjectAnimator? = null
    //
    private var animatorOK: ValueAnimator? = null

    private var startDrawOk: Boolean = false

    //对路径处理实现动画的效果
    private lateinit var effect: DashPathEffect

    private lateinit var okPaint: Paint
    //获取✔的路径
    private lateinit var path: Path

    //取路径的长度
    private lateinit var pathMeasure: PathMeasure


    private fun initPaint() {
        mCirPaint = Paint()
        mCirPaint.color = Color.RED
        mCirPaint.isAntiAlias = true
        mCirPaint.style = Paint.Style.FILL

        okPaint = Paint()
        okPaint.strokeWidth = 10f
        okPaint.style = Paint.Style.STROKE
        okPaint.isAntiAlias = true
        okPaint.color = Color.WHITE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        mDefaultInstance = (w - h) / 2
        //测量ok的Path
        initOk()
        initAnimation()
    }


    //draw ok
    private fun initOk() {
        path = Path()
        //对勾的路径
        path.moveTo((mDefaultInstance + mHeight / 8 * 3).toFloat(), (mHeight / 2).toFloat())
        path.lineTo((mDefaultInstance + mHeight / 2).toFloat(), (mHeight / 5 * 3).toFloat())
        path.lineTo((mDefaultInstance + mHeight / 3 * 2).toFloat(), (mHeight / 5 * 2).toFloat())

        pathMeasure = PathMeasure(path, true)
    }

    /**
     * 初始化所有动画
     */
    private fun initAnimation() {
        setAngleAnimation()
        setCircleAnimation()
        setMoveUpAnimation()
        setDrawOkAnimation()
        animatorSet = AnimatorSet()
        animatorSet!!
            .play(animatorAngle)
            .with(animatorCircle)
            .before(animatorMoveUp)
            .before(animatorOK)


    }


    /**
     * draw ok View
     */
    private fun setDrawOkAnimation() {
        animatorOK = ValueAnimator.ofInt(1, 0)
        animatorOK!!.duration = duration
        animatorOK!!.addUpdateListener {
            startDrawOk = true

            val value = it.animatedValue as Int

            effect = DashPathEffect(
                floatArrayOf(pathMeasure.length, pathMeasure.length),
                value * pathMeasure.length
            )
            okPaint.pathEffect = effect
            invalidate()
        }
    }

    // move to up the view
    private fun setMoveUpAnimation() {
        val curTranslationY: Float = this.translationY
        animatorMoveUp = ObjectAnimator.ofFloat(this, "translationY", curTranslationY, curTranslationY - 200)
        animatorMoveUp!!.duration = duration
        animatorMoveUp!!.interpolator = AccelerateDecelerateInterpolator()
    }

    private fun setAngleAnimation() {
        animatorAngle = ValueAnimator.ofInt(0, height / 2)
        animatorAngle!!.duration = duration
        animatorAngle!!.addUpdateListener { animation ->
            circleAngle = animation.animatedValue as Int
            invalidate()
        }

    }

    /**
     * 矩形圆角动画转成圆形
     */
    private fun setCircleAnimation() {
        animatorCircle = ValueAnimator.ofInt(0, mDefaultInstance)
        animatorCircle!!.duration = duration
        animatorCircle!!.addUpdateListener {
            distance = it.animatedValue as Int
            invalidate()

        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawCircleAngle(canvas)

        if (startDrawOk) {
            canvas!!.drawPath(path, okPaint)
        }
    }

    /**
     *  draw RoundRect
     */
    private fun drawCircleAngle(canvas: Canvas?) {
        rectF = RectF()
        rectF!!.left = distance.toFloat()
        rectF!!.top = 0f
        rectF!!.right = width.toFloat() - distance
        rectF!!.bottom = height.toFloat()
        canvas!!.drawRoundRect(rectF, circleAngle.toFloat(), circleAngle.toFloat(), mCirPaint)
    }


    /**
     * 启动动画
     */
    fun start() {
        animatorSet!!.start()

    }


    /**
     * 借口回调
     */
    interface AnimationButtonListener {
        /**
         * 按钮点击事件
         */
        fun onClickListener()

        /**
         * 动画完成回调
         */
        fun animationFinish()
    }
}