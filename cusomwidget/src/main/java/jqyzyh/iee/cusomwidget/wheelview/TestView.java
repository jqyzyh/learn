package jqyzyh.iee.cusomwidget.wheelview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/17.
 */
public class TestView extends LinearLayout {
    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawARGB(255,255,0,0);
        Camera camera = new Camera();
        camera.dotWithNormal(100, 100,100);
//        camera.save();
        camera.rotate(10,10,10);
        camera.translate(100,100,100);
        Matrix matrix = new Matrix();
        camera.getMatrix(matrix);
        canvas.setMatrix(matrix);
        super.dispatchDraw(canvas);
//        camera.restore();
    }
}
