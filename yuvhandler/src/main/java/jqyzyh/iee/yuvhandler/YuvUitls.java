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
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToYuv(Bitmap bitmap){
        if(bitmap == null){
            Log.d("mylog", "bitmapToYuv bitmap null");
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d("mylog", "bitmap=>" + width + "," + height);
        width += width % 2;
        height += height % 2;

        byte[] yBytes = new byte[width * height];
        byte[] uBytes = new byte[yBytes.length];
        byte[] vBytes = new byte[yBytes.length];

        byte[] ret = new byte[yBytes.length + yBytes.length / 2];

        for(int w = 0; w < width; w ++){
            for(int h = 0; h < height; h ++){
                if(w >= bitmap.getWidth() || h >= bitmap.getHeight()){
                    continue;
                }
                int px = bitmap.getPixel(w, h);
                int index = h * width + w;
                if((px >> 28) > 0){
                    int r = (px & 0xff0000) >> 16;
                    int g = (px & 0xff00) >> 8;
                    int b = px & 0xff;

                    Log.d("mylog", "RGB: "+r + "," + g + "," + b);

                    int y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                    int u =  (int) (-0.147 * r - 0.289 * g + 0.436 * b);
                    int v = (int) (0.615 * r - 0.515 * g - 0.100 * b);



                    yBytes[index] = (byte) (0.299 * r + 0.587 * g + 0.114 * b);
                    uBytes[index] = (byte) (-0.147 * r - 0.289 * g + 0.436 * b);
                    vBytes[index] = (byte) (0.615 * r - 0.515 * g - 0.100 * b);

                    Log.d("mylog", w + "," + h + "  YUV: "+ yBytes[index] + "," + uBytes[index] + "," + vBytes[index]);
                }else{
                    int r = 0xff;
                    int g = 0xff;
                    int b = 0xff;
                    uBytes[index] = (byte) (-0.147 * r - 0.289 * g + 0.436 * b);
                    vBytes[index] = (byte) (0.615 * r - 0.515 * g - 0.100 * b);
                }
            }
        }


        int u = 0;
        int v = 0;

       for(int h = 0; h < height / 2; h ++){
           for(int w = 0; w < width / 2; w ++){
               int offset = h * 2 * width + w * 2;



               u = uBytes[offset];
               v = vBytes[offset];

               Log.d("mylog",  (w * 2)+ "," + (h * 2)  + "uv=>"+u + ","+v);

               offset = yBytes.length + h * height / 2 + w;
               ret[offset] = (byte) (u >> 2);
               ret[offset + 1] = (byte) (v >> 2);
           }
       }


        for(int i = 0; i < yBytes.length; i ++){
            ret[i] = yBytes[i];

        }

        return ret;
    }


    /**
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToYuv444(Bitmap bitmap){
        if(bitmap == null){
            Log.d("mylog", "bitmapToYuv bitmap null");
            return null;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        width += width % 2;
        height += height % 2;

        byte[] yBytes = new byte[width * height];
        byte[] uBytes = new byte[yBytes.length];
        byte[] vBytes = new byte[yBytes.length];

        byte[] ret = new byte[yBytes.length * 3];

        for(int w = 0; w < width; w ++){
            for(int h = 0; h < height; h ++){
                if(w >= bitmap.getWidth() || h >= bitmap.getHeight()){
                    continue;
                }
                int px = bitmap.getPixel(w, h);
                if((px >> 28) > 0){
                    int r = (px & 0xff0000) >> 16;
                    int g = (px & 0xff00) >> 8;
                    int b = px & 0xff;

                    Log.d("mylog", "RGB: "+r + "," + g + "," + b);

                    int y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                    int u =  (int) (-0.147 * r - 0.289 * g + 0.436 * b);
                    int v = (int) (0.615 * r - 0.515 * g - 0.100 * b);


                    int index = h * width + w;

                    ret[index * 3] = (byte) (0.299 * r + 0.587 * g + 0.114 * b);
                    ret[index * 3 + 1] = (byte) (-0.147 * r - 0.289 * g + 0.436 * b);
                    ret[index * 3 + 2] = (byte) (0.615 * r - 0.515 * g - 0.100 * b);

                    Log.d("mylog", w + "," + h + "  YUV: "+ yBytes[index] + "," + uBytes[index] + "," + vBytes[index]);
                }
            }
        }

        return ret;
    }
}
