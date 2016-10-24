package jqyzyh.iee.cusomwidget.pullrefreshlistview;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import jqyzyh.iee.cusomwidget.R;

import static android.animation.ValueAnimator.INFINITE;

/**
 * Created by jqyzyh on 2016/10/24.
 * 刷新条
 */

public class RoteLoadingLayout implements ILoadingLayout{
    static final Map<Integer, CharSequence> default_text = new HashMap<>();
    static {
        default_text.put(PullRefreshListView.STATE_NONE, "下拉加载");
        default_text.put(PullRefreshListView.STATE_PULL_TO_REFRESH, "下拉加载");
        default_text.put(PullRefreshListView.STATE_RELEASE_TO_REFRESH, "释放加载");
        default_text.put(PullRefreshListView.STATE_REFRESHING, "正在载入...");
    }

    View mRootView;
    ImageView mIvIcon;
    TextView mTvTextView;

    private int mState;

    private ValueAnimator mLoadingAnimator;

    Map<Integer, CharSequence> mTextMap = new HashMap<>();

    public RoteLoadingLayout(Context context){
        mRootView = View.inflate(context, R.layout.rote_list_header_laoding, null);
        mTvTextView = (TextView) mRootView.findViewById(R.id.tv_list_loading);
        mIvIcon = (ImageView) mRootView.findViewById(R.id.iv_list_loading);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void offsetY(float offsetY) {
        int angle = (int) (360 * offsetY) % 360;
        mIvIcon.setRotation(angle);
    }

    @Override
    public void setState(int state) {
        if(mState == state){
            return;
        }
        mState = state;
        updateState();
    }

    @Override
    public View getLoadingLayout() {
        return mRootView;
    }

    CharSequence getTextByState(int state){
        CharSequence ret = mTextMap.get(state);
        return ret == null ? default_text.get(state) : ret;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void updateState(){
        mTvTextView.setText(getTextByState(mState));
        if(PullRefreshListView.STATE_REFRESHING == mState){
            mLoadingAnimator = ValueAnimator.ofFloat(0, 1);
            mLoadingAnimator.setDuration(1000);
            mLoadingAnimator.setRepeatCount(INFINITE);
            mLoadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    offsetY(animation.getAnimatedFraction());
                }
            });
            mLoadingAnimator.start();
        }else{
            if(mLoadingAnimator != null){
                mLoadingAnimator.cancel();
            }
        }
    }

    public void setStateText(int state, CharSequence text){
        mTextMap.put(state, text);
        mTvTextView.setText(getTextByState(mState));
    }
}
