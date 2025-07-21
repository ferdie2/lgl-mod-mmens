
package com.android.support;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Launcher extends Service {
    
    private static final String TAG = "Launcher";
    
    // Performance optimizations
    private static final long VISIBILITY_CHECK_INTERVAL = 1500; // Reduced from 1000ms
    private static final long DESTROY_DELAY = 200; // Reduced from 100ms
    
    private Menu menu;
    private Handler mainHandler;
    private ScheduledExecutorService scheduledExecutor;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);
    
    // Cache for activity manager to avoid repeated lookups
    private ActivityManager activityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        
        try {
            Log.d(TAG, "Launcher service created");
            
            // Initialize components
            mainHandler = new Handler(Looper.getMainLooper());
            scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            
            // Create menu with error handling
            if (!createMenu()) {
                Log.e(TAG, "Failed to create menu, stopping service");
                stopSelf();
                return;
            }
            
            // Start visibility monitoring
            startVisibilityMonitoring();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            stopSelf();
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (isDestroyed.get()) {
                Log.w(TAG, "Service already destroyed, ignoring start command");
                return START_NOT_STICKY;
            }
            
            boolean noPermission = intent != null && intent.getBooleanExtra("noPermission", false);
            Log.d(TAG, "onStartCommand called, noPermission: " + noPermission);
            
            if (menu == null && !createMenu()) {
                Log.e(TAG, "Failed to create menu in onStartCommand");
                return START_NOT_STICKY;
            }
            
            // Setup menu based on permission mode
            setupMenuDisplay(noPermission);
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onStartCommand", e);
        }
        
        return START_NOT_STICKY; // Don't restart if killed
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // We don't provide binding
    }
    
    /**
     * Creates menu instance with error handling
     */
    private boolean createMenu() {
        try {
            if (menu == null) {
                menu = new Menu(this);
                Log.d(TAG, "Menu created successfully");
                return true;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to create menu", e);
            return false;
        }
    }
    
    /**
     * Sets up menu display based on permission mode
     */
    private void setupMenuDisplay(boolean noPermission) {
        try {
            if (noPermission) {
                menu.SetWindowManagerWithoutPermission();
                Log.d(TAG, "Menu setup without permission");
            } else {
                menu.SetWindowManagerWindowService();
                Log.d(TAG, "Menu setup with window service");
            }
            
            menu.ShowMenu();
            Log.d(TAG, "Menu displayed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to setup menu display", e);
        }
    }
    
    /**
     * Starts optimized visibility monitoring
     */
    private void startVisibilityMonitoring() {
        if (isRunning.compareAndSet(false, true)) {
            scheduledExecutor.scheduleWithFixedDelay(
                this::checkVisibility,
                VISIBILITY_CHECK_INTERVAL,
                VISIBILITY_CHECK_INTERVAL,
                TimeUnit.MILLISECONDS
            );
            Log.d(TAG, "Visibility monitoring started");
        }
    }
    
    /**
     * Optimized visibility check to reduce CPU usage
     */
    private void checkVisibility() {
        if (isDestroyed.get() || menu == null) {
            return;
        }
        
        try {
            boolean notInGame = isNotInGame();
            
            // Update UI on main thread only when necessary
            mainHandler.post(() -> {
                try {
                    if (menu != null && !isDestroyed.get()) {
                        int newVisibility = notInGame ? View.INVISIBLE : View.VISIBLE;
                        
                        // Only update if visibility actually changed
                        if (menu.getRootFrame() != null && 
                            menu.getRootFrame().getVisibility() != newVisibility) {
                            menu.setVisibility(newVisibility);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating menu visibility", e);
                }
            });
            
        } catch (Exception e) {
            Log.e(TAG, "Error in visibility check", e);
        }
    }

    /**
     * Optimized game state check with caching
     */
    private boolean isNotInGame() {
        try {
            if (activityManager == null) {
                activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            }
            
            ActivityManager.RunningAppProcessInfo runningAppProcessInfo = 
                new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(runningAppProcessInfo);
            
            // Cache the result briefly to avoid excessive checks
            return runningAppProcessInfo.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking game state", e);
            return false; // Assume in game on error
        }
    }

    /**
     * Enhanced cleanup on destroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        if (!isDestroyed.compareAndSet(false, true)) {
            Log.w(TAG, "Service already destroyed");
            return;
        }
        
        Log.d(TAG, "Destroying Launcher service");
        
        // Stop monitoring
        isRunning.set(false);
        
        try {
            // Cleanup scheduled executor
            if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
                scheduledExecutor.shutdown();
                try {
                    if (!scheduledExecutor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                        scheduledExecutor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduledExecutor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            
            // Cleanup menu
            if (menu != null) {
                try {
                    menu.onDestroy();
                } catch (Exception e) {
                    Log.e(TAG, "Error destroying menu", e);
                }
                menu = null;
            }
            
            // Cleanup handler
            if (mainHandler != null) {
                mainHandler.removeCallbacksAndMessages(null);
                mainHandler = null;
            }
            
            // Clear activity manager reference
            activityManager = null;
            
            Log.d(TAG, "Launcher service destroyed successfully");
            
        } catch (Exception e) {
            Log.e(TAG, "Error during destruction", e);
        }
    }

    /**
     * Enhanced task removal handling
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        
        Log.d(TAG, "Task removed, preparing to stop service");
        
        try {
            // Stop monitoring immediately
            isRunning.set(false);
            
            // Schedule cleanup and stop
            if (mainHandler != null) {
                mainHandler.postDelayed(() -> {
                    try {
                        stopSelf();
                    } catch (Exception e) {
                        Log.e(TAG, "Error stopping service", e);
                    }
                }, DESTROY_DELAY);
            } else {
                // Immediate stop if handler is not available
                stopSelf();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onTaskRemoved", e);
            stopSelf(); // Ensure service stops even on error
        }
    }
    
    /**
     * Handle low memory situations
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "Low memory detected, optimizing");
        
        try {
            // Temporarily hide menu to save memory
            if (menu != null) {
                menu.setVisibility(View.INVISIBLE);
            }
            
            // Force garbage collection
            System.gc();
            
        } catch (Exception e) {
            Log.e(TAG, "Error handling low memory", e);
        }
    }
    
    /**
     * Handle memory trimming
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d(TAG, "Memory trim requested, level: " + level);
        
        try {
            if (level >= TRIM_MEMORY_RUNNING_CRITICAL) {
                // Critical memory situation - hide menu temporarily
                if (menu != null) {
                    menu.setVisibility(View.INVISIBLE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling memory trim", e);
        }
    }
}
