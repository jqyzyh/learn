package jqyzyh.iee.cusomwidget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * 一个仿ios loading框的动画
 *
 * @author yuhang on 4/24/2019
 */
public class IOSChrysanthemum extends View {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    int petalsWidth, petalsHeight;
    int radius;
    Rect drawRect = new Rect();

    int startRotate;

    long startTime;

    boolean inited;
    int chrysanthemumColor = 0x666666;

    public IOSChrysanthemum(Context context) {
        super(context);
        init(context, null);
    }

    public IOSChrysanthemum(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IOSChrysanthemum(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IOSChrysanthemum(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    void init(Context context, AttributeSet attrs) {
        if (inited) {
            return;
        }
        inited = true;

        if (attrs == null) {
            return;
        }

        TypedArray ary = context.obtainStyledAttributes(attrs, R.styleable.IOSChrysanthemum);
        chrysanthemumColor = ary.getColor(R.styleable.IOSChrysanthemum_chrysanthemumColor, chrysanthemumColor) & 0x00ffffff;
        ary.recycle();
    }


    public final int dip2Px(Context context, float dip) {
        float f = TypedValue.applyDimension(COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        final int res = (int) ((f >= 0) ? (f + 0.5f) : (f - 0.5f));
        if (res != 0) return res;
        if (dip == 0) return 0;
        if (dip > 0) return 1;
        return res;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //目的都是保证是方块的
        if (widthMode == MeasureSpec.AT_MOST) {//宽高都自适应
            if (heightMode == MeasureSpec.AT_MOST) {//都是自适应有默认尺寸
                setMeasuredDimension(dip2Px(getContext(), 36) + getPaddingLeft() + getPaddingRight()
                        , dip2Px(getContext(), 36) + getPaddingTop() + getPaddingBottom());
                return;
            } else if (heightMode == MeasureSpec.EXACTLY) {//高度固定值，宽最大值比高大时 宽设置成=高
                int width = widthSize - getPaddingLeft() - getPaddingRight();
                int height = heightSize - getPaddingTop() - getPaddingBottom();
                if (width > height) {
                    setMeasuredDimension(height + getPaddingLeft() + getPaddingRight()
                            , heightSize);
                } else {
                    setMeasuredDimension(widthSize
                            , heightSize);
                }
                return;
            }
        } else if (widthMode == MeasureSpec.EXACTLY) {
            if (heightMode == MeasureSpec.AT_MOST) {
                int width = widthSize - getPaddingLeft() - getPaddingRight();
                int height = heightSize - getPaddingTop() - getPaddingBottom();
                if (height > width) {
                    setMeasuredDimension(widthSize
                            , widthSize + getPaddingTop() + getPaddingBottom());
                } else {
                    setMeasuredDimension(widthSize
                            , heightSize);
                }
                return;
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w == oldw && h == oldh) {
            return;
        }

        int width = w - getPaddingLeft() - getPaddingRight();
        int height = h - getPaddingTop() - getPaddingBottom();
        radius = Math.min(width, height) / 2;
        int minRadius = (int) (radius * 0.4);
        drawRect.set(w / 2 - radius, h / 2 - radius, w / 2 + radius, h / 2 + radius);
        petalsHeight = radius - minRadius - 1;
        petalsWidth = (int) (minRadius * Math.PI * 2 / 12) - 1;//内圈周长
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int saveCount = canvas.save();
        canvas.translate(drawRect.left, drawRect.top);

        canvas.rotate(startRotate, radius, radius);
        RectF rect = new RectF(radius - petalsWidth / 2, 0, radius + petalsWidth / 2, petalsHeight);
        for (int i = 0; i < 12; i++) {
            canvas.rotate(30, radius, radius);
            int a = 0x30 + 0xcf * i / 12;
            paint.setColor(chrysanthemumColor | (a << 24));
            canvas.drawRoundRect(rect, petalsWidth, petalsWidth, paint);
        }

        canvas.restoreToCount(saveCount);

        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

        startRotate = 30 * (int) (((System.currentTimeMillis() - startTime) / 70) % 12);
        postInvalidateDelayed(16);
    }
}
