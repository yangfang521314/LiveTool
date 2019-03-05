package com.cgtn.minor.liveminority.widget;

import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * created by yf on 2019/2/12.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PathView extends View {
    public PathView(Context context) {
        super(context);
    }

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    Paint paint = new Paint();
    Path path = new Path();
    private float mProgress= 0;
    private float mMaxProgress = 100;
    private int mValue;

//    {
//        path.addArc(200, 200, 400, 400, -225, 225);
//        path.arcTo(400, 200, 600, 400, -180, 225, false);
//        path.lineTo(400, 542);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(mValue);
        paint.setStrokeWidth(30f);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
////        canvas.drawPath(path, paint);
        RectF mRectF = new RectF();
        mRectF.left = 100;
        mRectF.top= 100;
        mRectF.right = 400;
        mRectF.bottom = 400;
        String text = (int) mProgress + "%";
        canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, paint);
        paint.reset();
        paint.setStrokeWidth(2f);
        paint.setAntiAlias(true);
        paint.setColor(mValue);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(50);
        canvas.drawText(text,250,250,paint);

    }

    public void setProgress(float progress){
        mProgress = progress;
        invalidate();
    }

    public void setColor(int value){
        mValue = value;
    }

}
