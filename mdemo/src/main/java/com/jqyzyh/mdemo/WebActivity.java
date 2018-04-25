package com.jqyzyh.mdemo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView wv = (WebView)findViewById(R.id.webview);

        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);
//        wv.loadUrl("http://bbs.qyzxtest.com.cn/member.php?mod=logging&action=login&app=1&username=aaa&password=oo1234");
//        wv.loadUrl("http://www.jindinghui.com.cn/topic/161117xydzp_wap1/?pagetype=share&nat=1&fontsize=m&sub=0");
        wv.setWebChromeClient(new WebChromeClient());
        wv.setWebViewClient(new WebViewClient());
//        wv.loadUrl("file:///android_asset/test.html");
        wv.loadUrl("http://m.toutiao.com/profile/56818696770/");
    }
}
