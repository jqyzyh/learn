package jqyzyh.iee.yuvhandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by yuhang on 2016/7/25.
 */
public class YuvUitls {

    /**
     * NV21格式 Yuv
     * yyyyyyyy
     * uvuv
     * Y = 0.299R + 0.587G + 0.114B
     * U = -0.147R - 0.289G + 0.436B
     * V = 0.615R - 0.515G - 0.100B
     * @param context
     * @param resId
     * @return
     */
    public static byte[] bitmapToYuv(Context context, int resId){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        if(bitmap == null){
            Log.d("mylog", "bitmapToYuv bitmap null");
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        byte[] ret = new byte[width * height * 3 / 2];

        byte[] yBytes = new byte[width * height];
        byte[] uBytes = new byte[width * height];
        byte[] vBytes = new byte[width * height];

        for(int w = 0; w < width; w ++){
            for(int h = 0; h < height; h ++){
                int px = bitmap.getPixel(w, h);
                if((px >> 28) > 0){
                    int r = px & 0xff0000;
                    int g = px & 0xff00;
                    int b = px * 0xff;

                    int y = Math.max(0, Math.min(255, (int) (0.299 * r + 0.587 * g + 0.114 * b)));
                    int u = Math.max(0, Math.min(255, (int) (-0.147 * r - 0.289 * g + 0.436 * b)));
                    int v = Math.max(0, Math.min(255, (int) (0.615 * r - 0.515 * g - 0.100 * b)));

                    yBytes[w * h] = (byte) y;
                    uBytes[w * h] = (byte) u;
                    vBytes[w * h] = (byte) v;
                }
            }
        }

        return null;
    }
}
