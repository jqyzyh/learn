package jqyzyh.iee.cusomwidget.pullrefreshlistview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by jqyzyh on 2016/10/21.
 * 很吊的下拉刷新控件
 */

public class PullRefreshListView extends ListView implements AbsListView.OnScrollListener {

    static final int STATE_NONE = 0;
    static final int STATE_PULL_TO_REFRESH = 1;//拖动中
    static final int STATE_RELEASE_TO_REFRESH = 2;//释放刷新
    static final int STATE_REFRESHING = 3;//刷新中

    protected ListView mListView;

    private FrameLayout mListHeader;

    private LinearLayout mLoadingHeader;

    private LinearLayout mLoadingFooter;

    private View mLoadMoreStance;

    private ILoadingLayout mLoadingLayout;

    private ILoadingLayout mLoadMoreLayout;

    private int mState = STATE_NONE;

    private boolean moveFirst;

    private float mLastTouchY;

    private int mLoadingHeight;

    private boolean mShouqiing;

    private long mShouqiStartTime;

    private OnRefreshListListener mOnRefreshListListener;

    private ShowLoadingViewRunnable showLoadingViewRunnable = new ShowLoadingViewRunnable();

    private PackUpRunnable packUpRunnable = new PackUpRunnable();

    private AbsListView.OnScrollListener mMyScrollListener;

    public PullRefreshListView(Context context) {
        super(context);
        initView(context);
    }

    public PullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PullRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullRefreshListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        if (mLoadingHeader != null) {
            return;
        }
        mListHeader = new FrameLayout(context);
        mLoadingHeader = new LinearLayout(context);
        mLoadMoreStance = new View(context);
        addHeaderView(mListHeader);
        addHeaderView(mLoadingHeader);
        addFooterView(mLoadMoreStance);
        setLoadingLayout(new RoteLoadingLayout(context));

        super.setOnScrollListener(this);
    }

    public OnRefreshListListener getOnRefreshListListener() {
        return mOnRefreshListListener;
    }

    public void setOnRefreshListListener(OnRefreshListListener onRefreshListListener) {
        this.mOnRefreshListListener = onRefreshListListener;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mMyScrollListener = l;
    }

    /**
     * 移动到第一个了
     */
    void moveFirst() {
        ViewGroup.LayoutParams lp = mLoadMoreStance.getLayoutParams();
        int stanceHeight = 0;
        if (canRefresh()) {
            if (mLoadMoreLayout != null) {
                mLoadMoreLayout.getLoadingLayout().setVisibility(VISIBLE);
                int firstPosition = getFirstVisiblePosition();
                if (firstPosition > 2) {
                    return;
                }
                View firstView = getChildAt(2 - firstPosition);
                View view = getChildAt(getAdapter().getCount() - firstPosition - 3);
                if (firstView == null || view == null) {
                    if (lp.height != 0) {
                        stanceHeight = 0;
                    }
                }else{
                    int height = view.getBottom() - (firstView == null ? 0 : firstView.getTop());
                    if (height < getHeight()) {
                        stanceHeight = getHeight() - height - 40;
                    }
                }
            }
        }else{
            mLoadMoreLayout.getLoadingLayout().setVisibility(GONE);
            stanceHeight = 0;
        }

        if(lp.height != stanceHeight){
            lp.height = stanceHeight;
            mLoadMoreStance.requestLayout();
        }

    }

    boolean checkMoreLayout() {
        if (getAdapter() == null) {
            return false;
        }

        if (mLoadingFooter == null || mLoadMoreLayout == null) {
            return false;
        }

        if (mLoadingFooter.getParent() == null || mLoadingFooter.getHeight() == 0) {
            return false;
        }

        if (mLoadingFooter.getBottom() >= getHeight()) {
            return true;
        }

        return false;
    }

    public void setLoadingLayout(ILoadingLayout loadingLayout) {
        if (loadingLayout == null) {
            return;
        }
        mLoadingHeader.removeAllViews();
        mLoadingLayout = loadingLayout;
        mLoadingHeader.addView(mLoadingLayout.getLoadingLayout(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mLoadingHeader.measure(View.MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().widthPixels, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().heightPixels, ViewGroup.MeasureSpec.AT_MOST));
        mLoadingHeight = mLoadingLayout.getLoadingLayout().getMeasuredHeight();
        MarginLayoutParams lp = (MarginLayoutParams) mLoadingLayout.getLoadingLayout().getLayoutParams();
        lp.topMargin = -mLoadingHeight;
    }

    public void setLoadMoreLayout(ILoadingLayout loadMoreLayout) {
        if (mLoadingFooter == null) {
            mLoadingFooter = new LinearLayout(getContext());
            addFooterView(mLoadingFooter);
        }
        mLoadingFooter.removeAllViews();
        mLoadMoreLayout = loadMoreLayout;
        if (loadMoreLayout == null) {
            return;
        }
        mLoadingFooter.addView(mLoadMoreLayout.getLoadingLayout(), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    public void setAnimHeader(View headerView) {
        mListHeader.removeAllViews();
        mLoadingHeader.addView(headerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    void setState(int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        if (mLoadingLayout != null) {
            mLoadingLayout.setState(mState);
        }
        if (mLoadMoreLayout != null) {
            mLoadMoreLayout.setState(STATE_REFRESHING == mState ? STATE_REFRESHING : STATE_NONE);
        }
    }

    void touchMove(float dy) {
        View loadingView = getLoadingView();

        if (loadingView == null) {
            return;
        }

        MarginLayoutParams lp = (MarginLayoutParams) loadingView.getLayoutParams();
        if (dy > 0) {//向下
            if (!moveFirst) {
                setState(STATE_NONE);
            } else {
                if (lp.topMargin > 0) {
                    setState(STATE_RELEASE_TO_REFRESH);
                    dy = dy * Math.max(0, mLoadingHeight - lp.topMargin) / mLoadingHeight;
                } else {
                    setState(STATE_PULL_TO_REFRESH);
                }
                lp.topMargin += dy;
                mLoadingLayout.offsetY((lp.topMargin + mLoadingHeight) * 1.0f / mLoadingHeight);
                loadingView.requestLayout();
            }
        } else {//向上
            if (lp.topMargin > -mLoadingHeight) {
                if (lp.topMargin < 0) {
                    setState(STATE_PULL_TO_REFRESH);
                }
                lp.topMargin += dy;
                if (lp.topMargin <= -mLoadingHeight) {
                    lp.topMargin = -mLoadingHeight;
                    setState(STATE_NONE);
                }
                loadingView.requestLayout();
                mLoadingLayout.offsetY((lp.topMargin + mLoadingHeight) * 1.0f / mLoadingHeight);
            } else {
                setState(STATE_NONE);
            }
        }
    }

    public void startRefreshing() {
        if (getAdapter() == null) {
            return;
        }

        if (STATE_REFRESHING == mState) {
            return;
        }
        setState(STATE_REFRESHING);
        showLoadingViewRunnable.start();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onRefreshComplate() {
        if (mLoadMoreLayout != null) {
            mLoadMoreLayout.getLoadingLayout().setVisibility(GONE);
            mLoadMoreStance.getLayoutParams().height = 0;
            mLoadMoreStance.requestLayout();
        }
        setState(STATE_PULL_TO_REFRESH);
        resetLoadingView(true);
    }

    void resetLoadingView(boolean anim) {
        View loadingView = getLoadingView();

        if (loadingView == null) {
            return;
        }

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) loadingView.getLayoutParams();
        if (lp.topMargin == -mLoadingHeight) {
            setState(STATE_NONE);
            return;
        }

        if (anim) {
            mShouqiStartTime = System.currentTimeMillis();
            mShouqiing = true;
            packUpRunnable.start();
        } else {
            setState(STATE_NONE);
            lp.topMargin = -mLoadingHeight;
            loadingView.requestLayout();
        }
    }

    private void touchUp() {
        if (STATE_RELEASE_TO_REFRESH == mState) {//释放刷新状态
            refreshList();
            resetLoadingView(true);
            return;
        }
        resetLoadingView(true);
    }

    private View getLoadingView() {
        return mLoadingLayout == null ? null : mLoadingLayout.getLoadingLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getAdapter() == null) {
            return super.dispatchTouchEvent(ev);
        }
        float dy = ev.getY() - mLastTouchY;
        mLastTouchY = ev.getY();

        if (STATE_REFRESHING != mState) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    moveFirst();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(dy);
                    if (STATE_NONE != mState) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    if (STATE_NONE != mState) {
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        super.dispatchTouchEvent(ev);
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    resetLoadingView(true);
                    if (STATE_NONE != mState) {
                        return true;
                    }
                    break;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mMyScrollListener != null) {
            mMyScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view.getChildCount() == 0 || firstVisibleItem > 0) {
            moveFirst = false;
        } else {
            View child = view.getChildAt(0);
            moveFirst = child.getTop() >= 0;
            moveFirst();
        }

        if (STATE_REFRESHING != mState) {
            ListAdapter adapter = getAdapter();
            if (canRefresh() && checkMoreLayout() && mLoadingFooter.getBottom() <= getHeight()) {//加载更多
                if (mOnRefreshListListener != null) {
                    setState(STATE_REFRESHING);
                    mOnRefreshListListener.loadMore(this);
                }
            }
        }

        if (mMyScrollListener != null) {
            mMyScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    void refreshList() {
        if (mOnRefreshListListener != null) {
            setState(STATE_REFRESHING);
            mOnRefreshListListener.onRefresh(this);
        } else {
            onRefreshComplate();
        }
    }

    boolean canRefresh() {
        return getAdapter() != null && getAdapter().getCount() > 4;
    }

    class PackUpRunnable implements Runnable {

        long startTime;
        boolean running;

        public void start() {
            running = true;
            startTime = System.currentTimeMillis();
            post(this);
        }

        public void stop() {
            running = false;
        }

        @Override
        public void run() {
            if (!running) {
                return;
            }

            View loadingView = getLoadingView();
            if (loadingView == null) {
                return;
            }

            long d = System.currentTimeMillis() - startTime;

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) loadingView.getLayoutParams();
            lp.topMargin -= d;

            startTime = System.currentTimeMillis();

            if (STATE_REFRESHING == mState) {
                if (lp.topMargin < 0) {
                    lp.topMargin = 0;
                    running = false;
                }
            } else {
                if (lp.topMargin <= -mLoadingHeight) {
                    setState(STATE_NONE);
                    lp.topMargin = -mLoadingHeight;
                    running = false;
                } else if (lp.topMargin > 0) {
                    setState(STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(STATE_PULL_TO_REFRESH);
                }
            }

            loadingView.requestLayout();

            if (running) {
                post(this);
            }
        }
    }

    class ShowLoadingViewRunnable implements Runnable {

        long startTime;
        boolean running;

        public void start() {
            running = true;
            startTime = System.currentTimeMillis();
            post(this);
        }

        public void stop() {
            running = false;
        }

        @Override
        public void run() {
//            LogUtils.d("", "mylog" + mState);
            if (STATE_REFRESHING != mState) {
                running = false;
            }

            if (!running) {
                return;
            }

            View loadingView = getLoadingView();
            if (loadingView == null) {
                return;
            }

            /*计算移动距离*/
            long d = System.currentTimeMillis() - startTime;

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) loadingView.getLayoutParams();
            lp.topMargin += d;

            startTime = System.currentTimeMillis();

            if (lp.topMargin >= 0) {
                lp.topMargin = 0;
                running = false;
                refreshList();
            }

            loadingView.requestLayout();

            if (running) {
                post(this);
            }
        }
    }

}
