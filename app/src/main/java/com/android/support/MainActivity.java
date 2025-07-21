package com.android.support;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    private static final String TAG = "MainActivity";
    
    // Game activity configuration
    private static final String GAME_ACTIVITY = "com.unity3d.player.UnityPlayerActivity";
    private volatile boolean hasLaunched = false;
    
    // Performance optimization: Use handler for UI operations
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize main handler for UI operations
        mainHandler = new Handler(Looper.getMainLooper());
        
        // Set up global crash handler
        setupCrashHandler();
        
        // Launch game activity if not already launched
        if (!hasLaunched) {
            launchGameActivity();
        } else {
            // Launch mod menu directly
            startModMenu();
        }
    }
    
    /**
     * Sets up enhanced crash handling
     */
    private void setupCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.e(TAG, "Uncaught exception in MainActivity", throwable);
                
                // Show user-friendly error message
                if (mainHandler != null) {
                    mainHandler.post(() -> {
                        try {
                            Toast.makeText(MainActivity.this, 
                                "App encountered an error. Please restart.", 
                                Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Error showing toast", e);
                        }
                    });
                }
                
                // Call default handler after a brief delay
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (Thread.getDefaultUncaughtExceptionHandler() != null) {
                        Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, throwable);
                    }
                }, 1000);
            }
        });
    }
    
    /**
     * Launches the game activity with proper error handling
     */
    private void launchGameActivity() {
        try {
            hasLaunched = true;
            
            // Start the game activity
            Intent gameIntent = new Intent(this, Class.forName(GAME_ACTIVITY));
            gameIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(gameIntent);
            
            // Start mod menu with a slight delay for better performance
            mainHandler.postDelayed(this::startModMenu, 500);
            
            Log.d(TAG, "Game activity launched successfully");
            
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Game's main activity does not exist: " + GAME_ACTIVITY, e);
            
            // Show error message to user
            Toast.makeText(this, "Game activity not found. Starting mod menu only.", 
                Toast.LENGTH_LONG).show();
            
            // Start mod menu as fallback
            startModMenu();
        } catch (Exception e) {
            Log.e(TAG, "Failed to launch game activity", e);
            
            // Fallback to mod menu
            startModMenu();
        }
    }
    
    /**
     * Starts the mod menu with enhanced error handling
     */
    private void startModMenu() {
        try {
            Main.Start(this);
            Log.d(TAG, "Mod menu started successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to start mod menu", e);
            
            // Try fallback method
            try {
                Main.StartWithoutPermission(this);
                Log.d(TAG, "Mod menu started with fallback method");
            } catch (Exception fallbackException) {
                Log.e(TAG, "All mod menu startup methods failed", fallbackException);
                Toast.makeText(this, "Failed to initialize mod menu", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Clean up handler to prevent memory leaks
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
            mainHandler = null;
        }
        
        // Clean up other components
        try {
            Main.cleanup();
            CrashHandler.cleanup();
            Preferences.clearCache();
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup", e);
        }
        
        Log.d(TAG, "MainActivity destroyed and cleaned up");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity paused");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity resumed");
    }
}
