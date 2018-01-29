package com.waitwind.activity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.waitwind.R;

public class WebViewActivity extends BaseActivity {
	
	private WebView webView = null;
	private WebSettings webSettings = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		initWebView();
	}
	
	/**
	 * 初始化webview
	 */
	private void initWebView() {
		String url = getIntent().getStringExtra("URL");
		if (url == null) {
			return;
		}
		webView = (WebView) findViewById(R.id.webView);
		webSettings = webView.getSettings();
		//支持javascript
		webSettings.setJavaScriptEnabled(true);
		// 设置可以支持缩放
		webSettings.setSupportZoom(true);
		webSettings.setDisplayZoomControls(false);
		// 设置出现缩放工具
		webSettings.setBuiltInZoomControls(true);
		//扩大比例的缩放
		webSettings.setUseWideViewPort(true);
		//自适应屏幕
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setLoadWithOverviewMode(true);
		webView.loadUrl(url);
		
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}
		});
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webView.loadUrl(url);
				return true;
			}
		});
	}
}
