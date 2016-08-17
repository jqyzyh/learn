package com.jqyzyh.learn;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import jqyzyh.iee.cusomwidget.imagehandler.ImageHandler;

public class ImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        overridePendingTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit);

        ViewPager vp= (ViewPager) findViewById(R.id.imageView);
        vp.setAdapter(new ImagesAdapter());
    }

    int[] resids = {R.drawable.image, R.drawable.image1, R.drawable.image2};

    class ImagesAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return resids.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final ImageView imageView = new ImageView(ImageActivity.this);
            imageView.setImageResource(resids[position]);
            ImageHandler imageHandler = new ImageHandler(ImageActivity.this);
            imageHandler.attachImageView(imageView);
            container.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView.setImageResource(resids[(position + 1) % resids.length]);
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
