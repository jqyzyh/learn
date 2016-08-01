package jqyzyh.iee.yuvhandler;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.FROYO)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bankuai_logo);
            YuvImage yuvImage = new YuvImage(YuvUitls.bitmapToYuv(bitmap), ImageFormat.NV21, bitmap.getWidth(), bitmap.getHeight(), null);
            if(yuvImage!=null){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), 80, stream);
                Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

                ImageView image2 = (ImageView) findViewById(R.id.image2);
                image2.setImageBitmap( bmp);
                stream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
