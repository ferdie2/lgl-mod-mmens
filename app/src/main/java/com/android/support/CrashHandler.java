//Credit: Raunak Mods - https://t.me/raunakmods786

package com.android.support;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CrashHandler {

    private static final String TAG = "CrashHandler";
    public static final UncaughtExceptionHandler DEFAULT_UNCAUGHT_EXCEPTION_HANDLER = 
        Thread.getDefaultUncaughtExceptionHandler();

    // Performance optimizations
    private static final AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Enhanced crash handler initialization with better performance
     */
    public static void init(final Context app, final boolean overlayRequired) {
        if (app == null) {
            Log.e(TAG, "Context is null, cannot initialize crash handler");
            return;
        }
        
        // Prevent multiple initializations
        if (!isInitialized.compareAndSet(false, true)) {
            Log.w(TAG, "Crash handler already initialized");
            return;
        }
        
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e(TAG, "Uncaught exception occurred", throwable);
                
                try {
                    // Process crash in background thread to prevent ANR
                    executorService.execute(() -> {
                        try {
                            handleUncaughtException(app, thread, throwable);
                        } catch (Throwable e) {
                            Log.e(TAG, "Error in crash handler", e);
                            // Fallback to default handler
                            if (DEFAULT_UNCAUGHT_EXCEPTION_HANDLER != null) {
                                DEFAULT_UNCAUGHT_EXCEPTION_HANDLER.uncaughtException(thread, throwable);
                            } else {
                                System.exit(2);
                            }
                        }
                    });
                    
                    // Give some time for crash processing
                    Thread.sleep(2000);
                    
                } catch (Throwable e) {
                    Log.e(TAG, "Critical error in uncaught exception handler", e);
                } finally {
                    // Ensure app exits
                    System.exit(2);
                }
            }
        });
        
        Log.d(TAG, "Crash handler initialized successfully");
    }

    /**
     * Enhanced crash handling with modern Android support
     */
    private static void handleUncaughtException(Context app, Thread thread, Throwable throwable) {
        Log.d(TAG, "Processing uncaught exception");

        final String timestamp = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss", Locale.getDefault())
            .format(new Date());
        String fileName = "mod_menu_crash_" + timestamp + ".txt";
        
        File crashFile = getCrashLogFile(app, fileName);
        String errorLog = generateErrorLog(app, timestamp, throwable);

        // Save crash log
        try {
            writeFile(crashFile, errorLog);
            Log.d(TAG, "Crash log saved to: " + crashFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Failed to save crash log", e);
        }

        // Show user notifications on main thread
        showCrashNotification(app, crashFile);
    }
    
    /**
     * Get appropriate crash log directory based on Android version
     */
    private static File getCrashLogFile(Context app, String fileName) {
        String dirPath;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33+
            // Use app-specific directory for Android 13+
            dirPath = app.getFilesDir().getAbsolutePath();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API 30+
            // Use Documents directory for Android 11+
            dirPath = "/storage/emulated/0/Documents/";
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // API 23+
            // Use app external files directory for Android 6+
            File externalDir = app.getExternalFilesDir(null);
            dirPath = externalDir != null ? externalDir.getAbsolutePath() : app.getFilesDir().getAbsolutePath();
        } else {
            // Fallback to internal storage
            dirPath = app.getFilesDir().getAbsolutePath();
        }
        
        return new File(dirPath, fileName);
    }
    
    /**
     * Generate comprehensive error log with device and app info
     */
    private static String generateErrorLog(Context app, String timestamp, Throwable throwable) {
        StringBuilder errorLog = new StringBuilder();
        
        // Get app version info
        String versionName = "unknown";
        long versionCode = 0;
        try {
            PackageManager pm = app.getPackageManager();
            PackageInfo packageInfo;
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageInfo = pm.getPackageInfo(app.getPackageName(), 
                    PackageManager.PackageInfoFlags.of(0));
            } else {
                packageInfo = pm.getPackageInfo(app.getPackageName(), 0);
            }
            
            versionName = packageInfo.versionName;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = packageInfo.getLongVersionCode();
            } else {
                versionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Could not get package info", e);
        }

        // Generate stack trace
        String fullStackTrace;
        try (StringWriter sw = new StringWriter(); 
             PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            fullStackTrace = sw.toString();
        } catch (IOException e) {
            fullStackTrace = "Error generating stack trace: " + e.getMessage();
        }

        // Build comprehensive crash report
        errorLog.append("************* CRASH REPORT ****************\n");
        errorLog.append("Timestamp          : ").append(timestamp).append("\n");
        errorLog.append("Thread Name        : ").append(Thread.currentThread().getName()).append("\n");
        errorLog.append("Exception Type     : ").append(throwable.getClass().getSimpleName()).append("\n");
        errorLog.append("Exception Message  : ").append(throwable.getMessage()).append("\n");
        errorLog.append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n");
        errorLog.append("Device Model       : ").append(Build.MODEL).append("\n");
        errorLog.append("Device Brand       : ").append(Build.BRAND).append("\n");
        errorLog.append("Android Version    : ").append(Build.VERSION.RELEASE).append("\n");
        errorLog.append("Android SDK        : ").append(Build.VERSION.SDK_INT).append("\n");
        errorLog.append("App VersionName    : ").append(versionName).append("\n");
        errorLog.append("App VersionCode    : ").append(versionCode).append("\n");
        errorLog.append("App Package        : ").append(app.getPackageName()).append("\n");
        
        // Add memory info
        try {
            Runtime runtime = Runtime.getRuntime();
            errorLog.append("Memory Used        : ").append(
                (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024).append(" MB\n");
            errorLog.append("Memory Free        : ").append(
                runtime.freeMemory() / 1024 / 1024).append(" MB\n");
            errorLog.append("Memory Total       : ").append(
                runtime.totalMemory() / 1024 / 1024).append(" MB\n");
            errorLog.append("Memory Max         : ").append(
                runtime.maxMemory() / 1024 / 1024).append(" MB\n");
        } catch (Exception e) {
            errorLog.append("Memory Info        : Error retrieving memory info\n");
        }
        
        errorLog.append("*******************************************\n\n");
        errorLog.append("STACK TRACE:\n");
        errorLog.append(fullStackTrace);
        
        return errorLog.toString();
    }
    
    /**
     * Show crash notification to user
     */
    private static void showCrashNotification(Context app, File crashFile) {
        mainHandler.post(() -> {
            try {
                String message = "Game crashed unexpectedly";
                String logPath = crashFile.getAbsolutePath().replace("/storage/emulated/0/", "");
                String detailMessage = "Log saved to: " + logPath;
                
                Toast.makeText(app, message, Toast.LENGTH_LONG).show();
                
                // Show detailed message after a short delay
                mainHandler.postDelayed(() -> {
                    try {
                        Toast.makeText(app, detailMessage, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error showing detailed crash message", e);
                    }
                }, 1000);
                
            } catch (Exception e) {
                Log.e(TAG, "Error showing crash notification", e);
            }
        });
    }

    /**
     * Enhanced file writing with proper error handling
     */
    private static void writeFile(File file, String content) throws IOException {
        File parentFile = file.getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                throw new IOException("Failed to create directory: " + parentFile.getAbsolutePath());
            }
        }
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes("UTF-8"));
            fos.flush();
        }
        
        Log.d(TAG, "Crash log written successfully to: " + file.getAbsolutePath());
    }
    
    /**
     * Cleanup resources when app is destroyed
     */
    public static void cleanup() {
        try {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
            
            if (mainHandler != null) {
                mainHandler.removeCallbacksAndMessages(null);
            }
            
            isInitialized.set(false);
            Log.d(TAG, "Crash handler cleaned up");
            
        } catch (Exception e) {
            Log.e(TAG, "Error during crash handler cleanup", e);
        }
    }
}

