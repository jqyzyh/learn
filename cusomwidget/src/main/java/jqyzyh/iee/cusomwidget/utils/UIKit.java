package jqyzyh.iee.cusomwidget.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by jqyzyh on 2016/8/2.
 */
public class UIKit {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void removeOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener){
        if(view != null && view.getViewTreeObserver().isAlive()){
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
            }else{
                view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
            }
        }
    }

    public static void addOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener){
        if(view != null && view.getViewTreeObserver().isAlive()){
            view.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        }
    }
}
