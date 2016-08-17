package jqyzyh.iee.cusomwidget.wheelview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by jqyzyh on 2016/8/17.
 */
public class WheelView extends View implements GestureDetector.OnGestureListener {

    private TextPaint mPaint;
    private DataWraper mDataWraper;

    private Paint mMask = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Rect mTextSizeRect = new Rect();

    private GestureDetector gestureDetector;

    private int centerY = -1;

    private float margin;

    public WheelView(Context context) {
        super(context);
        init();
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WheelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    void init(){
        if(mPaint != null){
            return;
        }
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(20 * getResources().getDisplayMetrics().density);
        mPaint.setTextAlign(Paint.Align.CENTER);
        gestureDetector = new GestureDetector(getContext(), this);
        margin = 5 * getResources().getDisplayMetrics().density;
    }

    public void setDataWraper(DataWraper dataWraper) {
        this.mDataWraper = dataWraper;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mDataWraper == null){
            return;
        }


        int width = getWidth();
        int height = getHeight();

        canvas.drawLine(0, height / 2, width, height/ 2, mPaint);

        if(centerY < 0){
            centerY = height / 2;
        }

        mPaint.getTextBounds("年", 0, 1, mTextSizeRect);

        float lastYs = centerY;
        float lastYx = centerY;
        for(int i = 0; i < 5; i ++){
            if(i > 0){
                lastYs = onDrawItem(canvas, -i, lastYs);
            }
            lastYx = onDrawItem(canvas, i, lastYx);
        }
    }


    private float onDrawItem(Canvas canvas, int index, float lastY){
        int width = getWidth();
        int height = getHeight();

        int center = height / 2;

        Log.d("mylog", "onDrawItem==>" + index + "," + lastY + "," + center);

        float marginY = lastY;


//        if(index != 0){
//            if(index > 0){
//                marginY = lastY + mTextSizeRect.height() / 2 + margin;
//            }else if(index < 0){
//                marginY = lastY - mTextSizeRect.height() / 2 - margin;
//            }
//
//            float offset = marginY - center;
//            float locOffset =  offset * 1.0f / center;
//            float scaleY = 1 - Math.abs(locOffset) * 0.5f;
//            scaleY *= scaleY;
//
//            if(index > 0){
//                lastY += scaleY * margin;
//            }else if(index < 0){
//                lastY -= scaleY * margin;
//            }
//        }


        float y = centerY + index * mTextSizeRect.height();

        float offset = y - center;

        float locOffset =  offset * 1.0f / center;
        float scaleX = 1 - Math.abs(locOffset) * 0.02f;
        float scaleY = 1 - Math.abs(locOffset) * 0.5f;
        scaleY *= scaleY;

        float textHeight = mTextSizeRect.height() * scaleY;
        Log.d("mylog", "onDrawItem textHeight==>" + textHeight);

        y = lastY + (index == 0 ? 0 : textHeight * (index < 0 ? -1 : 1));

        String text = (String)mDataWraper.getData(index);

        int saveCount = canvas.save();
//        y = (int) (center + offset *  (1 - Math.abs(locOffset)));
        canvas.scale(scaleX, scaleY, width / 2, y);
        canvas.drawText(text, 0, text.length(), width / 2, y + textHeight / 2, mPaint);
        canvas.restoreToCount(saveCount);


//        if(index > 0){
//            y += margin * scaleY;
//        }else {
//            y -= marginY * scaleY;
//        }

        return y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        centerY = (int) (centerY - distanceY);
        if(Math.abs(centerY - getHeight() / 2) < mTextSizeRect.height()){

        }
        postInvalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
