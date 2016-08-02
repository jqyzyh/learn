package jqyzyh.iee.cusomwidget.canvasview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import jqyzyh.iee.cusomwidget.utils.UIKit;

/**
 * Created by yuhang on 2016/8/1.
 */
public class PathHandler implements View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener{
    static final String LOG_TAG = "PathHandler";

    static final int MASK_ALPHE = 0xff000000;
    static final int MASK_RED = 0xff0000;
    static final int MASK_GREEN = 0xff00;
    static final int MASK_BLUE = 0xff;

    static final int BITMAP_WIDTH = 480;
    static final int BITMAP_HEIGHT = 480;

    private static final float TOUCH_TOLERANCE = 4;

    private Bitmap mResultBitmap;
    private Canvas mBitmapCanvas;

    private int mBackgroundColor = Color.WHITE;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);

    private Path mPath = new Path();

    private View mAttechView;

    private float mLastX, mLastY;

    private int mBitmapWidth;
    private int mBitmapHeight;


    public PathHandler(){
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath.setFillType(Path.FillType.EVEN_ODD);
    }

    public void attach(View attechView){
        if(mAttechView != null){
            mAttechView.setOnClickListener(null);
            UIKit.removeOnGlobalLayoutListener(mAttechView, this);
        }
        mAttechView = null;
        clearCanvas();
        mAttechView = attechView;
        if(mAttechView != null){
            mAttechView.setOnTouchListener(this);
            UIKit.addOnGlobalLayoutListener(mAttechView, this);
        }
    }

    private void touchDown(float x, float y){
        mPath.moveTo(x, y);
        mLastX = x;
        mLastY = y;
    }

    private void touchMove(float x, float y){
        mPath.lineTo(x, y);
        mAttechView.invalidate();
//        float dx = Math.abs(x - mLastX);
//        float dy = Math.abs(y - mLastY);
//        if(dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
//            LogUtils.d(LOG_TAG, "touchMove==>" + x + "," + y + "," + mLastX + "," + mLastY );
//            mPath.quadTo(x, y, (mLastX + x) / 2, (mLastY + y) / 2);
//            mLastX = x;
//            mLastY = y;
//            mAttechView.invalidate();
//        }
    }

    private void touchUp(float x, float y){
        mPath.lineTo(x, y);
        if(mBitmapCanvas != null){
            mBitmapCanvas.drawPath(mPath, mPaint);
        }
        mPath.reset();
        mAttechView.invalidate();
    }

    Bitmap resetBitmap(){
        if(mResultBitmap != null){
            mResultBitmap.recycle();
        }

        mResultBitmap = null;
        mBitmapCanvas = null;

        if(mAttechView != null){
            mResultBitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.RGB_565);
            mBitmapCanvas = new Canvas(mResultBitmap);
            mBitmapCanvas.drawARGB((mBackgroundColor & MASK_ALPHE) >> 24, (mBackgroundColor & MASK_RED) >> 16, (mBackgroundColor & MASK_GREEN) >> 8, mBackgroundColor & MASK_BLUE);
        }
        return mResultBitmap;
    }

    public Bitmap getCanvasBitmap(){
        if(mAttechView == null){
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(255, 255, 255, 255);

        int width = mAttechView.getWidth();
        int height = mAttechView.getHeight();

        float scale = Math.min(BITMAP_WIDTH * 1.0f / width, BITMAP_HEIGHT * 1.0f / height);

        canvas.save();
        width = (int) (width * scale);
        height = (int) (height * scale);
        canvas.translate((BITMAP_WIDTH - width) / 2, (BITMAP_HEIGHT - height) / 2);
        canvas.scale(scale, scale);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        return bitmap;
    }

    public void clearCanvas(){
        mPath.reset();

        if(mResultBitmap != null){
            mResultBitmap.recycle();
        }

        resetBitmap();

        if(mAttechView != null){
            mAttechView.invalidate();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mResultBitmap == null){
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchUp(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
        }

        return true;
    }

    public void drawPath(Canvas canvas) {
        canvas.drawBitmap(mResultBitmap, 0, 0, mPaint);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void onGlobalLayout() {
        int width = mAttechView.getWidth();
        int height = mAttechView.getHeight();
        mBitmapWidth = width;
        mBitmapHeight = height;
        if(width >=0 && height >=0 && mResultBitmap == null){
            resetBitmap();
        }
    }
}
