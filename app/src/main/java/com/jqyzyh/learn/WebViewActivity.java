package com.jqyzyh.learn;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jqyzyh.iee.cusomwidget.mediaplayer.TextureVideoView;
import jqyzyh.iee.cusomwidget.utils.LogUtils;

public class WebViewActivity extends AppCompatActivity {
    static final String LOG_TAG = "WebViewActivity";
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        String html = "<div></div> <p>　　和平区少儿图书馆积极与各校园图书馆合作，试点“通借通还”，充分利用少儿图书馆内的图书资源，为儿童们营造更好、更便捷的阅读环境。</p> <div> <p></p> <center><img height=\"400px\" src=\"http://www.pub.ec.hubtest.enorth.com.cn/pic/003/000/326/00300032602_14b45f8d.jpg\" width=\"600px\" /></center> <center>四平东道小学图书馆内新增了1000余册和平区少儿图书馆的书籍</center> </div> <p>　　3月3日，“前沿”新闻记者在和平区四平东道小学内看到，校园图书馆内新增了1000余册来自和平区少儿图书馆的书籍，学生们凭借和平区少儿图书馆的借阅证，就可以在这里借阅图书，而在少儿图书馆里借阅的图书也可以在学校内归还。</p> <p>　　学生张瑷廸刚刚借阅了一本连环画册，他告诉“前沿”新闻记者：“原来我们借书需要走到离家比较远的少儿图书馆，现在学校就能借书、还书了，真的挺方便。”</p> <div> <p></p> <center><img height=\"400px\" src=\"http://www.pub.ec.hubtest.enorth.com.cn/pic/003/000/326/00300032603_53f80ee2.jpg\" width=\"600px\" /></center> <center>同学们在学校借还图书</center> </div> <p>　　和平区四平东道小学德育主任倪妮表示：“校园图书馆在充实了少儿图书馆的1000多册图书以后，前来借书的学生也多了起来，校园里充满了浓郁的阅读氛围。”</p> <p>　　据了解，目前和平区少儿图书馆在天津市第二南开中学、天津市汇文中学与和平区四平东道小学三所学校启动了“通借通还”的试点工作，并力争在今年年底前，使全区各中、小学校全部实现“通借通还”。</p>";



        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(getAssets().open("test1.html"));
            NodeList nodeList = (NodeList) document.getElementsByTagName("img");
            int len = nodeList.getLength();
            for(int i = 0; i < len; i ++){
                Node element = nodeList.item(i);
                LogUtils.d("mylog", "element==>" + element.getClass().getName());
                NamedNodeMap namedNodeMap = element.getAttributes();
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
//        wv.loadUrl("http://www.jindinghui.com.cn/topic/161117xydzp_wap1/?pagetype=share&nat=1&fontsize=m&sub=0");
        wv.loadUrl("file:///android_asset/test.html");
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

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }

    class MyWebChromeClient extends WebChromeClient{
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }
    }
}
