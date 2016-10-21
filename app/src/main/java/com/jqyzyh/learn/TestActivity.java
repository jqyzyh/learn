package com.jqyzyh.learn;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jqyzyh.rvex.HeaderRecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TestActivity extends AppCompatActivity {

    HeaderRecyclerView rv;

    MA mAdpater;

    private int offset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        rv = (HeaderRecyclerView) findViewById(R.id.rv);
        rv.getRecyclerView().setLayoutManager(new My(this));
        rv.getRecyclerView().setAdapter(mAdpater = new MA());
        rv.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("mylog", "onScrolled==>" + recyclerView.getScaleY());
            }
        });
    }


    class My extends LinearLayoutManager{

        public My(Context context) {
            super(context);
        }

        public My(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public My(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
            Log.d("mylog", "scrollVerticallyBy dy==>" + dy);
            int ret =  super.scrollVerticallyBy(dy, recycler, state);

            Log.d("mylog", "scrollVerticallyBy ret==>" + ret);
//            if(dy < 0){
//                if(ret == 0){
//                    offset -= dy;
//                    LinearLayout v = (LinearLayout) rv.getChildAt(0);
//                    View vv = v.getChildAt(0);
//                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vv.getLayoutParams();
//                    lp.topMargin = Math.min(-100 + offset, 0);
//                }
//            }else{
//                if(offset > 0){
//                    offset -= dy;
//                    LinearLayout v = (LinearLayout) rv.getChildAt(0);
//                    View vv = v.getChildAt(0);
//                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vv.getLayoutParams();
//                    lp.topMargin = Math.min(-100 + offset, 0);
//                }
//            }
            return ret;
        }

        @Override
        public RecyclerView.LayoutParams generateDefaultLayoutParams() {
            return super.generateDefaultLayoutParams();
        }
    }

    public static class MyB extends CoordinatorLayout.Behavior<RecyclerView>{

    }


    class MA extends RecyclerView.Adapter<Holder>{


        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 1 : 0;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout ll = new LinearLayout(TestActivity.this);
            Holder ret;
            if(viewType == 1){
                ret = new HeaderHodler(ll);
            }else{
                ret = new Holder(ll);
            }
            return ret;
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.tv.setText("文本" + position);
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView tv;
        public Holder(View itemView) {
            super(itemView);
            tv = new TextView(TestActivity.this);
            tv.setPadding(10, 50, 10,50);
            tv.setTextSize(30);
            LinearLayout ll = (LinearLayout) itemView;
            ll.addView(tv);
        }
    }


    class HeaderHodler extends Holder{


        public HeaderHodler(View itemView) {
            super(itemView);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tv.getLayoutParams();
            lp.topMargin = Math.min(-100 + offset, 0);

        }
    }

    Runnable runnable;

    public void click(View view) {
        if (runnable != null) {
            return;
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream is = null;
                try {
                    Log.d("mylog", "start=========>" + System.currentTimeMillis());
                    URL url = new URL("http://demo.enorth.cn:9000/gov_open/imgcode");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(10 * 60000);

                    connection.setConnectTimeout(10 * 60000);
                    connection.connect();
                    Log.d("mylog", "connect=========>" + System.currentTimeMillis());

                    is = connection.getInputStream();
                    byte[] buffer = new byte[8 * 1024];
                    int len;
                    while((len = is.read(buffer)) != -1){
                        Log.d("mylog", "download=========>" + System.currentTimeMillis());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(is != null){
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(connection != null){
                        connection.disconnect();
                    }
                }


                runnable = null;
            }
        };


        new Thread(runnable).start();
    }


}
