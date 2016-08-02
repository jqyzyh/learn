package jqyzyh.iee.cusomwidget.canvasview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yuhang on 2016/8/1.
 */
public class CanvasView extends View{

    private PathHandler mPathHandler;

    public CanvasView(Context context) {
        super(context);
        init(context);
    }

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        if(mPathHandler != null){
            return;
        }
        mPathHandler = new PathHandler();
        mPathHandler.attach(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPathHandler.drawPath(canvas);
    }

    public Bitmap createBitmap(){
        return mPathHandler.getCanvasBitmap();
    }

    public void clearPath(){
        mPathHandler.clearCanvas();
    }
}
