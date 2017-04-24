package jqyzyh.iee.cusomwidget.keyboardlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author jqyzyh
 */
public class KeyboardLayout extends LinearLayout {
    public interface KeyboardPanel{
        void keyboardChange(boolean isShow);
    }


    public KeyboardLayout(Context context) {
        super(context);
    }

    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KeyboardLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        beforehandMeasure(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int mOldHieght = -1;
    private KeyboardPanel mPanel;

    public KeyboardPanel getPanel(){
        if(mPanel == null){
            int len = getChildCount();
            for(int i = 0; i < len; i ++){
                View view = getChildAt(i);
                if(view instanceof KeyboardPanel){
                    mPanel = (KeyboardPanel) view;
                    break;
                }
            }
        }
        return mPanel;
    }

    private void beforehandMeasure(int width, int height){
        if(mOldHieght < 0){
            mOldHieght = height;
            return;
        }

        if(mOldHieght == height){
            return;
        }

        getPanel();

        if(mOldHieght > height){//软键盘弹出
            if(mPanel != null){
                mPanel.keyboardChange(true);
            }
        }else{
            if(mPanel != null){
                mPanel.keyboardChange(false);
            }
        }
        mOldHieght = height;
    }

    public void setPanel(KeyboardPanel panel){
        mPanel = panel;
    }
}
