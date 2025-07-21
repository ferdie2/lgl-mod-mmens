package com.android.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    
    private static final String TAG = "Main";
    private static final String LIBRARY_NAME = "MyLibName";
    
    // Thread-safe loading state
    private static final AtomicBoolean isLibraryLoaded = new AtomicBoolean(false);
    private static final AtomicBoolean isInitialized = new AtomicBoolean(false);
    
    // Performance optimization: Use thread pool for background operations
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    
    // Handler for UI operations
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Load native library with error handling
    static {
        try {
            System.loadLibrary(LIBRARY_NAME);
            isLibraryLoaded.set(true);
            Log.d(TAG, "Native library loaded successfully: " + LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load native library: " + LIBRARY_NAME, e);
            isLibraryLoaded.set(false);
        }
    }

    // Native method declarations
    private static native void CheckOverlayPermission(Context context);

    /**
     * Enhanced main start method with better performance and error handling
     */
    public static void Start(Context context) {
        if (context == null) {
            Log.e(TAG, "Context is null, cannot start");
            return;
        }
        
        // Prevent multiple initializations
        if (!isInitialized.compareAndSet(false, true)) {
            Log.w(TAG, "Already initialized, skipping");
            return;
        }
        
        // Initialize crash handler first
        try {
            CrashHandler.init(context, false);
            Log.d(TAG, "Crash handler initialized");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize crash handler", e);
        }
        
        // Handle Android 13+ permission requirements
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            handleModernPermissions(context);
        }
        
        // Start main functionality in background thread to prevent UI blocking
        executorService.execute(() -> {
            try {
                if (isLibraryLoaded.get()) {
                    CheckOverlayPermission(context);
                    Log.d(TAG, "Overlay permission check completed");
                } else {
                    Log.w(TAG, "Native library not loaded, using fallback method");
                    // Use main thread for UI operations
                    mainHandler.post(() -> StartWithoutPermission(context));
                }
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "Native method call failed", e);
                // Fallback to non-native implementation
                mainHandler.post(() -> StartWithoutPermission(context));
            } catch (Exception e) {
                Log.e(TAG, "Error in native permission check", e);
                mainHandler.post(() -> StartWithoutPermission(context));
            }
        });
    }
    
    /**
     * Handles permissions for Android 13+
     */
    private static void handleModernPermissions(Context context) {
        // For Android 13+, handle notification and other modern permissions
        try {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                // Check if we need to request notification permission
                // This is handled automatically by the system in most cases
                Log.d(TAG, "Modern permission handling for API " + Build.VERSION.SDK_INT);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling modern permissions", e);
        }
    }
    
    /**
     * Enhanced fallback method with better performance and error handling
     */
    public static void StartWithoutPermission(Context context) {
        if (context == null) {
            Log.e(TAG, "Context is null in StartWithoutPermission");
            return;
        }
        
        try {
            // Initialize crash handler if not already done
            CrashHandler.init(context, false);
            
            // Use background thread for menu creation to prevent UI lag
            executorService.execute(() -> {
                try {
                    Menu menu = createMenu(context);
                    if (menu == null) {
                        Log.e(TAG, "Failed to create menu");
                        return;
                    }
                    
                    // Setup menu on main thread
                    mainHandler.post(() -> setupMenu(context, menu));
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error creating menu in background", e);
                    // Try on main thread as last resort
                    mainHandler.post(() -> {
                        try {
                            Menu menu = createMenu(context);
                            if (menu != null) {
                                setupMenu(context, menu);
                            }
                        } catch (Exception mainThreadError) {
                            Log.e(TAG, "All menu creation attempts failed", mainThreadError);
                            showErrorToUser(context, "Failed to initialize mod menu");
                        }
                    });
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Critical error in StartWithoutPermission", e);
            showErrorToUser(context, "Critical initialization error");
        }
    }
    
    /**
     * Creates menu instance with error handling
     */
    private static Menu createMenu(Context context) {
        try {
            return new Menu(context);
        } catch (Exception e) {
            Log.e(TAG, "Failed to create Menu instance", e);
            return null;
        }
    }
    
    /**
     * Sets up menu with appropriate window manager
     */
    private static void setupMenu(Context context, Menu menu) {
        try {
            if (context instanceof Activity) {
                // Activity context - try activity mode first
                try {
                    menu.SetWindowManagerActivity();
                    menu.ShowMenu();
                    Log.d(TAG, "Menu started in Activity mode");
                } catch (Exception e) {
                    Log.w(TAG, "Activity mode failed, trying fallback: " + e.getMessage());
                    // Fallback to without permission mode
                    try {
                        menu.SetWindowManagerWithoutPermission();
                        menu.ShowMenu();
                        Log.d(TAG, "Menu started with fallback mode");
                    } catch (Exception fallbackError) {
                        Log.e(TAG, "All activity setup methods failed", fallbackError);
                    }
                }
            } else {
                // Service context - start as service
                startMenuService(context);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up menu", e);
        }
    }
    
    /**
     * Starts menu as a service
     */
    private static void startMenuService(Context context) {
        try {
            Intent intent = new Intent(context, Launcher.class);
            intent.putExtra("noPermission", true);
            context.startService(intent);
            Log.d(TAG, "Menu service started");
        } catch (Exception e) {
            Log.e(TAG, "Failed to start menu service", e);
            // Last resort - try direct menu creation
            try {
                Menu menu = new Menu(context);
                menu.SetWindowManagerWithoutPermission();
                menu.ShowMenu();
                Log.d(TAG, "Menu started as last resort");
            } catch (Exception lastResortError) {
                Log.e(TAG, "Last resort menu creation failed", lastResortError);
            }
        }
    }
    
    /**
     * Shows error message to user on main thread
     */
    private static void showErrorToUser(Context context, String message) {
        mainHandler.post(() -> {
            try {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e(TAG, "Failed to show error toast", e);
            }
        });
    }
    
    /**
     * Clean up resources (call this when app is destroyed)
     */
    public static void cleanup() {
        try {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
                Log.d(TAG, "ExecutorService shutdown");
            }
            
            if (mainHandler != null) {
                mainHandler.removeCallbacksAndMessages(null);
                Log.d(TAG, "Main handler cleared");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup", e);
        }
    }
}
