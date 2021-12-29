/*
SlimSocial for Twitter is an Open Source app realized by Leonardo Rignanese
 GNU GENERAL PUBLIC LICENSE  Version 2, June 1991
*/

package it.rignanese.leo.sliminstagram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;

import im.delight.android.webview.AdvancedWebView;


public class MainActivity extends Activity implements AdvancedWebView.Listener {

    // Main webView
    private AdvancedWebView webViewInsta;

    // Object to show full screen videos
    private WebChromeClient myWebChromeClient;
    private FrameLayout mTargetView;
    private FrameLayout mContentView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private View mCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Setup the webView
        webViewInsta = findViewById(R.id.webView);
        webViewInsta.setListener(this, this);
        webViewInsta.addPermittedHostname("www.instagram.com");
        webViewInsta.addPermittedHostname("instagram.com");

        // Full screen video
        myWebChromeClient = new WebChromeClient() {
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                mCustomViewCallback = callback;
                mTargetView.addView(view);
                mCustomView = view;
                mContentView.setVisibility(View.GONE);
                mTargetView.setVisibility(View.VISIBLE);
                mTargetView.bringToFront();
            }

            @Override
            public void onHideCustomView() {
                if (mCustomView == null)
                    return;

                mCustomView.setVisibility(View.GONE);
                mTargetView.removeView(mCustomView);
                mCustomView = null;
                mTargetView.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                mContentView.setVisibility(View.VISIBLE);
            }
        };

        webViewInsta.setWebChromeClient(myWebChromeClient);
        mContentView = findViewById(R.id.main_content);
        mTargetView = findViewById(R.id.target_view);

        // Load homepage
        webViewInsta.loadUrl(getString(R.string.urlInstagram));
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        webViewInsta.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        webViewInsta.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.e("Info", "onDestroy()");
        webViewInsta.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        webViewInsta.onActivityResult(requestCode, resultCode, intent);
    }

    //*********************** WebView methods ****************************

    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) { }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        String summary =
                "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /></head><body><h1 "
                        + "style='text-align:center; padding-top:15%;'>"
                        + getString(R.string.titleNoConnection)
                        + "</h1> <h3 style='text-align:center; padding-top:1%; font-style: italic;'>"
                        + getString(R.string.descriptionNoConnection)
                        + "</h3>  <h5 style='text-align:center; padding-top:80%; opacity: 0.3;'>"
                        + getString(R.string.awards)
                        + "</h5></body></html>";
        webViewInsta.loadData(summary, "text/html; charset=utf-8", "utf-8");
        //load a custom html page
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }


    //*********************** KEY ****************************
    // handling the back button
    @Override
    public void onBackPressed() {
        if (mCustomView != null) {
            myWebChromeClient.onHideCustomView(); //hide video player
        }
        else {
            if (webViewInsta.canGoBack()) {
                webViewInsta.goBack();
            }
            else {
                finish(); // close app
            }
        }
    }
}
