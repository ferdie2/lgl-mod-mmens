package com.android.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

public class Main {

    //Load lib
    static {
        // When you change the lib name, change also on Android.mk file
        // Both must have same name
        System.loadLibrary("MyLibName");
    }

    private static native void CheckOverlayPermission(Context context);

    public static void Start(Context context) {
        CrashHandler.init(context, false);
        
        // For SDK 33+, ensure proper permission handling
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Request notification permission if needed
            // CheckOverlayPermission will handle the rest
        }
        
        try {
            CheckOverlayPermission(context);
        } catch (UnsatisfiedLinkError e) {
            // Fallback if native library is not loaded
            StartWithoutPermission(context);
        }
    }
    
    public static void StartWithoutPermission(Context context) {
        try {
            CrashHandler.init(context, false);
            
            if (context instanceof Activity) {
                //Check if context is an Activity.
                Menu menu = new Menu(context);
                try {
                    menu.SetWindowManagerActivity();
                    menu.ShowMenu();
                    android.util.Log.d("Main", "Menu started in Activity mode");
                } catch (Exception e) {
                    android.util.Log.e("Main", "Failed to start menu in Activity mode: " + e.getMessage());
                    // Fallback to SetWindowManagerWithoutPermission
                    try {
                        menu.SetWindowManagerWithoutPermission();
                        menu.ShowMenu();
                        android.util.Log.d("Main", "Menu started with fallback mode");
                    } catch (Exception fallbackError) {
                        android.util.Log.e("Main", "All fallback methods failed: " + fallbackError.getMessage());
                    }
                }
            } else {
                // Start menu without overlay permission check
                try {
                    Intent intent = new Intent(context, Launcher.class);
                    intent.putExtra("noPermission", true);
                    context.startService(intent);
                    android.util.Log.d("Main", "Menu service started without permission");
                } catch (Exception e) {
                    android.util.Log.e("Main", "Failed to start menu service: " + e.getMessage());
                    // Try direct menu creation as last resort
                    try {
                        Menu menu = new Menu(context);
                        menu.SetWindowManagerWithoutPermission();
                        menu.ShowMenu();
                        android.util.Log.d("Main", "Menu started as last resort");
                    } catch (Exception lastResortError) {
                        android.util.Log.e("Main", "Last resort menu creation failed: " + lastResortError.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.e("Main", "Critical error in StartWithoutPermission: " + e.getMessage());
        }
    }
}
