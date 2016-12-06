package jqyzyh.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrFrameLayout;

import jqyzyh.iee.cusomwidget.repetpager.AutoScrollNextViewPager;
import jqyzyh.iee.cusomwidget.repetpager.RepetAdapter;

public class PullPagerActivity extends Activity {

    RecyclerView rv;
    PtrClassicFrameLayout ptr;

    Handler handler = new Handler();

    MyPtrHandler ptrHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_pager);
        rv = (RecyclerView) findViewById(R.id.rv);
        ptr = (PtrClassicFrameLayout) findViewById(R.id.ptr);

        ptrHandler = new MyPtrHandler(){
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptr.refreshComplete();
                    }
                }, 2000);
            }
        };
        ptr.setPtrHandler(ptrHandler);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new MyAdapter());
    }

    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == 1) {
                return new PagerHolder(View.inflate(PullPagerActivity.this, R.layout.layout_pager, null));
            }

            return new TextHolder(View.inflate(PullPagerActivity.this, R.layout.layout_text, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof TextHolder) {
                TextHolder th = (TextHolder) holder;
                th.setText(position);
            }
        }

        @Override
        public int getItemCount() {
            return 30;
        }
    }

    class PagerHolder extends RecyclerView.ViewHolder {

        public PagerHolder(View itemView) {
            super(itemView);
            PAdapter adapter = new PAdapter();
            AutoScrollNextViewPager vp = (AutoScrollNextViewPager) itemView.findViewById(R.id.vp);
            adapter.attachViewPager(vp);
            vp.setOnMyPagerTouchListener(ptrHandler);
        }
    }

    class TextHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public TextHolder(View itemView) {
            super(itemView);
            itemView.setBackgroundColor(Color.WHITE);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }

        public void setText(int position) {
            tv.setText("text--" + position);
        }
    }

    class PAdapter extends RepetAdapter {

        @Override
        public int getRealCount() {
            return 4;
        }

        @Override
        protected Object instantiateRealItem(ViewGroup container, int position) {
            LinearLayout ll = new LinearLayout(PullPagerActivity.this);
            ll.setBackgroundColor(Color.WHITE);
            TextView tv = new TextView(PullPagerActivity.this);
            tv.setTextSize(30);
            tv.setGravity(Gravity.CENTER);
            tv.setText("page" + position);

            ll.addView(tv, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            container.addView(ll);
            return ll;
        }

        @Override
        protected void destroyRealItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
