package com.jqyzyh.learn;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import jqyzyh.iee.cusomwidget.pullrefreshlistview.OnRefreshListListener;
import jqyzyh.iee.cusomwidget.pullrefreshlistview.PullRefreshListView;

public class ListAnimHeaderActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    PullRefreshListView lv;
    private View loadingView;

    private boolean moveFirst;

    private int loadingHeight;

    private Handler mHandler = new Handler();

    long startTime;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(!running){
                return;
            }

            long d = System.currentTimeMillis() - startTime;

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) loadingView.getLayoutParams();
            lp.topMargin -= d;
            if(lp.topMargin <= -loadingHeight){
                lp.topMargin = -loadingHeight;
                running = false;
            }
            loadingView.requestLayout();

            if(running){
                mHandler.post(mRunnable);
            }
        }
    };

    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_anim_header);
        lv = (PullRefreshListView) findViewById(R.id.lv);
        lv.setOnRefreshListListener(new OnRefreshListListener() {
            @Override
            public void onRefresh(PullRefreshListView listView) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lv.onRefreshComplate();
                    }
                }, 2000);
            }
        });
//        lv.addHeaderView(View.inflate(this, R.layout.header_test, null));
        lv.setAdapter(new MAdatper());
//        View l = View.inflate(this, R.layout.height_laoding, null);
//        l.measure(View.MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().widthPixels, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().heightPixels, ViewGroup.MeasureSpec.AT_MOST));
//        loadingHeight = l.getMeasuredHeight();
//        lv.addHeaderView(l);
//        loadingView = l.findViewById(R.id.loading);
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) loadingView.getLayoutParams();
//        lp.topMargin = -loadingHeight;
//        lv.setOnScrollListener(this);
    }

    public void refresh(View view){
        lv.startRefreshing();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view.getChildCount() == 0 || firstVisibleItem > 0) {
            moveFirst = false;
        } else {
            View child = view.getChildAt(0);
            moveFirst = child.getTop() >= 0;
        }
        Log.d("mylog", "onScroll=>" + moveFirst);
    }

    float lastY;

    boolean lastState;

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        float dy = ev.getY() - lastY;
//        lastY = ev.getY();
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                if(touchMove(dy)){
//                    lastState = true;
//                    ev.setAction(MotionEvent.ACTION_UP);
//                    return true;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                shouqi();
//                if(lastState){
//                    return true;
//                }
//                break;
//        }
//
//        if(lastState){
//            lastState = false;
//            parentTouch(ev);
//        }
//
//        return super.dispatchTouchEvent(ev);
//    }


    void shouqi(){
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) loadingView.getLayoutParams();
        if(lp.topMargin == -loadingHeight){
            return;
        }
        startTime = System.currentTimeMillis();
        running = true;
        mHandler.post(mRunnable);
    }

    void parentTouch(MotionEvent ev) {
        if (lv != null) {
            //保存当前事件类型
            int action = ev.getAction();
            //变为canel事件
            ev.setAction(MotionEvent.ACTION_CANCEL);
            super.dispatchTouchEvent(ev);
            //变为down事件
            ev.setAction(MotionEvent.ACTION_DOWN);
            super.dispatchTouchEvent(ev);
            //还原事件类型
            ev.setAction(action);
        }
    }

    boolean touchMove(float dy) {
        boolean flag = true;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) loadingView.getLayoutParams();
        if(dy > 0){//向下
            if (!moveFirst) {
                flag = false;
            }else{
                if(lp.topMargin > 0){
                    dy = dy * Math.max(0, loadingHeight - lp.topMargin) / loadingHeight;
                }
                lp.topMargin += dy;
                loadingView.requestLayout();
            }
        }else{//向上
            if(lp.topMargin > -loadingHeight){
                lp.topMargin += dy;
                if(lp.topMargin <= -loadingHeight){
                    lp.topMargin = -loadingHeight;
                }
                loadingView.requestLayout();
            }else{
                flag = false;
            }
        }
        Log.d("mylog", "touchMove=>" + flag);

        return flag;
    }

    class MAdatper extends BaseAdapter {

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(ListAnimHeaderActivity.this);
            tv.setTextSize(30);
            tv.setPadding(10, 30, 10, 30);
            tv.setText("flksajfslkf" + position);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("mylog", "click=>" + position);
                }
            });
            return tv;
        }
    }
}
