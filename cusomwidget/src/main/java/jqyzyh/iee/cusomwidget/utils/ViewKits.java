package jqyzyh.iee.cusomwidget.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.view.inputmethod.InputMethodManager;

/**
 * @author yuhang
 */

public class ViewKits {
    public static final int[] STATE_NORMAL = {};
    public static final int[] STATE_PRESSED = {android.R.attr.state_enabled, android.R.attr.state_pressed};
    public static final int[] STATE_NOT_ENABLE = {-android.R.attr.state_enabled};

    public static final int dip2Px(Context context, float dip){
        if(dip < 0){
            return (int) (-dip * context.getResources().getDisplayMetrics().density + 0.5f) * -1;
        }
        return (int) (dip * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static ColorStateList createColorState(int colorNormal, int colorSelect){
        int[][] state = {{android.R.attr.state_enabled, android.R.attr.state_pressed}, {-android.R.attr.state_enabled}, {}};
        int[] colors = {colorSelect, colorSelect, colorNormal};
        return new ColorStateList(state, colors);
    }

    public static void hideSoftInput(Activity activity){
        if(activity != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
            }
        }
    }
    /**
     * new float[] {
     * topLeftRadius, topLeftRadius,
     * topRightRadius, topRightRadius,
     * bottomRightRadius, bottomRightRadius,
     * bottomLeftRadius, bottomLeftRadius
     * }
     * @param radius
     * @param normalColor
     * @param pressedColor
     * @param disableColor
     * @return
     */
    public static StateListDrawable createStateListDrawable(float[] radius, int normalColor, int pressedColor, int disableColor){
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(STATE_PRESSED, createGradientDrawable(radius, pressedColor));
        drawable.addState(STATE_NOT_ENABLE, createGradientDrawable(radius, disableColor));
        drawable.addState(STATE_NORMAL, createGradientDrawable(radius, normalColor));
        return drawable;
    }

    /**
     *
     * @param context
     * @param normalRes
     * @param pressedRes
     * @param disableRes
     * @return
     */
    public static StateListDrawable createStateListDrawable(Context context, int normalRes, int pressedRes, int disableRes){
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(STATE_PRESSED, ContextCompat.getDrawable(context, pressedRes));
        drawable.addState(STATE_NOT_ENABLE, ContextCompat.getDrawable(context, disableRes));
        drawable.addState(STATE_NORMAL, ContextCompat.getDrawable(context, normalRes));
        return drawable;
    }

    public static GradientDrawable createGradientDrawable(float[] radius, int color, int strokeWidth, int strokeColor){
        GradientDrawable normal = new GradientDrawable();
        normal.setCornerRadii(radius);
        normal.setColor(color);
        normal.setStroke(strokeWidth, strokeColor);
        return normal;
    }

    public static GradientDrawable createGradientDrawable(float[] radius, int color){
        GradientDrawable normal = new GradientDrawable();
        normal.setCornerRadii(radius);
        normal.setColor(color);
        return normal;
    }
}
