package com.qjmarriage.app;

import android.app.Activity;
import android.content.Intent;
import android.webkit.WebView;
import java.util.Map;
import androidx.annotation.NonNull;

public interface PluginInterface {
	void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config);
	String getPluginName();
	void onActivityResult(int requestCode, int resultCode, Intent data);
	void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
	boolean shouldOverrideUrlLoading(WebView view, String url);
	void onPageStarted(String url);
	void onPageFinished(String url);
	void onResume();
	void onPause();
	void onDestroy();
	void evaluateJavascript(String script);
}