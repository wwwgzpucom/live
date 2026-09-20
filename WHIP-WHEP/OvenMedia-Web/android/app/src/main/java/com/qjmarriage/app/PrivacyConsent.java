package com.qjmarriage.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gates sensitive APIs (location, etc.) until the user accepts the privacy policy.
 * Required by mainland Android stores such as 应用宝.
 */
public final class PrivacyConsent {

    private static final String PREFS = "qj_privacy_consent";
    private static final String KEY_ACCEPTED = "accepted_v1";

    private PrivacyConsent() {}

    public static boolean isAccepted(Context context) {
        return prefs(context).getBoolean(KEY_ACCEPTED, false);
    }

    public static void accept(Context context) {
        prefs(context).edit().putBoolean(KEY_ACCEPTED, true).apply();
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
