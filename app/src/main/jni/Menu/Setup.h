/**
 * Enhanced Modern Setup Header - Setup.h
 * 
 * Features:
 * - Modern C++17 architecture
 * - Enhanced JNI utilities
 * - Better permission management
 * - Improved security features
 * - Performance optimizations
 */

#ifndef ENHANCED_SETUP_H
#define ENHANCED_SETUP_H

#include <jni.h>
#include <android/log.h>
#include <string>
#include <memory>
#include <functional>
#include <atomic>
#include <chrono>

#include "Menu/Menu.h"
#include "Menu/get_device_api_level_inlines.h"
#include "Includes/Logger.h"
#include "Includes/obfuscate.h"

// Forward declarations
extern "C" {
    // Core JNI functions
    JNIEXPORT void JNICALL CheckOverlayPermission(JNIEnv* env, jclass clazz, jobject context);
    JNIEXPORT void JNICALL Init(JNIEnv* env, jobject obj, jobject context, jobject title, jobject subtitle);
    
    // Menu functions
    JNIEXPORT jstring JNICALL Icon(JNIEnv* env, jobject obj);
    JNIEXPORT jstring JNICALL IconWebViewData(JNIEnv* env, jobject obj);
    JNIEXPORT jobjectArray JNICALL SettingsList(JNIEnv* env, jobject obj);
}

// Enhanced namespace for setup utilities
namespace EnhancedSetup {
    // Constants
    constexpr int TOAST_LENGTH_SHORT = 0;
    constexpr int TOAST_LENGTH_LONG = 1;
    
    // Global state management
    namespace State {
        extern std::atomic<bool> iconValid;
        extern std::atomic<bool> settingsValid; 
        extern std::atomic<bool> initValid;
        extern std::atomic<bool> permissionGranted;
    }
    
    // Enhanced utility functions
    class UIUtils {
    public:
        /**
         * Enhanced dialog creation with modern styling
         */
        static void showDialog(JNIEnv* env, jobject context, 
                              const std::string& title, 
                              const std::string& message,
                              const std::string& positiveText = "OK",
                              std::function<void()> onPositive = nullptr);
        
        /**
         * Enhanced toast with custom styling
         */
        static void showToast(JNIEnv* env, jobject context, 
                             const std::string& text, 
                             int duration = TOAST_LENGTH_SHORT);
        
        /**
         * Enhanced text setting with HTML support
         */
        static bool setText(JNIEnv* env, jobject textView, const std::string& htmlText);
        
        /**
         * Show loading dialog
         */
        static void showLoadingDialog(JNIEnv* env, jobject context, 
                                     const std::string& message = "Loading...");
        
        /**
         * Show success notification
         */
        static void showSuccessNotification(JNIEnv* env, jobject context, 
                                           const std::string& message);
        
        /**
         * Show error notification
         */
        static void showErrorNotification(JNIEnv* env, jobject context, 
                                         const std::string& message);
    private:
        static jclass findClass(JNIEnv* env, const std::string& className);
        static jmethodID getMethodID(JNIEnv* env, jclass clazz, 
                                    const std::string& methodName, 
                                    const std::string& signature);
    };
    
    // Enhanced permission management
    class PermissionManager {
    public:
        /**
         * Check if overlay permission is granted
         */
        static bool hasOverlayPermission(JNIEnv* env, jobject context);
        
        /**
         * Request overlay permission with modern UI
         */
        static void requestOverlayPermission(JNIEnv* env, jobject context);
        
        /**
         * Check notification permission (Android 13+)
         */
        static bool hasNotificationPermission(JNIEnv* env, jobject context);
        
        /**
         * Request notification permission
         */
        static void requestNotificationPermission(JNIEnv* env, jobject context);
        
        /**
         * Check and request all required permissions
         */
        static bool checkAndRequestPermissions(JNIEnv* env, jobject context);
        
    private:
        static void startPermissionActivity(JNIEnv* env, jobject context, 
                                           const std::string& action, 
                                           const std::string& packageUri = "");
    };
    
    // Enhanced service management
    class ServiceManager {
    public:
        /**
         * Start launcher service with enhanced error handling
         */
        static bool startLauncherService(JNIEnv* env, jobject context);
        
        /**
         * Stop launcher service
         */
        static void stopLauncherService(JNIEnv* env, jobject context);
        
        /**
         * Check if service is running
         */
        static bool isServiceRunning(JNIEnv* env, jobject context, 
                                    const std::string& serviceName);
        
        /**
         * Restart service with delay
         */
        static void restartService(JNIEnv* env, jobject context, int delayMs = 1000);
    };
    
    // Enhanced security manager
    class SecurityManager {
    public:
        /**
         * Perform comprehensive security checks
         */
        static bool performSecurityCheck(JNIEnv* env, jobject context);
        
        /**
         * Check for debugging tools
         */
        static bool isDebuggingDetected();
        
        /**
         * Check for emulator environment
         */
        static bool isEmulatorDetected();
        
        /**
         * Check for root access
         */
        static bool isRootDetected();
        
        /**
         * Validate mod menu integrity
         */
        static bool validateIntegrity();
        
        /**
         * Handle security breach
         */
        static void handleSecurityBreach(const std::string& reason);
    };
    
    // Enhanced initialization manager
    class InitializationManager {
    public:
        /**
         * Initialize mod menu with enhanced features
         */
        static bool initializeModMenu(JNIEnv* env, jobject context, 
                                     jobject titleView, jobject subtitleView);
        
        /**
         * Setup title with modern styling
         */
        static void setupTitle(JNIEnv* env, jobject titleView);
        
        /**
         * Setup subtitle with animations
         */
        static void setupSubtitle(JNIEnv* env, jobject subtitleView);
        
        /**
         * Show welcome dialog
         */
        static void showWelcomeDialog(JNIEnv* env, jobject context);
        
        /**
         * Perform startup checks
         */
        static bool performStartupChecks(JNIEnv* env, jobject context);
    };
}

// Legacy function declarations for compatibility
void setDialog(jobject ctx, JNIEnv* env, const char* title, const char* msg);
void Toast(JNIEnv* env, jobject thiz, const char* text, int length);
void startActivityPermisson(JNIEnv* env, jobject ctx);
void startService(JNIEnv* env, jobject ctx);
void* exit_thread(void*);

// Enhanced function implementations
namespace EnhancedSetup {
    
    // State initialization
    namespace State {
        std::atomic<bool> iconValid{false};
        std::atomic<bool> settingsValid{false};
        std::atomic<bool> initValid{false};
        std::atomic<bool> permissionGranted{false};
    }
    
    // UIUtils implementation
    void UIUtils::showDialog(JNIEnv* env, jobject context, 
                            const std::string& title, 
                            const std::string& message,
                            const std::string& positiveText,
                            std::function<void()> onPositive) {
        try {
            jclass alertClass = findClass(env, OBFUSCATE("android/app/AlertDialog$Builder"));
            if (!alertClass) return;
            
            jmethodID constructor = getMethodID(env, alertClass, OBFUSCATE("<init>"), 
                                               OBFUSCATE("(Landroid/content/Context;)V"));
            if (!constructor) return;
            
            jobject alertBuilder = env->NewObject(alertClass, constructor, context);
            if (!alertBuilder) return;
            
            // Set title with enhanced styling
            jmethodID setTitle = getMethodID(env, alertClass, OBFUSCATE("setTitle"), 
                                           OBFUSCATE("(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;"));
            if (setTitle) {
                jstring titleStr = env->NewStringUTF(title.c_str());
                env->CallObjectMethod(alertBuilder, setTitle, titleStr);
                env->DeleteLocalRef(titleStr);
            }
            
            // Set message
            jmethodID setMessage = getMethodID(env, alertClass, OBFUSCATE("setMessage"), 
                                             OBFUSCATE("(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;"));
            if (setMessage) {
                jstring messageStr = env->NewStringUTF(message.c_str());
                env->CallObjectMethod(alertBuilder, setMessage, messageStr);
                env->DeleteLocalRef(messageStr);
            }
            
            // Set cancelable
            jmethodID setCancelable = getMethodID(env, alertClass, OBFUSCATE("setCancelable"), 
                                                OBFUSCATE("(Z)Landroid/app/AlertDialog$Builder;"));
            if (setCancelable) {
                env->CallObjectMethod(alertBuilder, setCancelable, JNI_FALSE);
            }
            
            // Set positive button
            jmethodID setPositiveButton = getMethodID(env, alertClass, OBFUSCATE("setPositiveButton"), 
                                                    OBFUSCATE("(Ljava/lang/CharSequence;Landroid/content/DialogInterface$OnClickListener;)Landroid/app/AlertDialog$Builder;"));
            if (setPositiveButton) {
                jstring positiveStr = env->NewStringUTF(positiveText.c_str());
                env->CallObjectMethod(alertBuilder, setPositiveButton, positiveStr, nullptr);
                env->DeleteLocalRef(positiveStr);
            }
            
            // Create and show dialog
            jmethodID create = getMethodID(env, alertClass, OBFUSCATE("create"), 
                                         OBFUSCATE("()Landroid/app/AlertDialog;"));
            if (create) {
                jobject dialog = env->CallObjectMethod(alertBuilder, create);
                if (dialog) {
                    jclass dialogClass = env->GetObjectClass(dialog);
                    jmethodID show = getMethodID(env, dialogClass, OBFUSCATE("show"), OBFUSCATE("()V"));
                    if (show) {
                        env->CallVoidMethod(dialog, show);
                    }
                    env->DeleteLocalRef(dialogClass);
                    env->DeleteLocalRef(dialog);
                }
            }
            
            env->DeleteLocalRef(alertBuilder);
            env->DeleteLocalRef(alertClass);
            
        } catch (...) {
            LOGE(OBFUSCATE("Error showing dialog"));
        }
    }
    
    void UIUtils::showToast(JNIEnv* env, jobject context, 
                           const std::string& text, int duration) {
        try {
            jstring textStr = env->NewStringUTF(text.c_str());
            jclass toastClass = findClass(env, OBFUSCATE("android/widget/Toast"));
            
            if (toastClass && textStr) {
                jmethodID makeText = env->GetStaticMethodID(toastClass, OBFUSCATE("makeText"),
                    OBFUSCATE("(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;"));
                
                if (makeText) {
                    jobject toast = env->CallStaticObjectMethod(toastClass, makeText, 
                                                               context, textStr, duration);
                    if (toast) {
                        jmethodID show = getMethodID(env, toastClass, OBFUSCATE("show"), OBFUSCATE("()V"));
                        if (show) {
                            env->CallVoidMethod(toast, show);
                        }
                        env->DeleteLocalRef(toast);
                    }
                }
                env->DeleteLocalRef(toastClass);
            }
            
            if (textStr) env->DeleteLocalRef(textStr);
            
        } catch (...) {
            LOGE(OBFUSCATE("Error showing toast"));
        }
    }
    
    bool UIUtils::setText(JNIEnv* env, jobject textView, const std::string& htmlText) {
        try {
            // Get HTML class and fromHtml method
            jclass htmlClass = findClass(env, OBFUSCATE("android/text/Html"));
            if (!htmlClass) return false;
            
            jmethodID fromHtml = env->GetStaticMethodID(htmlClass, OBFUSCATE("fromHtml"), 
                                                       OBFUSCATE("(Ljava/lang/String;)Landroid/text/Spanned;"));
            if (!fromHtml) {
                env->DeleteLocalRef(htmlClass);
                return false;
            }
            
            // Get TextView class and setText method
            jclass textViewClass = env->GetObjectClass(textView);
            jmethodID setText = getMethodID(env, textViewClass, OBFUSCATE("setText"), 
                                          OBFUSCATE("(Ljava/lang/CharSequence;)V"));
            
            if (setText) {
                jstring htmlStr = env->NewStringUTF(htmlText.c_str());
                jobject spanned = env->CallStaticObjectMethod(htmlClass, fromHtml, htmlStr);
                
                if (spanned) {
                    env->CallVoidMethod(textView, setText, spanned);
                    env->DeleteLocalRef(spanned);
                }
                
                env->DeleteLocalRef(htmlStr);
                env->DeleteLocalRef(textViewClass);
                env->DeleteLocalRef(htmlClass);
                return true;
            }
            
            env->DeleteLocalRef(textViewClass);
            env->DeleteLocalRef(htmlClass);
            return false;
            
        } catch (...) {
            LOGE(OBFUSCATE("Error setting text"));
            return false;
        }
    }
    
    // Helper methods
    jclass UIUtils::findClass(JNIEnv* env, const std::string& className) {
        return env->FindClass(className.c_str());
    }
    
    jmethodID UIUtils::getMethodID(JNIEnv* env, jclass clazz, 
                                  const std::string& methodName, 
                                  const std::string& signature) {
        return env->GetMethodID(clazz, methodName.c_str(), signature.c_str());
    }
}

#endif // ENHANCED_SETUP_H
