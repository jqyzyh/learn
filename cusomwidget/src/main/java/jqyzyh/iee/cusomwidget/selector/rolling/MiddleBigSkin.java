package jqyzyh.iee.cusomwidget.selector.rolling;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;

import jqyzyh.iee.cusomwidget.R;
import jqyzyh.iee.cusomwidget.selector.CanSelectItem;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * 中间放大
 *
 * @author jqyzyh on 2019/7/12
 */
public class MiddleBigSkin implements RollingSkin {
    RollingSelector rollView;
    Context context;

    private Paint mTextPaint;//文字画笔
    private Paint.FontMetrics mFontMetrics;
    private int mRollerNum = 3;

    private float mTextSpace;

    private float mTextHeight;//字高
    private float mLineHeight;//行高
    private float mPaperHeight;//页面高度

    private int width;
    private int height;

    public MiddleBigSkin(RollingSelector rollView, TypedArray ary) {
        this.rollView = rollView;
        this.context = rollView.getContext();
        init(context, ary);
    }

    private void init(Context context, TypedArray ary) {
        int textColor = 0xff333333;
        float textSize = TypedValue.applyDimension(COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        mTextSpace = TypedValue.applyDimension(COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        if (ary != null) {
            textColor = ary.getColor(R.styleable.RollingSelector_rollerTextColor, textColor);
            textSize = ary.getDimension(R.styleable.RollingSelector_rollerTextSize, textSize);
            mTextSpace = ary.getDimension(R.styleable.RollingSelector_rollerLineSpace, mTextSpace);
            mRollerNum = ary.getInt(R.styleable.RollingSelector_rollerNum, mRollerNum);
            ary.recycle();
        }


        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(textSize);
        measureText();
    }

    @Override
    public int measureHeight(int widthMeasureSpec, int heightMeasureSpec) {
        return (int) mPaperHeight;
    }

    void measureText() {
        mFontMetrics = mTextPaint.getFontMetrics();
        Rect rect = new Rect();
        mTextPaint.getTextBounds("方块", 0, 2, rect);
        mTextHeight = rect.height();
        mLineHeight = mTextHeight + mTextSpace;

        mPaperHeight = mLineHeight * (2 * mRollerNum + 1);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        width = rollView.getWidth() - rollView.getPaddingLeft() - rollView.getPaddingRight();
        height = rollView.getHeight() - rollView.getPaddingTop() - rollView.getPaddingBottom();
    }

    float mPaperOffsetY;
    @Override
    public void draw(Canvas canvas, CanSelectItem item, float paperOffsetY) {
        if (item == null) {
            return;
        }
        mPaperOffsetY = paperOffsetY;
        int saveCount = canvas.save();
        canvas.translate(0, (height - mPaperHeight) / 2);

        //画选中的
        drawText(canvas, item.getText(), mPaperHeight / 2, mTextPaint);

        CanSelectItem temp;
        //画上边
        temp = item;
        for (int i = 0; i < mRollerNum + 1; i++) {
            temp = temp.last();
            if (temp == null) {
                break;
            }
            drawText(canvas, temp.getText(), mPaperHeight / 2 - (i + 1) * mLineHeight, mTextPaint);
        }

        //画下边
        temp = item;
        for (int i = 0; i < mRollerNum + 1; i++) {
            temp = temp.next();
            if (temp == null) {
                break;
            }

            drawText(canvas, temp.getText(), mPaperHeight / 2 + (i + 1) * mLineHeight, mTextPaint);
        }
        canvas.restoreToCount(saveCount);
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
    void drawText(Canvas canvas, String text, float inPaperY, Paint paint) {
        inPaperY += mPaperOffsetY;

        float ts = paint.getTextSize();
        float juli = Math.abs(inPaperY - mPaperHeight / 2);
        int saveCount = canvas.save();
        int width = this.width / 2;
        if (juli < mLineHeight) {
            float scale = ((mLineHeight - juli) / mLineHeight) * 0.5f + 1;
//            paint.setTextSize(ts * scale);
//            canvas.drawText(text, width, getTextBaseLine(inPaperY, paint.getFontMetrics()), paint);
//            paint.setTextSize(ts);

            canvas.translate(width, inPaperY);
            canvas.scale(scale, scale);
            canvas.drawText(text, 0, getTextBaseLine(0), mTextPaint);
        } else {
            canvas.drawText(text, width, getTextBaseLine(inPaperY), paint);
        }


        canvas.restoreToCount(saveCount);
    }

    float getTextBaseLine(float y) {
        return RollingSelector.getTextBaseLine(y, mFontMetrics);
    }

    @Override
    public int getLineHeight() {
        return (int) mLineHeight;
    }
}
