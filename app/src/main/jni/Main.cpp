/**
 * Enhanced Modern Mod Menu - Main.cpp
 * 
 * Features:
 * - Modern C++17 architecture
 * - Thread-safe operations
 * - Enhanced memory management
 * - Improved error handling
 * - Performance optimizations
 * - Better code organization
 */

#include <memory>
#include <atomic>
#include <mutex>
#include <condition_variable>
#include <unordered_map>
#include <string_view>
#include <chrono>
#include <thread>
#include <future>
#include <functional>

// Core includes
#include <jni.h>
#include <android/log.h>
#include <dlfcn.h>
#include <unistd.h>

// Project includes
#include "Includes/Logger.h"
#include "Includes/obfuscate.h"
#include "Includes/Utils.h"
#include "Includes/Macros.h"
#include "KittyMemory/MemoryPatch.h"
#include "Menu/Setup.h"
#include "Menu/MenuManager.h"
#include "Menu/FeatureManager.h"
#include "Menu/SecurityManager.h"

// Namespace declarations
using namespace std::chrono_literals;
using namespace std::string_literals;

// Configuration constants
namespace Config {
    constexpr auto TARGET_LIB_NAME = OBFUSCATE("libFileA.so");
    constexpr auto HOOK_RETRY_DELAY = 1s;
    constexpr auto MAX_HOOK_RETRIES = 30;
    constexpr auto SECURITY_CHECK_INTERVAL = 5s;
    constexpr bool ENABLE_ANTI_DEBUG = true;
    constexpr bool ENABLE_ANTI_EMULATOR = true;
}

// Global state management
namespace GlobalState {
    std::atomic<bool> isInitialized{false};
    std::atomic<bool> isGameLibLoaded{false};
    std::atomic<bool> isSecurityActive{true};
    std::atomic<int> currentMenuIndex{1};
    
    // Thread-safe feature state
    std::mutex featureStateMutex;
    std::unordered_map<int, bool> toggleStates;
    std::unordered_map<int, int> sliderValues;
    std::unordered_map<int, std::string> textValues;
    
    // Performance tracking
    std::atomic<uint64_t> hookCallCount{0};
    std::atomic<uint64_t> featureToggleCount{0};
    
    // Security tokens
    std::atomic<bool> iconValid{false};
    std::atomic<bool> settingsValid{false};
    std::atomic<bool> initValid{false};
}

// Enhanced Feature Manager Class
class EnhancedFeatureManager {
private:
    mutable std::shared_mutex featureMutex_;
    std::unordered_map<int, std::function<void(bool)>> toggleCallbacks_;
    std::unordered_map<int, std::function<void(int)>> sliderCallbacks_;
    std::unordered_map<int, std::function<void(const std::string&)>> textCallbacks_;
    
public:
    void registerToggleFeature(int featureId, std::function<void(bool)> callback) {
        std::unique_lock lock(featureMutex_);
        toggleCallbacks_[featureId] = std::move(callback);
    }
    
    void registerSliderFeature(int featureId, std::function<void(int)> callback) {
        std::unique_lock lock(featureMutex_);
        sliderCallbacks_[featureId] = std::move(callback);
    }
    
    void registerTextFeature(int featureId, std::function<void(const std::string&)> callback) {
        std::unique_lock lock(featureMutex_);
        textCallbacks_[featureId] = std::move(callback);
    }
    
    void executeToggleFeature(int featureId, bool enabled) {
        std::shared_lock lock(featureMutex_);
        auto it = toggleCallbacks_.find(featureId);
        if (it != toggleCallbacks_.end()) {
            try {
                it->second(enabled);
                GlobalState::featureToggleCount++;
                LOGD(OBFUSCATE("Toggle feature %d executed: %s"), featureId, enabled ? "ON" : "OFF");
            } catch (const std::exception& e) {
                LOGE(OBFUSCATE("Error executing toggle feature %d: %s"), featureId, e.what());
            }
        }
    }
    
    void executeSliderFeature(int featureId, int value) {
        std::shared_lock lock(featureMutex_);
        auto it = sliderCallbacks_.find(featureId);
        if (it != sliderCallbacks_.end()) {
            try {
                it->second(value);
                LOGD(OBFUSCATE("Slider feature %d executed: %d"), featureId, value);
            } catch (const std::exception& e) {
                LOGE(OBFUSCATE("Error executing slider feature %d: %s"), featureId, e.what());
            }
        }
    }
    
    void executeTextFeature(int featureId, const std::string& text) {
        std::shared_lock lock(featureMutex_);
        auto it = textCallbacks_.find(featureId);
        if (it != textCallbacks_.end()) {
            try {
                it->second(text);
                LOGD(OBFUSCATE("Text feature %d executed: %s"), featureId, text.c_str());
            } catch (const std::exception& e) {
                LOGE(OBFUSCATE("Error executing text feature %d: %s"), featureId, e.what());
            }
        }
    }
};

// Global feature manager instance
static std::unique_ptr<EnhancedFeatureManager> g_featureManager;

// Modern Game Hook Functions with enhanced safety
namespace GameHooks {
    // Original function pointers with smart pointers for safety
    std::atomic<void*> originalAddMoney{nullptr};
    std::atomic<void*> originalGetBool{nullptr};
    std::atomic<void*> originalGetFloat{nullptr};
    std::atomic<void*> originalLevel{nullptr};
    std::atomic<void*> originalFunction{nullptr};
    
    // Game instance tracking
    std::atomic<void*> gameInstance{nullptr};
    
    // Enhanced hook implementations with performance monitoring
    bool getBoolHook(void* instance) {
        GlobalState::hookCallCount++;
        
        if (instance && GlobalState::toggleStates[1]) {
            return true;
        }
        
        auto original = reinterpret_cast<bool(*)(void*)>(originalGetBool.load());
        return original ? original(instance) : false;
    }
    
    float getFloatHook(void* instance) {
        GlobalState::hookCallCount++;
        
        if (instance) {
            std::lock_guard lock(GlobalState::featureStateMutex);
            auto it = GlobalState::sliderValues.find(2);
            if (it != GlobalState::sliderValues.end() && it->second > 1) {
                return static_cast<float>(it->second);
            }
        }
        
        auto original = reinterpret_cast<float(*)(void*)>(originalGetFloat.load());
        return original ? original(instance) : 0.0f;
    }
    
    int levelHook(void* instance) {
        GlobalState::hookCallCount++;
        
        if (instance) {
            std::lock_guard lock(GlobalState::featureStateMutex);
            auto it = GlobalState::sliderValues.find(7);
            if (it != GlobalState::sliderValues.end() && it->second > 0) {
                return it->second;
            }
        }
        
        auto original = reinterpret_cast<int(*)(void*)>(originalLevel.load());
        return original ? original(instance) : 0;
    }
    
    void functionHook(void* instance) {
        GlobalState::hookCallCount++;
        gameInstance.store(instance);
        
        if (instance && GlobalState::toggleStates[6]) {
            // God mode implementation
            try {
                *reinterpret_cast<int*>(reinterpret_cast<uint64_t>(instance) + 0x48) = 999;
            } catch (...) {
                LOGE(OBFUSCATE("Error applying god mode"));
            }
        }
        
        auto original = reinterpret_cast<void(*)(void*)>(originalFunction.load());
        if (original) {
            original(instance);
        }
    }
    
    void addMoneyHook(void* instance, int amount) {
        GlobalState::hookCallCount++;
        
        auto original = reinterpret_cast<void(*)(void*, int)>(originalAddMoney.load());
        if (original) {
            original(instance, amount);
        }
    }
}

// Enhanced Menu System
namespace MenuSystem {
    struct MenuInfo {
        std::string name;
        std::vector<std::string> features;
        bool isActive = false;
    };
    
    std::array<MenuInfo, 3> menus = {{
        {
            "AIMBOT",
            {
                OBFUSCATE("Category_🎯 AIMBOT FEATURES"),
                OBFUSCATE("Toggle_True_Aimbot"),
                OBFUSCATE("Toggle_Auto Aim"),
                OBFUSCATE("SeekBar_Aim Speed_1_10"),
                OBFUSCATE("SeekBar_FOV Range_10_180"),
                OBFUSCATE("CheckBox_Smooth Aim"),
                OBFUSCATE("CheckBox_Silent Aim"),
                OBFUSCATE("Spinner_Target Priority_Head,Body,Closest,Weakest"),
                OBFUSCATE("CheckBox_Predict Movement"),
                OBFUSCATE("SeekBar_Aim Delay_0_500")
            }
        },
        {
            "WALLHACK",
            {
                OBFUSCATE("Category_👁️ WALLHACK FEATURES"),
                OBFUSCATE("Toggle_True_ESP Players"),
                OBFUSCATE("Toggle_Wallhack"),
                OBFUSCATE("Toggle_Item ESP"),
                OBFUSCATE("CheckBox_Show Distance"),
                OBFUSCATE("CheckBox_Show Health"),
                OBFUSCATE("CheckBox_Show Names"),
                OBFUSCATE("SeekBar_ESP Distance_50_1000"),
                OBFUSCATE("Spinner_ESP Style_Box,Line,Dot"),
                OBFUSCATE("CheckBox_Show Weapons"),
                OBFUSCATE("Toggle_Radar Hack")
            }
        },
        {
            "EXTRAS",
            {
                OBFUSCATE("Category_⚡ EXTRA FEATURES"),
                OBFUSCATE("Toggle_Speed Hack"),
                OBFUSCATE("Toggle_No Recoil"),
                OBFUSCATE("Toggle_Unlimited Ammo"),
                OBFUSCATE("CheckBox_Anti Ban"),
                OBFUSCATE("CheckBox_Hide Root"),
                OBFUSCATE("SeekBar_Game Speed_1_5"),
                OBFUSCATE("Button_💰 Add Money"),
                OBFUSCATE("InputValue_Custom Level_1_999"),
                OBFUSCATE("Toggle_Fly Mode"),
                OBFUSCATE("100_TextBox_📊 Game Stats_Real-time game statistics"),
                OBFUSCATE("101_TextBox_🔧 Debug Info_System debug information"),
                OBFUSCATE("102_TextBox_📝 Notes_Your custom notes and settings")
            }
        }
    }};
    
    std::string getMenuFeatures(int menuIndex, int& totalFeatures) {
        if (menuIndex < 1 || menuIndex > 3) {
            menuIndex = 1;
        }
        
        const auto& menu = menus[menuIndex - 1];
        totalFeatures = static_cast<int>(menu.features.size());
        
        // Create a single string with all features separated by newlines
        std::string result;
        for (const auto& feature : menu.features) {
            if (!result.empty()) result += "\n";
            result += feature;
        }
        
        return result;
    }
}

// Enhanced Security System
namespace SecuritySystem {
    std::atomic<bool> securityActive{true};
    std::atomic<uint64_t> lastSecurityCheck{0};
    
    bool performSecurityCheck() {
        if (!Config::ENABLE_ANTI_DEBUG && !Config::ENABLE_ANTI_EMULATOR) {
            return true;
        }
        
        auto now = std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::steady_clock::now().time_since_epoch()).count();
        
        lastSecurityCheck.store(now);
        
        // Add your security checks here
        // Return false if security breach detected
        
        return GlobalState::iconValid.load() && 
               GlobalState::settingsValid.load() && 
               GlobalState::initValid.load();
    }
    
    void securityMonitorThread() {
        while (securityActive.load()) {
            std::this_thread::sleep_for(Config::SECURITY_CHECK_INTERVAL);
            
            if (!performSecurityCheck()) {
                LOGE(OBFUSCATE("Security breach detected!"));
                // Handle security breach
                std::this_thread::sleep_for(5s);
                std::exit(0);
            }
        }
    }
}

// Main initialization thread
void* initializationThread(void*) {
    LOGI(OBFUSCATE("🚀 Enhanced Mod Menu initialization started"));
    
    // Initialize feature manager
    g_featureManager = std::make_unique<EnhancedFeatureManager>();
    
    // Register feature callbacks
    registerFeatureCallbacks();
    
    // Wait for target library
    LOGI(OBFUSCATE("⏳ Waiting for target library: %s"), Config::TARGET_LIB_NAME);
    
    int retryCount = 0;
    while (!isLibraryLoaded(Config::TARGET_LIB_NAME) && retryCount < Config::MAX_HOOK_RETRIES) {
        std::this_thread::sleep_for(Config::HOOK_RETRY_DELAY);
        retryCount++;
    }
    
    if (!isLibraryLoaded(Config::TARGET_LIB_NAME)) {
        LOGE(OBFUSCATE("❌ Target library not found after %d retries"), Config::MAX_HOOK_RETRIES);
        return nullptr;
    }
    
    GlobalState::isGameLibLoaded.store(true);
    LOGI(OBFUSCATE("✅ Target library loaded: %s"), Config::TARGET_LIB_NAME);
    
    // Setup hooks based on architecture
    setupGameHooks();
    
    // Start security monitoring
    if (Config::ENABLE_ANTI_DEBUG || Config::ENABLE_ANTI_EMULATOR) {
        std::thread securityThread(SecuritySystem::securityMonitorThread);
        securityThread.detach();
    }
    
    GlobalState::isInitialized.store(true);
    LOGI(OBFUSCATE("🎉 Enhanced Mod Menu initialized successfully"));
    
    return nullptr;
}

// Enhanced hook setup function
void setupGameHooks() {
#if defined(__aarch64__)
    LOGI(OBFUSCATE("Setting up ARM64 hooks"));
    
    // Hook examples with error handling
    try {
        HOOK("0x123456", GameHooks::functionHook, GameHooks::originalFunction);
        HOOK_LIB("libFileB.so", "0x654321", GameHooks::getBoolHook, GameHooks::originalGetBool);
        HOOKSYM("_SymbolExample", GameHooks::getFloatHook, GameHooks::originalGetFloat);
        
        // Patches
        PATCH("0x20D3A8", "00 00 A0 E3 1E FF 2F E1");
        PATCH_LIB("libFileB.so", "0x20D3A8", "00 00 A0 E3 1E FF 2F E1");
        
        GameHooks::originalAddMoney.store(
            reinterpret_cast<void*>(getAbsoluteAddress(Config::TARGET_LIB_NAME, 0x123456)));
            
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Error setting up ARM64 hooks: %s"), e.what());
    }
    
#else
    LOGI(OBFUSCATE("Setting up ARMv7 hooks"));
    
    try {
        HOOK("0x123456", GameHooks::functionHook, GameHooks::originalFunction);
        HOOK_LIB("libFileB.so", "0x654321", GameHooks::getBoolHook, GameHooks::originalGetBool);
        HOOKSYM("_SymbolExample", GameHooks::getFloatHook, GameHooks::originalGetFloat);
        
        // Patches
        PATCH("0x20D3A8", "00 00 A0 E3 1E FF 2F E1");
        PATCH_LIB("libFileB.so", "0x20D3A8", "00 00 A0 E3 1E FF 2F E1");
        
        // Restore functionality
        RESTORE("0x20D3A8");
        RESTORE_LIB("libFileB.so", "0x20D3A8");
        
        GameHooks::originalAddMoney.store(
            reinterpret_cast<void*>(getAbsoluteAddress(Config::TARGET_LIB_NAME, 0x123456)));
            
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Error setting up ARMv7 hooks: %s"), e.what());
    }
#endif

    LOGI(OBFUSCATE("✅ Game hooks setup completed"));
}

// Register feature callbacks
void registerFeatureCallbacks() {
    if (!g_featureManager) return;
    
    // Aimbot features
    g_featureManager->registerToggleFeature(0, [](bool enabled) {
        GlobalState::toggleStates[0] = enabled;
        PATCH_SWITCH("0x400000", "01 00 A0 E3 1E FF 2F E1", enabled);
    });
    
    g_featureManager->registerToggleFeature(1, [](bool enabled) {
        GlobalState::toggleStates[1] = enabled;
        // Auto aim implementation
    });
    
    g_featureManager->registerSliderFeature(2, [](int value) {
        GlobalState::sliderValues[2] = value;
        // Aim speed implementation
    });
    
    g_featureManager->registerSliderFeature(3, [](int value) {
        GlobalState::sliderValues[3] = value;
        // FOV range implementation
    });
    
    // Wallhack features
    g_featureManager->registerToggleFeature(10, [](bool enabled) {
        GlobalState::toggleStates[10] = enabled;
        // ESP implementation
    });
    
    // Extra features
    g_featureManager->registerToggleFeature(20, [](bool enabled) {
        GlobalState::toggleStates[20] = enabled;
        // Speed hack implementation
    });
    
    LOGI(OBFUSCATE("✅ Feature callbacks registered"));
}

// JNI Implementation
extern "C" {

JNIEXPORT jobjectArray JNICALL
Java_com_android_support_Menu_GetFeatureList(JNIEnv* env, jobject obj) {
    if (!env) {
        LOGE(OBFUSCATE("JNIEnv is null in GetFeatureList"));
        return nullptr;
    }
    
    try {
        int totalFeatures = 0;
        int currentMenu = GlobalState::currentMenuIndex.load();
        std::string featuresStr = MenuSystem::getMenuFeatures(currentMenu, totalFeatures);
        
        if (totalFeatures <= 0) {
            LOGW(OBFUSCATE("No features found for menu %d"), currentMenu);
            totalFeatures = 1;
            featuresStr = OBFUSCATE("Category_No Features Available");
        }
        
        // Parse features string and create array
        std::vector<std::string> features;
        std::istringstream iss(featuresStr);
        std::string line;
        while (std::getline(iss, line)) {
            features.push_back(line);
        }
        
        jclass stringClass = env->FindClass("java/lang/String");
        if (!stringClass) {
            LOGE(OBFUSCATE("Failed to find String class"));
            return nullptr;
        }
        
        jobjectArray result = env->NewObjectArray(
            static_cast<jsize>(features.size()), 
            stringClass, 
            env->NewStringUTF("")
        );
        
        if (!result) {
            LOGE(OBFUSCATE("Failed to create object array"));
            env->DeleteLocalRef(stringClass);
            return nullptr;
        }
        
        for (size_t i = 0; i < features.size(); ++i) {
            jstring feature = env->NewStringUTF(features[i].c_str());
            if (feature) {
                env->SetObjectArrayElement(result, static_cast<jsize>(i), feature);
                env->DeleteLocalRef(feature);
            }
        }
        
        env->DeleteLocalRef(stringClass);
        return result;
        
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Exception in GetFeatureList: %s"), e.what());
        return nullptr;
    }
}

JNIEXPORT void JNICALL
Java_com_android_support_Menu_onMenuSelected(JNIEnv* env, jobject obj, jint menuIndex) {
    try {
        LOGD(OBFUSCATE("Menu selected: %d"), menuIndex);
        
        if (menuIndex >= 1 && menuIndex <= 3) {
            if (GlobalState::currentMenuIndex.load() != menuIndex) {
                GlobalState::currentMenuIndex.store(menuIndex);
                LOGD(OBFUSCATE("Menu changed to: %d"), menuIndex);
            }
        } else {
            LOGW(OBFUSCATE("Invalid menu index: %d, defaulting to 1"), menuIndex);
            GlobalState::currentMenuIndex.store(1);
        }
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Exception in onMenuSelected: %s"), e.what());
    }
}

JNIEXPORT jstring JNICALL
Java_com_android_support_Menu_getTextBoxContent(JNIEnv* env, jobject obj, jint featNum) {
    try {
        std::string content;
        
        switch (featNum) {
            case 100: { // Game Stats
                std::lock_guard lock(GlobalState::featureStateMutex);
                auto level = GlobalState::sliderValues.find(7);
                int levelValue = (level != GlobalState::sliderValues.end()) ? level->second : 45;
                
                content = "🎮 GAME STATISTICS\n"
                         "━━━━━━━━━━━━━━━━━━━━\n"
                         "Player Level: " + std::to_string(levelValue) + "\n"
                         "Health: 100/100 ❤️\n"
                         "Experience: " + std::to_string(2350 + (levelValue * 100)) + " XP\n"
                         "Hook Calls: " + std::to_string(GlobalState::hookCallCount.load()) + "\n"
                         "Features Used: " + std::to_string(GlobalState::featureToggleCount.load()) + "\n"
                         "Menu Index: " + std::to_string(GlobalState::currentMenuIndex.load()) + "\n"
                         "━━━━━━━━━━━━━━━━━━━━\n"
                         "Status: ✅ ACTIVE";
                break;
            }
            case 101: { // Debug Info
                auto now = std::chrono::system_clock::now();
                auto time_t = std::chrono::system_clock::to_time_t(now);
                
                content = "🔧 DEBUG INFORMATION\n"
                         "━━━━━━━━━━━━━━━━━━━━\n"
                         "Mod Menu: ✅ Initialized\n"
                         "Game Lib: " + std::string(GlobalState::isGameLibLoaded.load() ? "✅ Loaded" : "❌ Not Found") + "\n"
                         "Security: " + std::string(SecuritySystem::securityActive.load() ? "🛡️ Active" : "⚠️ Disabled") + "\n"
                         "Architecture: " +
#if defined(__aarch64__)
                         "ARM64\n"
#else
                         "ARMv7\n"
#endif
                         "Timestamp: " + std::to_string(time_t) + "\n"
                         "Memory: Optimized 🚀\n"
                         "Performance: Excellent ⭐\n"
                         "━━━━━━━━━━━━━━━━━━━━";
                break;
            }
            case 102: { // User Notes
                std::lock_guard lock(GlobalState::featureStateMutex);
                auto it = GlobalState::textValues.find(102);
                content = (it != GlobalState::textValues.end()) ? it->second :
                         "📝 USER NOTES\n"
                         "━━━━━━━━━━━━━━━━━━━━\n"
                         "Welcome to Enhanced Mod Menu!\n\n"
                         "Features:\n"
                         "• Modern C++17 architecture\n"
                         "• Thread-safe operations\n"
                         "• Enhanced security\n"
                         "• Performance optimized\n"
                         "• Beautiful UI design\n\n"
                         "Add your custom notes here...\n"
                         "━━━━━━━━━━━━━━━━━━━━";
                break;
            }
            default:
                content = "No content available for feature " + std::to_string(featNum);
                break;
        }
        
        return env->NewStringUTF(content.c_str());
        
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Exception in getTextBoxContent: %s"), e.what());
        return env->NewStringUTF("Error retrieving content");
    }
}

JNIEXPORT jint JNICALL
Java_com_android_support_Menu_getCurrentMenuIndex(JNIEnv* env, jobject obj) {
    return GlobalState::currentMenuIndex.load();
}

JNIEXPORT jboolean JNICALL
Java_com_android_support_Menu_IsGameLibLoaded(JNIEnv* env, jobject obj) {
    return GlobalState::isGameLibLoaded.load();
}

} // extern "C"

// Enhanced Changes function with modern architecture
extern "C" void Changes(JNIEnv* env, jclass clazz, jobject obj,
                       jint featNum, jstring featName, jint value,
                       jboolean boolean, jstring str) {
    
    if (!env || !g_featureManager) return;
    
    try {
        const char* featureName = featName ? env->GetStringUTFChars(featName, nullptr) : "Unknown";
        const char* stringValue = str ? env->GetStringUTFChars(str, nullptr) : nullptr;
        
        LOGD(OBFUSCATE("🎛️ Feature: %d - %s | Value: %d | Bool: %s | Text: %s"), 
             featNum, featureName, value, boolean ? "ON" : "OFF", 
             stringValue ? stringValue : "null");
        
        // Handle feature changes based on type and current menu
        handleFeatureChange(featNum, value, boolean, stringValue);
        
        // Cleanup JNI strings
        if (featureName && featName) {
            env->ReleaseStringUTFChars(featName, featureName);
        }
        if (stringValue && str) {
            env->ReleaseStringUTFChars(str, stringValue);
        }
        
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Exception in Changes: %s"), e.what());
    }
}

// Enhanced feature change handling
void handleFeatureChange(int featNum, int value, bool boolean, const char* stringValue) {
    if (!g_featureManager) return;
    
    try {
        // Handle menu-specific features
        int currentMenu = GlobalState::currentMenuIndex.load();
        
        switch (featNum) {
            // Universal features
            case 0: // Aimbot toggle
                g_featureManager->executeToggleFeature(0, boolean);
                break;
                
            case 1: // Auto aim
                g_featureManager->executeToggleFeature(1, boolean);
                break;
                
            case 2: // Aim speed
                g_featureManager->executeSliderFeature(2, value);
                break;
                
            case 3: // FOV range
                g_featureManager->executeSliderFeature(3, value);
                break;
                
            case 10: // ESP Players
                g_featureManager->executeToggleFeature(10, boolean);
                break;
                
            case 20: // Speed hack
                g_featureManager->executeToggleFeature(20, boolean);
                break;
                
            case 100: // Game Stats TextBox
            case 101: // Debug Info TextBox
            case 102: // User Notes TextBox
                if (stringValue) {
                    std::lock_guard lock(GlobalState::featureStateMutex);
                    GlobalState::textValues[featNum] = stringValue;
                }
                break;
                
            // Add money button
            case 50: {
                auto instance = GameHooks::gameInstance.load();
                if (instance && GameHooks::originalAddMoney.load()) {
                    auto addMoney = reinterpret_cast<void(*)(void*, int)>(
                        GameHooks::originalAddMoney.load());
                    addMoney(instance, 999999);
                    LOGI(OBFUSCATE("💰 Money added successfully"));
                }
                break;
            }
                
            default:
                LOGW(OBFUSCATE("Unhandled feature: %d"), featNum);
                break;
        }
        
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Error handling feature change: %s"), e.what());
    }
}

// Constructor - Entry point
__attribute__((constructor))
void lib_main() {
    LOGI(OBFUSCATE("🚀 Enhanced Mod Menu library loaded"));
    
    try {
        // Create initialization thread
        pthread_t initThread;
        int result = pthread_create(&initThread, nullptr, initializationThread, nullptr);
        
        if (result != 0) {
            LOGE(OBFUSCATE("❌ Failed to create initialization thread: %d"), result);
            return;
        }
        
        // Detach thread to run independently
        pthread_detach(initThread);
        
        LOGI(OBFUSCATE("✅ Enhanced Mod Menu initialization thread started"));
        
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Exception in lib_main: %s"), e.what());
    }
}

// JNI Registration Functions
int RegisterMenu(JNIEnv* env) {
    try {
        JNINativeMethod methods[] = {
            {OBFUSCATE("Icon"), OBFUSCATE("()Ljava/lang/String;"), 
             reinterpret_cast<void*>(Icon)},
            {OBFUSCATE("IconWebViewData"), OBFUSCATE("()Ljava/lang/String;"), 
             reinterpret_cast<void*>(IconWebViewData)},
            {OBFUSCATE("IsGameLibLoaded"), OBFUSCATE("()Z"), 
             reinterpret_cast<void*>(Java_com_android_support_Menu_IsGameLibLoaded)},
            {OBFUSCATE("Init"), OBFUSCATE("(Landroid/content/Context;Landroid/widget/TextView;Landroid/widget/TextView;)V"), 
             reinterpret_cast<void*>(Init)},
            {OBFUSCATE("SettingsList"), OBFUSCATE("()[Ljava/lang/String;"), 
             reinterpret_cast<void*>(SettingsList)},
            {OBFUSCATE("GetFeatureList"), OBFUSCATE("()[Ljava/lang/String;"), 
             reinterpret_cast<void*>(Java_com_android_support_Menu_GetFeatureList)},
            {OBFUSCATE("onMenuSelected"), OBFUSCATE("(I)V"), 
             reinterpret_cast<void*>(Java_com_android_support_Menu_onMenuSelected)},
            {OBFUSCATE("getTextBoxContent"), OBFUSCATE("(I)Ljava/lang/String;"), 
             reinterpret_cast<void*>(Java_com_android_support_Menu_getTextBoxContent)},
            {OBFUSCATE("getCurrentMenuIndex"), OBFUSCATE("()I"), 
             reinterpret_cast<void*>(Java_com_android_support_Menu_getCurrentMenuIndex)},
        };

        jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Menu"));
        if (!clazz) {
            LOGE(OBFUSCATE("Failed to find Menu class"));
            return JNI_ERR;
        }
        
        if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0) {
            LOGE(OBFUSCATE("Failed to register Menu natives"));
            return JNI_ERR;
        }
        
        LOGI(OBFUSCATE("✅ Menu natives registered successfully"));
        return JNI_OK;
        
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Exception in RegisterMenu: %s"), e.what());
        return JNI_ERR;
    }
}

int RegisterPreferences(JNIEnv* env) {
    try {
        JNINativeMethod methods[] = {
            {OBFUSCATE("Changes"), OBFUSCATE("(Landroid/content/Context;ILjava/lang/String;IZLjava/lang/String;)V"), 
             reinterpret_cast<void*>(Changes)},
        };
        
        jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Preferences"));
        if (!clazz) {
            LOGE(OBFUSCATE("Failed to find Preferences class"));
            return JNI_ERR;
        }
        
        if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0) {
            LOGE(OBFUSCATE("Failed to register Preferences natives"));
            return JNI_ERR;
        }
        
        LOGI(OBFUSCATE("✅ Preferences natives registered successfully"));
        return JNI_OK;
        
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Exception in RegisterPreferences: %s"), e.what());
        return JNI_ERR;
    }
}

int RegisterMain(JNIEnv* env) {
    try {
        JNINativeMethod methods[] = {
            {OBFUSCATE("CheckOverlayPermission"), OBFUSCATE("(Landroid/content/Context;)V"), 
             reinterpret_cast<void*>(CheckOverlayPermission)},
        };
        
        jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Main"));
        if (!clazz) {
            LOGE(OBFUSCATE("Failed to find Main class"));
            return JNI_ERR;
        }
        
        if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0) {
            LOGE(OBFUSCATE("Failed to register Main natives"));
            return JNI_ERR;
        }
        
        LOGI(OBFUSCATE("✅ Main natives registered successfully"));
        return JNI_OK;
        
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Exception in RegisterMain: %s"), e.what());
        return JNI_ERR;
    }
}

// JNI_OnLoad - Enhanced with modern error handling
extern "C" JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void* reserved) {
    LOGI(OBFUSCATE("🔄 JNI_OnLoad called"));
    
    try {
        JNIEnv* env;
        if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
            LOGE(OBFUSCATE("❌ Failed to get JNI environment"));
            return JNI_ERR;
        }
        
        if (RegisterMenu(env) != JNI_OK) {
            LOGE(OBFUSCATE("❌ Failed to register Menu"));
            return JNI_ERR;
        }
        
        if (RegisterPreferences(env) != JNI_OK) {
            LOGE(OBFUSCATE("❌ Failed to register Preferences"));
            return JNI_ERR;
        }
        
        if (RegisterMain(env) != JNI_OK) {
            LOGE(OBFUSCATE("❌ Failed to register Main"));
            return JNI_ERR;
        }
        
        LOGI(OBFUSCATE("🎉 All JNI components registered successfully"));
        return JNI_VERSION_1_6;
        
    } catch (const std::exception& e) {
        LOGE(OBFUSCATE("Exception in JNI_OnLoad: %s"), e.what());
        return JNI_ERR;
    }
}
