package com.qjmarriage.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private static final String TAG = "PermissionManager";

    // --- Permission Request Codes ---
    // We use a single code for the initial batch request for simplicity.
    // Individual requests (like from a plugin) can use their own codes.
    public static final int INITIAL_REQUEST_CODE = 100;
    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int STORAGE_REQUEST_CODE = 102;
    /** WebView / OvenLiveKit 相机+麦克风 */
    public static final int LIVE_MEDIA_REQUEST_CODE = 103;

    private final Activity activity;

    public PermissionManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * Checks configured launch permissions and requests them in a batch.
     * LOCATION is never requested here — only after privacy consent and when a feature needs it.
     */
    public void requestInitialPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        for (String permissionGroup : SWVContext.ASWP_REQUIRED_PERMISSIONS) {
            switch (permissionGroup) {
                case "LOCATION":
                    // Skip on launch. Location must wait for PrivacyConsent + contextual request.
                    Log.d(TAG, "Skipping LOCATION in launch batch (privacy compliance).");
                    break;

                case "NOTIFICATIONS":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNotificationPermissionGranted()) {
                        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
                    }
                    break;

                case "STORAGE":
                    if (SWVContext.ASWP_FUPLOAD && !isStoragePermissionGranted()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES);
                        } else {
                            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }
                    break;
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            Log.d(TAG, "Requesting initial permissions: " + permissionsToRequest);
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toArray(new String[0]), INITIAL_REQUEST_CODE);
        } else {
            Log.d(TAG, "All initial permissions are already granted.");
        }
    }

    /**
     * Request location only after the user has accepted the privacy policy.
     */
    public void requestLocationPermission() {
        if (!PrivacyConsent.isAccepted(activity)) {
            Log.w(TAG, "Blocked location permission request: privacy not accepted.");
            return;
        }
        if (!isLocationPermissionGranted()) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    INITIAL_REQUEST_CODE
            );
        }
    }

    /**
     * A dedicated method to request camera permissions when needed.
     * This is better for user context than asking on launch.
     */
    public void requestCameraPermission() {
        if (!isCameraPermissionGranted()) {
            List<String> permissions = new ArrayList<>();
            permissions.add(Manifest.permission.CAMERA);
            if (!isStoragePermissionGranted()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
                } else {
                    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[0]), CAMERA_REQUEST_CODE);
        }
    }

    /**
     * Request camera / mic for OvenMedia H5 live (OvenLiveKit).
     * Prefer calling when user enters the host room, not on cold start.
     */
    public void requestLiveMediaPermissions(boolean needCamera, boolean needAudio) {
        List<String> permissions = new ArrayList<>();
        if (needCamera && !isCameraPermissionGranted()) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (needAudio && !isMicrophonePermissionGranted()) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if (permissions.isEmpty()) {
            Log.d(TAG, "Live media permissions already granted.");
            return;
        }
        Log.d(TAG, "Requesting live media permissions: " + permissions);
        ActivityCompat.requestPermissions(
                activity,
                permissions.toArray(new String[0]),
                LIVE_MEDIA_REQUEST_CODE
        );
    }

    public boolean isLiveMediaPermissionGranted(boolean needCamera, boolean needAudio) {
        if (needCamera && !isCameraPermissionGranted()) {
            return false;
        }
        if (needAudio && !isMicrophonePermissionGranted()) {
            return false;
        }
        return true;
    }

    // --- Helper methods to check permission status ---

    public boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Notifications permission not required before Android 13
    }

    public boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isMicrophonePermissionGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        }
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
