package jqyzyh.iee.cusomwidget.selector.rolling;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.TypedValue;
import android.view.View.MeasureSpec;

import jqyzyh.iee.cusomwidget.selector.CanSelectItem;
import jqyzyh.iee.cusomwidget.wheelloop.RollerView;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * 仿照ios的样式
 * @author jqyzyh on 2019/7/12
 */
public class LikeIOSSkin implements RollingSkin{
    Context context;
    RollingSelector rollView;

    private Paint mTextPaint;//文字画笔
    private Paint mPaintIndicator;//线的画笔

    private int mTextHeight;//文字高度

    private int mPaperHeight;//平面的高度
    private float initZ;//摄像机Z初始位置
    private float mCameraTranslateZ;//摄像机Z轴偏移 mTextHeight * 3
    private float middleScale = 1.15f;//中间文字放大的比例

    private int mWindowCenterX;

    private RollerView.OnItemSelectorListener mItemSelectorListener;
    private Paint.FontMetrics mFontMetrics;//计算字体绘制区域

    public LikeIOSSkin(RollingSelector rollView) {
        this.rollView = rollView;
        this.context = rollView.getContext();
        init(context);
    }

    private void init(Context context){
        //绘制参数
        initZ = -TypedValue.applyDimension(COMPLEX_UNIT_DIP, 7, context.getResources().getDisplayMetrics());
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(0xff333333);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(TypedValue.applyDimension(COMPLEX_UNIT_DIP, 18, context.getResources().getDisplayMetrics()));
        mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintIndicator.setColor(0xffc5c5c5);
        measureText();
    }

    @Override
    public int measureHeight(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST) {
            return Math.min(mTextHeight * 7, measureHeight);
        }
        return 0;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int[] location = new int[2];
        rollView.getLocationInWindow(location);
        mWindowCenterX = context.getResources().getDisplayMetrics().widthPixels / 2 - location[0];
    }

    void measureText() {
        mFontMetrics = mTextPaint.getFontMetrics();
        Rect rect = new Rect();
        mTextPaint.getTextBounds("方块", 0, 2, rect);
        mTextHeight = rect.height();

        mPaperHeight = mTextHeight * 9;
        mCameraTranslateZ = mTextHeight * 4F;
    }

    float paperOffsetY;

    @Override
    public void draw(Canvas canvas, CanSelectItem item, float paperOffsetY) {
        CanSelectItem curItem = rollView.getCurSelectItem();
        if (curItem == null) {
            return;
        }
        this.paperOffsetY = paperOffsetY;

        int width = rollView.getWidth();
        int height = rollView.getHeight();

        int saveCount;
        float centerHeight = mTextHeight * 1.5F;
        float lineY = height / 2 - centerHeight / 2;

        mTextPaint.setColor(0xff999999);
        //画上边
        saveCount = canvas.save();
        canvas.clipRect(0, 0, width, lineY);
        if (paperOffsetY <= 0) {
            drawText(canvas, curItem.getText(), mPaperHeight / 2, mTextPaint);
        }
        CanSelectItem temp;
        temp = curItem;
        for (int i = 0; i < 4; i++) {
            temp = temp.last();
            if (temp == null) {
                break;
            }
            drawText(canvas, temp.getText(), mPaperHeight / 2 - (i + 1) * mTextHeight, mTextPaint);
        }
        canvas.restoreToCount(saveCount);

        //画下边
        saveCount = canvas.save();
        canvas.clipRect(0, lineY + centerHeight, width, height);
        if (paperOffsetY > 0) {
            drawText(canvas, curItem.getText(), mPaperHeight / 2, mTextPaint);
        }
        temp = curItem;
        for (int i = 0; i < 4; i++) {
            temp = temp.next();
            if (temp == null) {
                break;
            }

            drawText(canvas, temp.getText(), mPaperHeight / 2 + (i + 1) * mTextHeight, mTextPaint);
        }
        canvas.restoreToCount(saveCount);

        //画中间
        mTextPaint.setColor(0xff333333);
        saveCount = canvas.save();
        canvas.clipRect(0, lineY, width, lineY + centerHeight);
        if (paperOffsetY < 0) {//向上滚动了 画下边的第一项
            temp = curItem.next();
            if (temp != null) {
                drawText(canvas, temp.getText(), mPaperHeight / 2 + mTextHeight, mTextPaint, middleScale);
            }
        } else if (paperOffsetY > 0) {//向下滚动了 画上边的第一项
            temp = curItem.last();
            if (temp != null) {
                drawText(canvas, temp.getText(), mPaperHeight / 2 - mTextHeight, mTextPaint, middleScale);
            }
        }
        drawText(canvas, curItem.getText(), mPaperHeight / 2, mTextPaint, middleScale);
        canvas.restoreToCount(saveCount);
//
        canvas.drawLine(0, lineY, width, lineY, mPaintIndicator);
        canvas.drawLine(0, lineY + centerHeight, width, lineY + centerHeight, mPaintIndicator);
    }

    private void drawText(Canvas canvas, String text, float inPaperY, Paint paint){
        drawText(canvas, text, inPaperY, paint, 1);
    }
    /**
     * 绘制文字
     *
     * @param canvas   画布
     * @param text     要绘制的文字
     * @param inPaperY 绘制未在在平面中的位置
     * @param paint    画笔
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void drawText(Canvas canvas, String text, float inPaperY, Paint paint, float scale) {
        inPaperY += paperOffsetY;
        float angle = 90 - (inPaperY * 180 / mPaperHeight);
        if (angle >= 90 || angle <= -90) {
            return;
        }

        Camera camera = new Camera();
        camera.setLocation(0, 0, initZ);
        //下面三行代码可以实现滚轮的效果，需要发挥一些想象力
        camera.translate(0, 0, mCameraTranslateZ);
        camera.rotateX(angle);
        camera.translate(0, 0, -mCameraTranslateZ);
        int saveTranslate = canvas.save();
        canvas.translate(mWindowCenterX, rollView.getHeight() / 2);
        int saveCamera = canvas.save();
        camera.applyToCanvas(canvas);
        canvas.translate(-mWindowCenterX + rollView.getWidth() / 2, 0);
        if (scale != 1) {
            canvas.scale(scale, scale);
        }
        canvas.drawText(text, 0, getTextBaseLine(0), paint);

        canvas.restoreToCount(saveCamera);
        canvas.restoreToCount(saveTranslate);
    }

    float getTextBaseLine(float y) {
        return RollingSelector.getTextBaseLine(y, mFontMetrics);
    }

    @Override
    public int getLineHeight() {
        return mTextHeight;
    }
}
