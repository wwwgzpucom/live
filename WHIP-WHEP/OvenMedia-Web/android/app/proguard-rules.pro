# ===========================================
# ===========================================

# --- WebView & JavaScript Interface ---
# Keep the JS interface class and all its public methods
-keepclassmembers class com.qjmarriage.app.MainActivity$WebAppInterface {
    public *;
}
# Keep any JS interfaces added by plugins
-keepclassmembers class com.qjmarriage.app.plugins.** {
    public *;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# --- Plugin System ---
# Keep the plugin interface and all plugin implementations
-keep interface com.qjmarriage.app.PluginInterface { *; }
-keep class com.qjmarriage.app.plugins.** { *; }
-keep class com.qjmarriage.app.PluginManager { *; }
-keep class com.qjmarriage.app.SWVContext { *; }
-keep class com.qjmarriage.app.SWVContext$* { *; }
-keep class com.qjmarriage.app.Functions { *; }

# --- Firebase ---
-keep class com.google.firebase.** { *; }
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.**

# --- Google Play Services (Auth, Location) ---
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# --- ZXing (QR Scanner) ---
-keep class com.journeyapps.** { *; }
-keep class com.google.zxing.** { *; }
-dontwarn com.journeyapps.**
-dontwarn com.google.zxing.**

# --- AndroidX Biometric ---
-keep class androidx.biometric.** { *; }
-dontwarn androidx.biometric.**

# --- Keep generic types needed for reflection ---
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes SourceFile,LineNumberTable

# --- Suppress warnings ---
-dontwarn android.support.**
-dontwarn org.slf4j.**
