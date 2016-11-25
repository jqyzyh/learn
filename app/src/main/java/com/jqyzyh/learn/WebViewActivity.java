package com.jqyzyh.learn;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import jqyzyh.iee.cusomwidget.mediaplayer.TextureVideoView;
import jqyzyh.iee.cusomwidget.utils.LogUtils;

public class WebViewActivity extends AppCompatActivity {
    static final String LOG_TAG = "WebViewActivity";
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView wv = (WebView) findViewById(R.id.wv);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
        }else{
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {
                    Log.d("mylog", "onReceiveValue==>" + value);
                }
            });
        }
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        wv.getSettings().setJavaScriptEnabled(true);
//        wv.loadUrl("http://bbs.qyzxtest.com.cn/member.php?mod=logging&action=login&app=1&username=aaa&password=oo1234");
        wv.loadUrl("http://www.jindinghui.com.cn/topic/161117xydzp_wap1/?pagetype=share&nat=1&fontsize=m&sub=0");
        wv.setWebChromeClient(new MyWebChromeClient());
        wv.setWebViewClient(new MyWebViewClient());
    }


    public void bofang(View v){
//        TextureVideoView videoView  = (TextureVideoView) findViewById(R.id.tv);
//        videoView.loadUrl(Uri.parse("http://www.pub.demo2016.2000cms.cn/video/003/000/152/00300015265_c3c28e17.mov"));
//        videoView.start();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        LogUtils.d(LOG_TAG, "onTitleChanged");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        LogUtils.d(LOG_TAG, "onConfigurationChanged");
    }

    class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    class MyWebChromeClient extends WebChromeClient{

    }
}
