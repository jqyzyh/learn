package com.jqyzyh.learn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        WebView wv = (WebView) findViewById(R.id.wv);
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("http://bbs.qyzxtest.com.cn/member.php?mod=logging&action=login&app=1&username=jqyzyh&password=oo1234");
        wv.setWebChromeClient(new MyWebChromeClient());
    }

    class MyWebChromeClient extends WebChromeClient{

    }
}
