package jqyzyh.iee.cusomwidget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Rachel on 16/6/21.
 */



public class CustomView extends View{


    private String TAG = "CustomView";

    private Paint paintNormal;
    private Paint paintOnTouch;
    private Paint paintInnerCycle;
    private Paint paintLines;
    private Paint paintText;

    private GestureCicle[] cycles;
    private Path linePath = new Path();
    private List<Integer> linedCycles = new ArrayList<Integer>();
    private OnGestureFinishListener onGestureFinishListener;
    private String key = "";
    private int eventX, eventY;
    private boolean canContinue = true;
    private boolean result;
    private Timer timer;

//    private int OUT_CYCLE_NORMAL = Color.rgb(255, 255, 255); // 正常外圆颜色
    private int OUT_CYCLE_NORMAL = Color.rgb(0, 0, 0); // 正常外圆颜色

    private int OUT_CYCLE_ONTOUCH = Color.rgb(025, 066, 103); // 选中外圆颜色
    private int INNER_CYCLE_ONTOUCH = Color.rgb(002, 210, 255); // 选择内圆颜色
    private int LINE_COLOR = Color.argb(127, 255, 210, 255); // 连接线颜色
    private int ERROR_COLOR = Color.argb(127, 255, 000, 000); // 连接错误醒目提示颜色
    private int minCountCycle = 1;
    private boolean isShowPattern = true;
    private CustomViewType type;
    private Activity activity;

    private String firstPwd;

    public enum CustomViewType{

        ADD,UPDATE,ClEAR,CONFIRM
    }

    /**
     * 设置联线错误后，连线的线宽和颜色
     *
     * @param color
     *
     * 线的颜色>=0表示不设置
     */

    public void setErrorColor(int color) {
        if (color < 0) {
            ERROR_COLOR = color;
        }
    }

    /**
     * 设置连线的线宽和颜色
     *
     * @param width
     *            线的宽度<0表示不设置
     * @param color
     *            线的颜色>=0表示不设置
     */
    public void setLinesPaint(float width, int color) {
        setPaintAndColor(width, color, paintLines, LINE_COLOR);

    }

    /**
     * 设置正常情况下，外圆的轮廓线宽和颜色
     *
     * @param width
     *            线的宽度 <0表示不设置
     * @param color
     *            线的颜色>=0表示不设置
     */
    public void setNormalPaint(float width, int color) {
        setPaintAndColor(width, color, paintNormal, OUT_CYCLE_NORMAL);
    }

    /**
     * 设置触摸圆后，外轮廓的线宽和颜色
     *
     * @param width
     *            线的宽度 <0表示不设置
     * @param color
     *            线的颜色>=0表示不设置
     */
    public void setOnTouchPaint(float width, int color) {
        setPaintAndColor(width, color, paintOnTouch, OUT_CYCLE_ONTOUCH);
    }

    /**
     * 设置触摸圆后，内轮廓的线宽和颜色
     *
     * @param width
     *            线的宽度<0表示不设置
     * @param color
     *            线的颜色 >=0表示不设置
     */
    public void setInnerCyclePaint(float width, int color) {
        setPaintAndColor(width, color, paintInnerCycle, INNER_CYCLE_ONTOUCH);
    }

    private void setPaintAndColor(float width, int color, Paint paint,
                                  int changeColor) {
        if (width >= 0) {
            paint.setStrokeWidth(width);
        }
        if (color < 0) {
            if (changeColor == OUT_CYCLE_NORMAL) {
                OUT_CYCLE_NORMAL = color;
            } else if (changeColor == OUT_CYCLE_ONTOUCH) {
                OUT_CYCLE_ONTOUCH = color;
            } else if (changeColor == INNER_CYCLE_ONTOUCH) {
                INNER_CYCLE_ONTOUCH = color;
            } else if (changeColor == LINE_COLOR) {
                LINE_COLOR = color;
            } else if (changeColor == ERROR_COLOR) {
                ERROR_COLOR = color;
            }
        }

    }

    /**
     * 设置连接最小圆的个数
     *
     * @param minCountCycle
     *            圆的个数
     */
    public void setMinCountCycle(int minCountCycle) {
        this.minCountCycle = minCountCycle;
    }

    /**
     * 是否显示连接的线路
     *
     * @param isShowPath
     *            true:显示，false：不显示
     */
    public void setShowPattern(boolean isShowPath) {
        this.isShowPattern = isShowPath;
    }

    public void setOnGestureFinishListener(
            OnGestureFinishListener onGestureFinishListener) {
        this.onGestureFinishListener = onGestureFinishListener;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public interface OnGestureFinishListener {
        public void OnGestureFinish(boolean success);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public CustomView(Context context) {
        super(context);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int perSize = 0;
        if (cycles == null && (perSize = getWidth() / 7 /2) > 0) {
            cycles = new GestureCicle[9];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    GestureCicle cycle = new GestureCicle();
                    cycle.setNum(i * 3 + j);
                    cycle.setOx(perSize * (j * 2 * 2 + 3 ));
                    cycle.setOy(perSize * (i * 2 * 2 + 3 ));
                    cycle.setR(perSize * 1f);
                    cycles[i * 3 + j] = cycle;
                }
            }
        }
    }

    private void initPaint() {
        paintNormal = new Paint();
        paintNormal.setAntiAlias(true);
        paintNormal.setStrokeWidth(3);
        paintNormal.setStyle(Paint.Style.STROKE);

        paintOnTouch = new Paint();
        paintOnTouch.setAntiAlias(true);
        paintOnTouch.setStrokeWidth(3);
        paintOnTouch.setStyle(Paint.Style.STROKE);

        paintInnerCycle = new Paint();
        paintInnerCycle.setAntiAlias(true);
        paintInnerCycle.setStyle(Paint.Style.FILL);

        paintLines = new Paint();
        paintLines.setAntiAlias(true);
        paintLines.setStyle(Paint.Style.STROKE);
        paintLines.setStrokeWidth(6);

        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setTextSize(70);
        paintText.setStrokeWidth(1f);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);




        for (int i = 0; i < cycles.length; i++) {
            if (!canContinue && !result) {
                paintOnTouch.setColor(ERROR_COLOR);
                paintInnerCycle.setColor(ERROR_COLOR);
                paintLines.setColor(ERROR_COLOR);
            } else if (cycles[i].isOnTouch()) {
                paintOnTouch.setColor(OUT_CYCLE_ONTOUCH);
                paintInnerCycle.setColor(INNER_CYCLE_ONTOUCH);
                paintLines.setColor(LINE_COLOR);
            } else {
                paintNormal.setColor(OUT_CYCLE_NORMAL);
                paintInnerCycle.setColor(INNER_CYCLE_ONTOUCH);
                paintLines.setColor(LINE_COLOR);
            }
            if (cycles[i].isOnTouch()) {
                if (!isShowPattern) {
                    canvas.drawCircle(cycles[i].getOx(), cycles[i].getOy(),
                            cycles[i].getR(), paintNormal);
                } else {

                    canvas.drawCircle(cycles[i].getOx(), cycles[i].getOy(),
                            cycles[i].getR(), paintOnTouch);
                    drawInnerBlueCycle(cycles[i], canvas);
                }
            } else {
                canvas.drawCircle(cycles[i].getOx(), cycles[i].getOy(),
                        cycles[i].getR(), paintNormal);
            }
        }


        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        if (!isShowPattern) {
            return;
        }
        linePath.reset();
        if (linedCycles.size() > 0) {
            for (int i = 0; i < linedCycles.size(); i++) {
                int index = linedCycles.get(i);
                float x = cycles[index].getOx();
                float y = cycles[index].getOy();
                if (i == 0) {
                    linePath.moveTo(x, y);
                } else {
                    linePath.lineTo(x, y);
                }
            }
            if (canContinue) {
                linePath.lineTo(eventX, eventY);
            } else {
                linePath.lineTo(
                        cycles[linedCycles.get(linedCycles.size() - 1)].getOx(),
                        cycles[linedCycles.get(linedCycles.size() - 1)].getOy());
            }
            canvas.drawPath(linePath, paintLines);
        }
    }

    private void drawInnerBlueCycle(GestureCicle myCycle, Canvas canvas) {
        canvas.drawCircle(myCycle.getOx(), myCycle.getOy(), myCycle.getR() / 3,
                paintInnerCycle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canContinue) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE: {
                    eventX = (int) event.getX();
                    eventY = (int) event.getY();
                    for (int i = 0; i < cycles.length; i++) {
                        if (cycles[i].isPointIn(eventX, eventY)) {
                            cycles[i].setOnTouch(true);
                            if (!linedCycles.contains(cycles[i].getNum())) {
                                linedCycles.add(cycles[i].getNum());
                            }
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    // 暂停触碰
                    canContinue = false;
                    if (linedCycles.size() >= minCountCycle) {// 大于等于连接圆的最小个数
                        // 检查结果
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < linedCycles.size(); i++) {
                            sb.append(linedCycles.get(i));
                        }

                        if (type == CustomViewType.ADD){


                            if (firstPwd == null){

                                firstPwd = sb.toString();
                                Toast.makeText(activity,"请再绘制一次密码",Toast.LENGTH_SHORT).show();
                                result = true;


                            }else{
                                if (sb.toString().equals(firstPwd)){

                                    result = true;
//                                    SharedPreUtil.put(activity, ParamConst.GESTURE_PWD, sb.toString());
//
//                                    SharedPreUtil.put(activity, ParamConst.GESTURE_IS_OPEN, true);

                                    activity.finish();


                                }else{

                                    result = false;
                                    Toast.makeText(activity,"与上次绘制不一致，请重新绘制",Toast.LENGTH_SHORT).show();

                                }


                            }




                        }else if (type == CustomViewType.UPDATE){

                            result = key.equals(sb.toString());
                            if (result){
                               type = CustomViewType.ADD;

                                if (onGestureFinishListener != null) {
                                    onGestureFinishListener.OnGestureFinish(true);
                                }

                            }else {

                                Toast.makeText(activity,"手势密码错误",Toast.LENGTH_SHORT).show();

                            }



                        }else if (type == CustomViewType.ClEAR){
                            result = key.equals(sb.toString());
                            if (result) {
//                                SharedPreUtil.remove(activity, ParamConst.GESTURE_PWD);
                                Toast.makeText(activity,"手势密码清除成功",Toast.LENGTH_SHORT).show();
                                activity.finish();



                            }else {
                                Toast.makeText(activity,"手势密码错误",Toast.LENGTH_SHORT).show();


                            }


                        }else {
                            result = key.equals(sb.toString());


                            if (onGestureFinishListener != null) {
                                onGestureFinishListener.OnGestureFinish(result);
                            }


                        }


                    } else {
                        Toast.makeText(getContext(), "最少连接" + minCountCycle + "个圆",
                                Toast.LENGTH_SHORT).show();
                    }
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // 还原
                            eventX = eventY = 0;
                            for (int i = 0; i < cycles.length; i++) {
                                cycles[i].setOnTouch(false);
                            }
                            linedCycles.clear();
                            linePath.reset();
                            canContinue = true;
                            postInvalidate();
                        }
                    }, 1000);
                    break;
                }
            }
            invalidate();
        }
        return true;
    }


    public CustomViewType getType() {
        return type;
    }

    public void setType(CustomViewType type) {
        this.type = type;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }



}
