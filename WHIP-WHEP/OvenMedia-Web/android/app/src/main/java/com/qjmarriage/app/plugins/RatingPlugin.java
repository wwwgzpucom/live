package com.qjmarriage.app.plugins;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.qjmarriage.app.Functions;
import com.qjmarriage.app.PluginInterface;
import com.qjmarriage.app.PluginManager;
import com.qjmarriage.app.SWVContext;

public class RatingPlugin implements PluginInterface {
    private static final String TAG = "RatingPlugin";
    private Activity activity;

    private static final String DIALOG_TITLE = "给个好评";
    private static final String DIALOG_MESSAGE = "如果您喜欢使用本应用，能否花一点时间给个评分？感谢支持！";
    private static final String BUTTON_RATE_NOW = "立即评分";
    private static final String BUTTON_REMIND_LATER = "稍后再说";
    private static final String BUTTON_NO_THANKS = "不用了";

    // SharedPreferences keys
    private static final String PREF_NAME = "swv_rating_plugin_prefs";
    private static final String KEY_INSTALL_DATE = "install_date";
    private static final String KEY_LAUNCH_TIMES = "launch_times";
    private static final String KEY_DONT_SHOW_AGAIN = "dont_show_again";
    private static final String KEY_REMIND_LATER_DATE = "remind_later_date";

    static {
        // No default config needed as it's read from SWVContext
        PluginManager.registerPlugin(new RatingPlugin(), new HashMap<>());
    }

    @Override
    public void initialize(Activity activity, WebView webView, Functions functions, Map<String, Object> config) {
        this.activity = activity;
        Log.d(TAG, "RatingPlugin initialized.");
        monitor();
        if (shouldShowRateDialog()) {
            showRateDialog();
        }
    }

    private SharedPreferences getPrefs() {
        return activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private void monitor() {
        SharedPreferences prefs = getPrefs();
        SharedPreferences.Editor editor = prefs.edit();

        int currentLaunchTimes = prefs.getInt(KEY_LAUNCH_TIMES, 0) + 1;
        editor.putInt(KEY_LAUNCH_TIMES, currentLaunchTimes);

        if (prefs.getLong(KEY_INSTALL_DATE, 0) == 0) {
            editor.putLong(KEY_INSTALL_DATE, System.currentTimeMillis());
        }
        editor.apply();
    }

    private boolean shouldShowRateDialog() {
        SharedPreferences prefs = getPrefs();

        if (prefs.getBoolean(KEY_DONT_SHOW_AGAIN, false)) {
            return false;
        }
        if (prefs.getInt(KEY_LAUNCH_TIMES, 0) < SWVContext.ASWR_TIMES) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long installDate = prefs.getLong(KEY_INSTALL_DATE, 0);
        long remindLaterDate = prefs.getLong(KEY_REMIND_LATER_DATE, 0);

        if (currentTime < installDate + (long) SWVContext.ASWR_DAYS * 24 * 60 * 60 * 1000) {
            return false;
        }
        if (remindLaterDate != 0 && currentTime < remindLaterDate + (long) SWVContext.ASWR_INTERVAL * 24 * 60 * 60 * 1000) {
            return false;
        }

        return true;
    }

    @SuppressLint("NewApi")
    private void showRateDialog() {
        if (activity == null || activity.isFinishing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(DIALOG_TITLE);
        builder.setMessage(DIALOG_MESSAGE);

        builder.setPositiveButton(BUTTON_RATE_NOW, (dialog, which) -> {
            rateApp();
            getPrefs().edit().putBoolean(KEY_DONT_SHOW_AGAIN, true).apply();
        });

        builder.setNeutralButton(BUTTON_REMIND_LATER, (dialog, which) -> {
            getPrefs().edit().putLong(KEY_REMIND_LATER_DATE, System.currentTimeMillis()).apply();
        });

        builder.setNegativeButton(BUTTON_NO_THANKS, (dialog, which) -> {
            getPrefs().edit().putBoolean(KEY_DONT_SHOW_AGAIN, true).apply();
        });

        builder.create().show();
    }

    private void rateApp() {
        final String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    // --- Standard Plugin Interface Methods ---
    @Override public String getPluginName() { return "RatingPlugin"; }
    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {}
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {}
    @Override public boolean shouldOverrideUrlLoading(WebView view, String url) { return false; }
    @Override public void onPageStarted(String url) {}
    @Override public void onPageFinished(String url) {}
    @Override public void onResume() {}
    @Override public void onPause() {}
    @Override public void onDestroy() {}
    @Override public void evaluateJavascript(String script) {}
}