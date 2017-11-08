package jqyzyh.iee.cusomwidget.iospupopmenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;

import jqyzyh.iee.cusomwidget.R;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

/**
 * @author yuhang on 2016/8/19.
 */
public class BottomPopup {
    public interface OnDismissPopupListener{
        void onDismiss(BottomPopup popup);
    }

    private WeakReference<Activity> mActivity;

    private View mRootView;
    private View mContentView;

    private boolean isShowing;
    private boolean isDismissing;

    private OnDismissPopupListener dismissPopupListener;

    public BottomPopup(Activity activity){
        this.mActivity = new WeakReference<>(activity);
    }


    public void setContentView(View contentView){
        mContentView = contentView;
    }

    Activity getActivity(){
        return this.mActivity == null ? null : this.mActivity.get();
    }

    View createMenuView(LayoutInflater inflater){
        RelativeLayout rootView = new MyRelativeLayout(inflater.getContext());
        rootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.viewfinder_mask));

        RelativeLayout.LayoutParams rLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rootView.addView(mContentView, rLp);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return rootView;
    }

    public OnDismissPopupListener getDismissPopupListener() {
        return dismissPopupListener;
    }

    public void setDismissPopupListener(OnDismissPopupListener dismissPopupListener) {
        this.dismissPopupListener = dismissPopupListener;
    }

    public void show(){
        if(mContentView == null){
            return;
        }

        if(mRootView != null){
            return;
        }

        Activity activity = getActivity();
        if(activity == null){
            return;
        }

        isShowing = true;

        mRootView = createMenuView(LayoutInflater.from(activity));

        final View view = mContentView;

        view.setVisibility(View.INVISIBLE);

        final Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(250);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShowing = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            layoutParams.systemUiVisibility = lp.systemUiVisibility;
        }

        if((lp.flags & FLAG_TRANSLUCENT_STATUS) == FLAG_TRANSLUCENT_STATUS){
            layoutParams.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            layoutParams.flags |= FLAG_TRANSLUCENT_STATUS;
        }
        if((lp.flags & FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) == FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS){
            layoutParams.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            layoutParams.flags |= FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        activity.getWindowManager().addView(mRootView, layoutParams);

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.post(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(animation);
            }
        });
    }

    public void dismiss(){
        if(isDismissing || isShowing){
            return;
        }


        Activity activity = getActivity();
        if(activity != null && mRootView != null && mRootView.getParent() != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(mRootView.getWindowToken(), 0);
            }
            isDismissing = true;
            Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
            animation.setDuration(250);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    justDismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mContentView.startAnimation(animation);
        }
    }

    public void justDismiss(){
        Activity activity = getActivity();
        if(activity != null && mRootView != null && mRootView.getParent() != null){
            activity.getWindowManager().removeView(mRootView);
            if (dismissPopupListener != null){
                dismissPopupListener.onDismiss(this);
            }
            mRootView = null;
        }
    }

    class MyRelativeLayout extends RelativeLayout{

        public MyRelativeLayout(Context context) {
            super(context);
        }


        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                dismiss();
                return true;
            }else{
                return super.dispatchKeyEvent(event);
            }
        }
    }
}
