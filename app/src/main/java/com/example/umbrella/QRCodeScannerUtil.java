package com.example.umbrella;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRCodeScannerUtil {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;

    public static void startScan(Activity activity) {
        if (checkCameraPermission(activity)) {
            initiateScan(activity);
        } else {
            requestCameraPermission(activity);
        }
    }

    // 카메라 스캔 시작 메소드
    private static void initiateScan(Activity activity) {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    // 카메라 권한 요청
    private static boolean checkCameraPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private static void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    // 카메라 권한 여부에 따른 동작
    public static void handlePermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initiateScan(activity);
            } else {
                Toast.makeText(activity.getBaseContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 스캔 시 작동 메소드
    public static void handleResult(int requestCode, int resultCode, Intent data, QRScanResultHandler handler) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && handler != null) {
            if (result.getContents() != null) {
                handler.onSuccess(result.getContents());
            } else {
                handler.onFailure();
            }
        }
    }


    public interface QRScanResultHandler {
        void onSuccess(String scannedText);
        void onFailure();
    }
}
