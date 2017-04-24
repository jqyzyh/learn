package jqyzyh.iee.cusomwidget.keyboardlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @author jqyzyh
 */
public class KeyboardPanelLayout extends RelativeLayout implements KeyboardLayout.KeyboardPanel {


    public KeyboardPanelLayout(Context context) {
        super(context);
    }

    public KeyboardPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyboardPanelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KeyboardPanelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
           super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mKeyboardShowing){
            setVisibility(GONE);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private boolean mKeyboardShowing;
    private boolean mIsShow;

    @Override
    public void keyboardChange(boolean isShow) {
        mKeyboardShowing = isShow;
        if(!isShow){
            if(mIsShow){
                setVisibility(VISIBLE);
            }else{
                setVisibility(INVISIBLE);
            }
        }else{
            mIsShow = false;
        }

    }

    public void show(){
        mIsShow = true;
        if(!mKeyboardShowing){
            setVisibility(VISIBLE);
        }
    }
}
