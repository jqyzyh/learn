package com.jqyzyh.learn;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class WebViewActivity extends AppCompatActivity {

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
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("http://bbs.qyzxtest.com.cn/member.php?mod=logging&action=login&app=1&username=aaa&password=oo1234");
        wv.setWebChromeClient(new MyWebChromeClient());
        wv.setWebViewClient(new MyWebViewClient());
    }


    public void bofang(View v){
        TextureVideoView videoView  = (TextureVideoView) findViewById(R.id.tv);
        videoView.loadUrl(Uri.parse("http://www.pub.demo2016.2000cms.cn/video/003/000/152/00300015265_c3c28e17.mov"));
        videoView.start();
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
