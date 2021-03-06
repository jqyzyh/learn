package jqyzyh.iee.cusomwidget.iospupopmenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import jqyzyh.iee.cusomwidget.R;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

/**
 * Created by jqyzyh on 2016/8/4.
 */
public class IOSPupopMenu {

    private WeakReference<Activity> mActivity;

    private View mRootView;

    private List<MenuItem> mMenuItems = new ArrayList<>();

    private String mTitleText;

    private boolean isShowing;
    private boolean isDismissing;

    private BottomPopup.OnDismissPopupListener dismissPopupListener;

    public IOSPupopMenu(Activity activity){
        this.mActivity = new WeakReference<>(activity);
    }

    Activity getActivity(){
        return this.mActivity == null ? null : this.mActivity.get();
    }


    public void setDismissPopupListener(BottomPopup.OnDismissPopupListener dismissPopupListener) {
        this.dismissPopupListener = dismissPopupListener;
    }

    /**
     * 添加菜单项
     * @param text 菜单文字
     * @param color 菜单文字颜色
     * @param onClickListener 菜单按钮点击事件
     */
    public void addMenu(CharSequence text, int color, View.OnClickListener onClickListener){
        mMenuItems.add(new MenuItem(text, color, onClickListener));
    }

    public void addMenu(MenuItem item){
        mMenuItems.add(item);
    }

    public void setTitleText(String titleText){
        mTitleText = titleText;
    }

    public void show(){

        if(mRootView != null){
            return;
        }

        Activity activity = getActivity();
        if(activity == null){
            return;
        }

        isShowing = true;

        mRootView = createMenuView(LayoutInflater.from(activity));

        final View view = mRootView.findViewById(R.id.lilay_menu);

        view.setVisibility(View.INVISIBLE);

        final Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(300);
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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        view.post(new Runnable() {
            @Override
            public void run() {
                view.startAnimation(animation);
            }
        });
    }

    View createMenuView(LayoutInflater inflater){
        RelativeLayout rootView = new MyRelativeLayout(inflater.getContext());
        rootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.viewfinder_mask));

        View view = inflater.inflate(R.layout.layout_ios_popup_menu, null);
        RelativeLayout.LayoutParams rLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rootView.addView(view, rLp);

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        if(!TextUtils.isEmpty(mTitleText)){
            tvTitle.setText(mTitleText);
        }

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.lilay_button);

        View cancel = view.findViewById(R.id.tv_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView textView = null;
        for(final MenuItem item : mMenuItems){
            View line = new View(inflater.getContext());
            line.setBackgroundColor(Color.argb(0x80, 0x88, 0x88, 0x88));
            layout.addView(line, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));

            textView = new TextView(inflater.getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(15);
            textView.setTextColor(item.color);
            textView.setText(item.text);
            textView.setSingleLine();
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if(item.onClickListener != null){
                        item.onClickListener.onClick(v);
                    }
                }
            });
            textView.setBackgroundResource(R.drawable.seletor_ios_popup_button);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getActivity().getResources().getDimensionPixelSize(R.dimen.ios_menu_height_button));
//            lp.topMargin = 1;
            layout.addView(textView, lp);
        }

        if(textView != null){
            textView.setBackgroundResource(R.drawable.seletor_ios_popup_bottom);
        }

        return rootView;
    }

    public void dismiss(){
        if(isDismissing || isShowing){
            return;
        }
        Activity activity = getActivity();
        if(activity != null && mRootView != null && mRootView.getParent() != null){
            isDismissing = true;
            View view = mRootView.findViewById(R.id.lilay_menu);
            Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
            animation.setDuration(300);
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
            view.startAnimation(animation);
        }
    }

    public void justDismiss(){
        Activity activity = getActivity();
        if(activity != null && mRootView != null && mRootView.getParent() != null){
            if(dismissPopupListener != null){
                dismissPopupListener.onDismiss(null);
            }
            activity.getWindowManager().removeView(mRootView);
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
