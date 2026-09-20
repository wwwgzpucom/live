package com.qjmarriage.app.plugins;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.qjmarriage.app.Functions;
import com.qjmarriage.app.PluginInterface;
import com.qjmarriage.app.PluginManager;
import com.qjmarriage.app.SWVContext;

public class JSInterfacePlugin implements PluginInterface {
    private static final String TAG = "JSInterfacePlugin";
    private Activity activity;
    private WebView webView;
    private Map<String, Object> config;

    // Static initializer block for self-registration
    static {
        Map<String, Object> config = new HashMap<>();
        config.put("enableConsoleLogging", true);  // Log console messages
        config.put("allowedOrigins", "*");  // Allow all origins by default
        config.put("enablePluginAccess", true);  // Allow access to other plugins

        PluginManager.registerPlugin(new JSInterfacePlugin(), config);
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        this.webView = webView;
        this.config = config;

        // Add JavaScript interface
        webView.addJavascriptInterface(new JSBridgeInterface(), "JSBridge");

        Log.d(TAG, "JSInterfacePlugin initialized with config: " + config);
    }

    @Override
    public String getPluginName() {
        return "JSInterfacePlugin";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {}

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    @Override
    public void onPageStarted(String url) {}

    @Override
    public void onPageFinished(String url) {
        // Inject JavaScript bridge
        injectJSBridge();
    }

    private void injectJSBridge() {
        String jsBridgeCode =
                "if (!window.JSBridge) {\n" +
                        "    window.JSBridgeCallbacks = {};\n" +
                        "    window.JSBridge.registerCallback = function(eventName, callback) {\n" +
                        "        window.JSBridgeCallbacks[eventName] = callback;\n" +
                        "    };\n" +
                        "    window.JSBridge.triggerCallback = function(eventName, data) {\n" +
                        "        if (window.JSBridgeCallbacks[eventName]) {\n" +
                        "            window.JSBridgeCallbacks[eventName](data);\n" +
                        "            return true;\n" +
                        "        }\n" +
                        "        return false;\n" +
                        "    };\n" +
                        "    console.log('JavaScript Bridge initialized');\n" +
                        "}\n";

        evaluateJavascript(jsBridgeCode);
    }

    @Override public void onResume() {}
    @Override public void onPause() {}

    @Override
    public void onDestroy() {}

    @Override
    public void evaluateJavascript(String script) {
        if (webView != null) {
            webView.evaluateJavascript(script, null);
        }
    }

    public void triggerJsCallback(String eventName, JSONObject data) {
        if (webView != null) {
            String jsCall = "if(window.JSBridge.triggerCallback) window.JSBridge.triggerCallback('" + eventName + "', " + data.toString() + ");";
            evaluateJavascript(jsCall);
        }
    }

    public class JSBridgeInterface {

        @JavascriptInterface
        public String getDeviceInfo() {
            try {
                JSONObject info = new JSONObject();
                info.put("manufacturer", Build.MANUFACTURER);
                info.put("model", Build.MODEL);
                info.put("osVersion", Build.VERSION.RELEASE);
                info.put("sdkVersion", Build.VERSION.SDK_INT);
                return info.toString();
            } catch (JSONException e) {
                Log.e(TAG, "Error creating device info JSON", e);
                return "{}";
            }
        }

        @JavascriptInterface
        public String getAppInfo() {
            try {
                // Get version name dynamically using the plugin's activity context
                String versionName = "";
                try {
                    versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
                } catch (Exception e) {
                    Log.e(TAG, "Could not get package version name", e);
                }

                JSONObject info = new JSONObject();
                info.put("version", versionName); // Use the dynamically fetched version
                info.put("homepage", SWVContext.ASWV_URL);
                return info.toString();

            } catch (JSONException e) {
                Log.e(TAG, "Error creating app info JSON", e);
                return "{}";
            }
        }

        @JavascriptInterface
        public void showToast(String message) {
            // Bridge to ToastPlugin if enabled
            if (Boolean.TRUE.equals(config.getOrDefault("enablePluginAccess", true))) {
                try {
                    ToastPlugin toastPlugin = (ToastPlugin) SWVContext.getPluginManager().getPluginInstance("ToastPlugin");
                    if (toastPlugin != null) {
                        toastPlugin.showToast(message);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error showing toast via plugin", e);
                }
            }
        }
    }
}