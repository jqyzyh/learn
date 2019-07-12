package com.jqyzyh.learn;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import jqyzyh.iee.cusomwidget.selector.CanSelectItem;
import jqyzyh.iee.cusomwidget.selector.rolling.RollingSelector;
import jqyzyh.iee.cusomwidget.wheelloop.RollerView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class WheelViewActivity extends AppCompatActivity {
    ArrayList<String> list;

    int initZ = -10;
    SeekBar sbY, sbZ;
    TextView tvY, tvZ;

    SimpleDateFormat format = new SimpleDateFormat("日期 yyyy年MM月dd日");

    class RItem implements RollerView.RollerItem{

        int index;

        public RItem(int index) {
            this.index = index;
        }

        @Override
        public Object getItem() {
            return index;
        }

        @Override
        public String getText() {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, index);
            return format.format(calendar.getTime());
        }

        @Override
        public RollerView.RollerItem last() {
            return new RItem(index - 1);
        }

        @Override
        public RollerView.RollerItem next() {
            return new RItem(index + 1);
        }
    }

    class IntItem implements CanSelectItem<Integer>{
        int value;
        String str;

        public IntItem(int value, String str) {
            this.value = value;
            this.str = str;
        }

        @Override
        public Integer getItem() {
            return value;
        }

        @Override
        public String getText() {
            return value + str;
        }

        @Override
        public CanSelectItem<Integer> last() {
            return new IntItem(value - 1, str);
        }

        @Override
        public CanSelectItem<Integer> next() {
            return new IntItem(value + 1, str);
        }
    }

    class YearItem implements RollerView.RollerItem{

        int year;

        public YearItem(int year) {
            this.year = year;
        }

        @Override
        public Object getItem() {
            return year;
        }

        @Override
        public String getText() {
            return String.valueOf(year) + "年";
        }

        @Override
        public RollerView.RollerItem last() {
            return new YearItem(year - 1);
        }

        @Override
        public RollerView.RollerItem next() {
            return new YearItem(year + 1);
        }
    }

    class MonthItem implements RollerView.RollerItem{

        int month;

        public MonthItem(int month) {
            this.month = month;
        }

        @Override
        public Object getItem() {
            return month;
        }

        @Override
        public String getText() {
            return String.valueOf(month) + "月";
        }

        @Override
        public RollerView.RollerItem last() {
            return new MonthItem(month - 1);
        }

        @Override
        public RollerView.RollerItem next() {
            return new MonthItem(month + 1);
        }
    }

    class DayItem implements RollerView.RollerItem{

        int day;

        public DayItem(int month) {
            this.day = month;
        }

        @Override
        public Object getItem() {
            return day;
        }

        @Override
        public String getText() {
            return String.valueOf(day) + "日";
        }

        @Override
        public RollerView.RollerItem last() {
            return new DayItem(day - 1);
        }

        @Override
        public RollerView.RollerItem next() {
            return new DayItem(day + 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel_view);

        sbY = (SeekBar)findViewById(R.id.sb_y);
        sbZ = (SeekBar)findViewById(R.id.sb_z);
        tvY = (TextView) findViewById(R.id.tv_y);
        tvZ = (TextView) findViewById(R.id.tv_z);
        sbY.setMax(30);
        sbZ.setMax(1000);

//        LoopView loopView = (LoopView) findViewById(R.id.test);

        RollerView rv1 = (RollerView) findViewById(R.id.rv1);
        RollerView rv2 = (RollerView) findViewById(R.id.rv2);
        RollerView rv3 = (RollerView) findViewById(R.id.rv3);

        rv1.setTextSizeSp(20);
        rv1.setRollerItem(new YearItem(2017));
        rv2.setTextSizeSp(20);
        rv2.setRollerItem(new MonthItem(11));
        rv3.setTextSizeSp(20);
        rv3.setRollerItem(new DayItem(2));

        list = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("日期 yyyy年MM月dd日 yyyy年MM月dd日");
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 20; i++) {
            list.add(format.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
//        loopView.setItems(list);
//        loopView.setNotLoop();
//        loopView.setTextSize(14);


        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relay_cotent);
        final MyView myview= new MyView(this);
//        relativeLayout.addView(myview, new RelativeLayout.LayoutParams(800, WRAP_CONTENT));

        sbY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress - 5;
                tvY.setText("y:" + progress);
                myview.setSby(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbZ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress - 500;
                tvZ.setText("z:" + progress);
                myview.setSbz(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lilay_bottom);
        linearLayout.addView(myview, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        RollingSelector rs1 = (RollingSelector) findViewById(R.id.rs_1);
        RollingSelector rs2 = (RollingSelector) findViewById(R.id.rs_2);
        RollingSelector rs3 = (RollingSelector) findViewById(R.id.rs_3);
        rs1.setCanSelect(new IntItem(2019, "年"), null);
        rs2.setCanSelect(new IntItem(7, "月"), null);
        rs3.setCanSelect(new IntItem(12, "日"), null);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(800, 400);
//        lp.topMargin = 450;
//        relativeLayout.addView(new MyView2(this), lp);

    }
    public void click(View v){
        getSupportFragmentManager().beginTransaction().addToBackStack("login").add(R.id.relay_cotent,new LoginFragment(), "login").commit();
        if (true){
            return;
        }
        final WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WRAP_CONTENT;
        params.width = WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
//        params.windowAnimations = com.android.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        final RelativeLayout layout = new RelativeLayout(this);
        TextView tv = new TextView(this);
        tv.setBackgroundColor(Color.RED);
        tv.setText("我是一段测试文字不拉不拉不拉 ");
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(tv, lp);

        wm.addView(layout, params);

        finish();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wm.removeView(layout);
            }
        }, 3000);
    }


    class MyView2 extends View{
        Bitmap bitmap;

        public MyView2(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.canada);
            }
            canvas.drawARGB(0xff, 0, 0xff, 0);

            Camera camera = new Camera();
//            camera.save();
            camera.rotateX(-60);
//            camera.restore();
            int count1 = canvas.save();
            canvas.translate(400, 200);
            int count2 = canvas.save();
            camera.applyToCanvas(canvas);
            canvas.translate(-100, -100);
            canvas.drawBitmap(bitmap, null, new Rect(0, 0, 200, 200), null);
            canvas.restoreToCount(count2);
            canvas.restoreToCount(count1);

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            for (int x = 100;x < getWidth(); x += 100){
                canvas.drawLine(x, 0, x, getHeight(), paint);
            }

            for (int y = 100; y < getHeight(); y += 100){
                canvas.drawLine(0, y, getWidth(), y, paint);
            }
        }
    }

    class MyView extends View{
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect textRect = new Rect();

        int zhouchang;
        double radius;

        public int sby = 15, sbz;

        public void setSby(int sby) {
            this.sby = sby;
            postInvalidate();
        }

        public void setSbz(int sbz) {
            this.sbz = sbz;
            postInvalidate();
        }

        public MyView(Context context) {
            super(context);
            paint.setColor(Color.BLACK);
            paint.setTextSize(context.getResources().getDisplayMetrics().density * 16);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.getTextBounds("星期", 0, 2, textRect);
            zhouchang = textRect.height() * 9;
            radius = zhouchang / Math.PI;
        }

        public MyView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public MyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), textRect.height() * 7);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawARGB(0xff, 0, 0xff, 0);


            for (int i = 0; i < 9; i++) {
                drawText(canvas, i);
            }
        }

        void drawText(Canvas canvas, int index){
            String txt = list.get(index);

            int angle = 90 - (10 + index * 180 / 9);

            int yy = (int)(Math.sin(angle * Math.PI / 180) * radius);

            float zz = paint.getTextSize() * 3.5f;
            Camera camera = new Camera();
            camera.setLocation(0, 0, -sby);
            camera.translate(0, 0, sbz);
            camera.rotateX(angle);
            camera.translate(0, 0, -sbz);
//            camera.translate(0, 0, -paint.getTextSize() * 3);
            int count1 = canvas.save();
            canvas.translate(getWidth() / 2, getHeight() / 2);
            int count2 = canvas.save();
            camera.applyToCanvas(canvas);

            canvas.translate(-getWidth() / 2, -getHeight() / 2);
            canvas.drawText(txt, getWidth() / 2, getHeight() / 2 + textRect.height() / 2, paint);
            canvas.restoreToCount(count2);
            canvas.restoreToCount(count1);

            for (int x = 100;x < getWidth(); x += 100){
                canvas.drawLine(x, 0, x, getHeight(), paint);
            }

            for (int y = 100; y < getHeight(); y += 100){
                canvas.drawLine(0, y, getWidth(), y, paint);
            }

        }
    }
}
