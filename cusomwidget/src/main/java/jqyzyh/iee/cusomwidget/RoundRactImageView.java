package jqyzyh.iee.cusomwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yuhang on 2016/3/16.
 * 圆角矩形控件
 */
public class RoundRactImageView extends ImageView{
    public static final int TYPE_ALL = 1;
    public static final int TYPE_LEFT = 2;
    public static final int TYPE_RIGHT = 3;
    public static final int TYPE_TOP = 4;
    public static final int TYPE_BOTTOm = 5;
    private Paint mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private BitmapShader mBitmapShader;
    private Matrix mMatrix = new Matrix();
    private RectF mRoundRect = new RectF();
    private int mRadius;
    private int mType = TYPE_ALL;

    public RoundRactImageView(Context context) {
        super(context);
        init(context, null);
    }

    public RoundRactImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundRactImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        if(isInEditMode()){
            mRadius = 10;
            return;
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundRectImageView);
        mRadius = array.getDimensionPixelSize(R.styleable.RoundRectImageView_imageRadius, 10);
        mType = array.getInt(R.styleable.RoundRectImageView_roundType, TYPE_ALL);
        array.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRoundRect.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        setUpdateShader();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        setUpdateShader();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(getDrawable() == null){
            return;
        }

        switch (mType){
            case TYPE_ALL:
                drawAll(canvas);
                break;
            case TYPE_LEFT:
                drawLeft(canvas);
                break;
        }
    }

    private void drawAll(Canvas canvas){
        canvas.drawRoundRect(mRoundRect, mRadius, mRadius, mBitmapPaint);
    }

    private void drawLeft(Canvas canvas){
        Path path = new Path();
        //顺时针画
        path.moveTo(mRoundRect.left + mRadius, mRoundRect.top);//左上开始
        path.lineTo(mRoundRect.right, mRoundRect.top);//移动到右上
        path.lineTo(mRoundRect.right, mRoundRect.bottom);//移动到右下
        path.lineTo(mRoundRect.left + mRadius, mRoundRect.bottom);//移动到左下
        path.arcTo(createRectF(mRoundRect.left, mRoundRect.bottom - mRadius * 2, mRadius * 2, mRadius * 2), 90, 90);//画左下圆角
        path.lineTo(mRoundRect.left, mRoundRect.top + mRadius);//移动到左上
        path.arcTo(createRectF(mRoundRect.left, mRoundRect.top, mRadius * 2, mRadius * 2), 180, 90);//画左上圆角
        path.close();
        canvas.drawPath(path, mBitmapPaint);
    }

    private void drawTop(Canvas canvas){
        Path path = new Path();
        //顺时针画
        path.moveTo(mRoundRect.left + mRadius, mRoundRect.top);//左上开始
        path.lineTo(mRoundRect.right - mRadius, mRoundRect.top);//移动到右上
        path.arcTo(createRectF(mRoundRect.right - mRadius, mRoundRect.top, mRadius * 2, mRadius * 2), -90, 90);//右上圆角
        path.lineTo(mRoundRect.right, mRoundRect.bottom);//移动到右下
        path.lineTo(mRoundRect.left, mRoundRect.bottom);//移动到左下
        path.lineTo(mRoundRect.left, mRoundRect.top + mRadius);//移动到左上
        path.arcTo(createRectF(mRoundRect.left, mRoundRect.top, mRadius * 2, mRadius * 2), 180, 90);//画左上圆角
        path.close();
        canvas.drawPath(path, mBitmapPaint);
    }

    private void setUpdateShader(){
        Drawable drawable = getDrawable();
        if(drawable == null){
            return;
        }
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        Bitmap bm = drawableToBitamp(drawable);
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();
        mBitmapShader = new BitmapShader(bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = Math.max(width * 1.0f / bmWidth, height * 1.0f / bmHeight);
        mMatrix.setScale(scale, scale);
        mMatrix.postTranslate((width - bmWidth * scale) / 2 + getPaddingLeft(), (height - bmHeight * scale) / 2 + getPaddingRight());
        mBitmapShader.setLocalMatrix(mMatrix);
        mBitmapPaint.setShader(mBitmapShader);
    }

    protected Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable db = (BitmapDrawable) drawable;
            return db.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    RectF createRectF(float left, float top, float width, float height){
        return new RectF(left, top, left + width, top + height);
    }
}
