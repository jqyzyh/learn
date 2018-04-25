package com.jqyzyh.learn;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
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

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

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
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setMediaPlaybackRequiresUserGesture(true);
//        wv.loadUrl("http://bbs.qyzxtest.com.cn/member.php?mod=logging&action=login&app=1&username=aaa&password=oo1234");
//        wv.loadUrl("http://www.jindinghui.com.cn/topic/161117xydzp_wap1/?pagetype=share&nat=1&fontsize=m&sub=0");
        wv.setWebChromeClient(new MyWebChromeClient());
        wv.setWebViewClient(new MyWebViewClient());
        wv.loadUrl("file:///android_asset/newswebshare_vthree2.html");
//        wv.loadUrl("http://10.0.251.109:7070/pub/appcloud_jin/androidweb/www/tsw/app/video/test/index.html?appId=6&newsId=030247817&sourceAppId=b2c5d21f-6b35-4d3a-bf7e-74efeee9ee91&nat=1&clientType=ANDROID&appId=6&version=v1.0&showImgs=1&devId=9581443d-4c1e-4503-84e2-22925cdae997&fontSize=1&userAppId=b2c5d21f-6b35-4d3a-bf7e-74efeee9ee91&apiVersion=2&userId=fc444413-42a6-4467-97fb-d978a7969a83");
//        wv.loadUrl("http://m.toutiao.com/profile/56818696770/");
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
            LogUtils.d("mylog", "shouldOverrideUrlLoading==>"+url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            LogUtils.d("mylog", "onLoadResource==>"+url);
        }

        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            LogUtils.d("mylog", "onFormResubmission");
            super.onFormResubmission(view, dontResend, resend);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            LogUtils.d("mylog", "onReceivedError");
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            LogUtils.d("mylog", "onReceivedHttpError");
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            LogUtils.d("mylog", "onPageCommitVisible");
            super.onPageCommitVisible(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            LogUtils.d("mylog", "onPageFinished");
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
            LogUtils.d("mylog", "onReceivedClientCertRequest");
            super.onReceivedClientCertRequest(view, request);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            LogUtils.d("mylog", "onReceivedHttpAuthRequest");
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        @Override
        public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
            LogUtils.d("mylog", "onReceivedLoginRequest");
            super.onReceivedLoginRequest(view, realm, account, args);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            LogUtils.d("mylog", "onReceivedSslError");
            super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            LogUtils.d("mylog", "onScaleChanged");
            super.onScaleChanged(view, oldScale, newScale);
        }

        @Override
        public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
            LogUtils.d("mylog", "onUnhandledKeyEvent");
            super.onUnhandledKeyEvent(view, event);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            LogUtils.d("mylog", "onUnhandledKeyEvent");
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
            LogUtils.d("mylog", "onTooManyRedirects");
            super.onTooManyRedirects(view, cancelMsg, continueMsg);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            LogUtils.d("mylog", "shouldOverrideUrlLoading");
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            LogUtils.d("mylog", "doUpdateVisitedHistory==>" + url);
            super.doUpdateVisitedHistory(view, url, isReload);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            LogUtils.d("mylog", "shouldInterceptRequest 2==>" + request.getUrl());
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            LogUtils.d("mylog", "shouldOverrideKeyEvent");
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            LogUtils.d("mylog", "shouldInterceptRequest 1==>" + url);
            return super.shouldInterceptRequest(view, url);
        }
    }

    class MyWebChromeClient extends WebChromeClient{
        View videoFullView;
        int requestedOrientation;
        @Override
        public Bitmap getDefaultVideoPoster() {
            Drawable drawable = ContextCompat.getDrawable(WebViewActivity.this, R.drawable.canada);
            if (drawable instanceof BitmapDrawable){
                return ((BitmapDrawable) drawable).getBitmap();
            }
            return super.getDefaultVideoPoster();
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            LogUtils.e("mylog", "onShowCustomView 2");
            LogUtils.e("mylog", "onShowCustomView 2  " + (view.getParent() == null ? null : view.getParent().getClass().getName()));
            requestedOrientation = getRequestedOrientation();
            setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
            videoFullView = view;
            ViewGroup vg = (ViewGroup) getWindow().getDecorView();
            vg.addView(videoFullView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            super.onShowCustomView(view, requestedOrientation, callback);
            LogUtils.e("mylog", "onShowCustomView 1");
        }

        @Override
        public void onHideCustomView() {
            if (videoFullView != null && videoFullView.getParent() != null) {
                ((ViewGroup)videoFullView.getParent()).removeView(videoFullView);
                setRequestedOrientation(requestedOrientation);
                videoFullView = null;
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.e("onConsoleMessage", "message:" + consoleMessage.message());
            return true;
        }
    }
}
