package com.bwie.test.day03demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ASUS on 2017/11/2.
 */

public class MyToggleButton extends View implements View.OnClickListener{
    /** 做为背景的图片 **/
    private Bitmap backgroundBitmap;
    /** 可以滑动的图片 **/
    private Bitmap slideButtonBitmap;
    private Paint paint;

    /** 滑动按钮的左边距 **/
    private float slideButtonLeft;

    /** 当前开关的状态,true为开,false为关 **/
    private boolean currentToggleSate;

    /** down 事件时的x值 **/
    private int firstX;
    /** touch 事件时上一个x值 **/
    private int lastX;

    /** 判断是否发生拖动,如果拖动了,则不响应onClick事件 **/
    private boolean isDrag;

    public MyToggleButton(Context context) {
        super(context);
    }

    public MyToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    private void initView() {
        backgroundBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.aaa);
        slideButtonBitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.bb);

        paint = new Paint();
        paint.setAntiAlias(true);
        setOnClickListener(this);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置当前View的大小,以背景图为大小,单位都是像素
        setMeasuredDimension(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight());
    }

    /**
     * 确定位置的时候调用此方法，自定义View的时候作用不大
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 先绘制背景图
        canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
        // 再绘制滑动按钮
        canvas.drawBitmap(slideButtonBitmap, slideButtonLeft, 0, paint);
    }

    @Override
    public void onClick(View v) {
        if (isDrag) {
            // 如果发生了拖动,则不响应点击事件了
            return;
        }

        currentToggleSate = !currentToggleSate;
        flushState();
    }

    /**
     * 刷新当前状态
     */
    private void flushState() {
        if (currentToggleSate) {
            slideButtonLeft = backgroundBitmap.getWidth()
                    - slideButtonBitmap.getWidth();
        } else {
            slideButtonLeft = 0;
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstX = lastX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int currentX = (int) event.getX();
                // 算出移动的距离
                int moveDistance = currentX - lastX;
                // 并把当前的x值缓存起来,但计算下次移动的距离
                lastX = currentX;
                // 然后根据移动位置来动态改变slideButtonLeft
                slideButtonLeft = slideButtonLeft + moveDistance;
                break;
            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    int maxLeft = backgroundBitmap.getWidth()
                            - slideButtonBitmap.getWidth();
                    // 根据slideButtonLeft来判断当前应该是什么状态(开,关)
                    if (slideButtonLeft > maxLeft / 2) {
                        // 开状态
                        currentToggleSate = true;
                    } else {
                        currentToggleSate = false;
                    }

                    flushState();
                }

                break;
        }
        flushView();

        return true;
    }

    private void flushView() {
        // 对slideButtonLeft的值进行判断,确保滑动时只能在合理的范围内:0<=slideButtonLeft<=maxleft
        int maxLeft = backgroundBitmap.getWidth()
                - slideButtonBitmap.getWidth();
        // 确保slideButtonLeft>=0
        slideButtonLeft = slideButtonLeft > 0 ? slideButtonLeft : 0;
        // 确保slideButtonLeft<=maxleft
        slideButtonLeft = slideButtonLeft < maxLeft ? slideButtonLeft : maxLeft;
        invalidate();
    }



}
