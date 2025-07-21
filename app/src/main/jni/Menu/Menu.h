/**
 * Enhanced Modern Menu Header - Menu.h
 * 
 * Features:
 * - Modern C++17 architecture
 * - Enhanced menu management
 * - Better icon and settings handling
 * - Improved security features
 * - Performance optimizations
 */

#ifndef ENHANCED_MENU_H
#define ENHANCED_MENU_H

#include <jni.h>
#include <string>
#include <vector>
#include <memory>
#include <atomic>
#include <functional>
#include <unordered_map>

#include "Includes/Logger.h"
#include "Includes/obfuscate.h"

// Enhanced namespace for menu functionality
namespace EnhancedMenu {
    
    // Constants and configuration
    namespace Config {
        constexpr int MAX_MENU_COUNT = 3;
        constexpr int MAX_FEATURES_PER_MENU = 20;
        constexpr int TEXTBOX_GAME_STATS = 100;
        constexpr int TEXTBOX_DEBUG_INFO = 101;
        constexpr int TEXTBOX_USER_NOTES = 102;
    }
    
    // Menu state management
    namespace State {
        extern std::atomic<bool> iconValid;
        extern std::atomic<bool> settingsValid;
        extern std::atomic<bool> initValid;
        extern std::atomic<bool> overlayEnabled;
        extern std::atomic<int> currentMenuIndex;
        extern std::string overlayCustomText;
        extern std::atomic<int> overlayTextSize;
    }
    
    // Enhanced menu data structures
    struct FeatureInfo {
        int id;
        std::string name;
        std::string type; // Toggle, SeekBar, CheckBox, etc.
        std::string description;
        bool enabled = false;
        int value = 0;
        std::string textContent;
    };
    
    struct MenuInfo {
        int index;
        std::string name;
        std::string displayName;
        std::string icon;
        std::vector<FeatureInfo> features;
        bool isActive = false;
    };
    
    // Enhanced menu manager class
    class MenuManager {
    private:
        std::vector<std::unique_ptr<MenuInfo>> menus_;
        std::unordered_map<int, std::function<void(bool)>> toggleCallbacks_;
        std::unordered_map<int, std::function<void(int)>> sliderCallbacks_;
        std::unordered_map<int, std::function<void(const std::string&)>> textCallbacks_;
        
    public:
        MenuManager();
        ~MenuManager() = default;
        
        // Menu management
        bool initializeMenus();
        MenuInfo* getMenu(int index);
        std::vector<std::string> getMenuFeatures(int menuIndex);
        bool setActiveMenu(int menuIndex);
        int getActiveMenuIndex() const;
        
        // Feature management
        bool addFeature(int menuIndex, const FeatureInfo& feature);
        bool removeFeature(int menuIndex, int featureId);
        FeatureInfo* getFeature(int menuIndex, int featureId);
        
        // Callback registration
        void registerToggleCallback(int featureId, std::function<void(bool)> callback);
        void registerSliderCallback(int featureId, std::function<void(int)> callback);
        void registerTextCallback(int featureId, std::function<void(const std::string&)> callback);
        
        // Feature execution
        void executeToggleFeature(int featureId, bool enabled);
        void executeSliderFeature(int featureId, int value);
        void executeTextFeature(int featureId, const std::string& text);
        
        // Utility functions
        std::string generateFeatureList(int menuIndex);
        void updateFeatureState(int featureId, bool enabled, int value = 0, const std::string& text = "");
    };
    
    // Enhanced icon manager
    class IconManager {
    public:
        static std::string getIconData();
        static std::string getWebViewIconData();
        static bool validateIcon();
        static void setIconValid(bool valid);
        
        // Enhanced icon features
        static std::string getCustomIcon(const std::string& iconName);
        static bool loadIconFromBase64(const std::string& base64Data);
        static std::string generateDynamicIcon(const std::string& text, int size = 50);
    };
    
    // Enhanced settings manager
    class SettingsManager {
    public:
        static std::vector<std::string> getSettingsList();
        static bool validateSettings();
        static void setSettingsValid(bool valid);
        
        // Enhanced settings features
        static bool loadSettings();
        static bool saveSettings();
        static void resetSettings();
        static std::string getSettingValue(const std::string& key);
        static void setSettingValue(const std::string& key, const std::string& value);
        
        // Overlay text management
        static void setOverlayEnabled(bool enabled);
        static void setOverlayText(const std::string& text);
        static void setOverlayTextSize(int size);
        static bool isOverlayEnabled();
        static std::string getOverlayText();
        static int getOverlayTextSize();
    };
    
    // Enhanced text content manager
    class TextContentManager {
    public:
        static std::string getTextBoxContent(int featNum);
        static void updateTextBoxContent(int featNum, const std::string& content);
        
        // Real-time content generation
        static std::string generateGameStats();
        static std::string generateDebugInfo();
        static std::string generateUserNotes();
        
        // Content formatting
        static std::string formatGameStats(const std::unordered_map<std::string, std::string>& stats);
        static std::string formatDebugInfo(const std::unordered_map<std::string, std::string>& info);
        static std::string addEmojisToText(const std::string& text);
        static std::string formatWithBorders(const std::string& title, const std::string& content);
    };
    
    // Enhanced initialization manager
    class InitializationManager {
    public:
        static bool initializeMenu(JNIEnv* env, jobject context, jobject title, jobject subtitle);
        static void setupTitle(JNIEnv* env, jobject title);
        static void setupSubtitle(JNIEnv* env, jobject subtitle);
        static void showWelcomeMessage(JNIEnv* env, jobject context);
        static void setInitValid(bool valid);
        static bool isInitialized();
        
        // Enhanced initialization features
        static bool performStartupChecks();
        static void displayStartupAnimation(JNIEnv* env, jobject context);
        static void checkForUpdates();
        static void validateMenuIntegrity();
    };
}

// JNI function declarations
extern "C" {
    // Core menu functions
    JNIEXPORT jstring JNICALL Icon(JNIEnv* env, jobject obj);
    JNIEXPORT jstring JNICALL IconWebViewData(JNIEnv* env, jobject obj);
    JNIEXPORT jboolean JNICALL isGameLibLoaded(JNIEnv* env, jobject obj);
    JNIEXPORT void JNICALL Init(JNIEnv* env, jobject obj, jobject context, jobject title, jobject subtitle);
    JNIEXPORT jobjectArray JNICALL SettingsList(JNIEnv* env, jobject obj);
    JNIEXPORT jobjectArray JNICALL GetFeatureList(JNIEnv* env, jobject obj);
    JNIEXPORT void JNICALL OnMenuSelected(JNIEnv* env, jobject obj, jint menuIndex);
    JNIEXPORT jstring JNICALL GetTextBoxContent(JNIEnv* env, jobject obj, jint featNum);
    JNIEXPORT jint JNICALL GetCurrentMenuIndex(JNIEnv* env, jobject obj);
    
    // Enhanced menu functions
    JNIEXPORT void JNICALL SetOverlayText(JNIEnv* env, jobject obj, jboolean enabled, jstring text, jint size);
    JNIEXPORT jstring JNICALL GetMenuName(JNIEnv* env, jobject obj, jint menuIndex);
    JNIEXPORT jint JNICALL GetMenuCount(JNIEnv* env, jobject obj);
    JNIEXPORT jboolean JNICALL IsFeatureEnabled(JNIEnv* env, jobject obj, jint featureId);
    JNIEXPORT void JNICALL UpdateFeatureState(JNIEnv* env, jobject obj, jint featureId, jboolean enabled, jint value);
}

// Legacy compatibility functions
bool setText(JNIEnv* env, jobject obj, const char* text);
void setDialog(jobject ctx, JNIEnv* env, const char* title, const char* msg);

// Global variables for backward compatibility
extern bool iconValid, settingsValid, initValid;
extern bool overlayEnabled;
extern std::string overlayCustomText;
extern int overlayTextSize;
extern int currentMenuIndex;
extern bool isLibLoaded;

// Enhanced function implementations
namespace EnhancedMenu {
    
    // State initialization
    namespace State {
        std::atomic<bool> iconValid{false};
        std::atomic<bool> settingsValid{false};
        std::atomic<bool> initValid{false};
        std::atomic<bool> overlayEnabled{false};
        std::atomic<int> currentMenuIndex{1};
        std::string overlayCustomText = "Mod Menu Active";
        std::atomic<int> overlayTextSize{16};
    }
    
    // MenuManager implementation
    class MenuManager {
    private:
        static std::unique_ptr<MenuManager> instance_;
        
    public:
        static MenuManager& getInstance() {
            if (!instance_) {
                instance_ = std::make_unique<MenuManager>();
            }
            return *instance_;
        }
        
        bool initializeMenus() {
            menus_.clear();
            
            // Initialize Aimbot menu
            auto aimbotMenu = std::make_unique<MenuInfo>();
            aimbotMenu->index = 1;
            aimbotMenu->name = "AIMBOT";
            aimbotMenu->displayName = "🎯 Aimbot Features";
            aimbotMenu->features = {
                {0, "Aimbot", "Toggle", "Enable/disable aimbot functionality"},
                {1, "Auto Aim", "Toggle", "Automatic target acquisition"},
                {2, "Aim Speed", "SeekBar", "Adjust aiming speed (1-10)"},
                {3, "FOV Range", "SeekBar", "Field of view range (10-180)"},
                {4, "Smooth Aim", "CheckBox", "Enable smooth aiming"},
                {5, "Silent Aim", "CheckBox", "Enable silent aim mode"},
                {6, "Target Priority", "Spinner", "Select target priority"},
                {7, "Predict Movement", "CheckBox", "Predict target movement"},
                {8, "Aim Delay", "SeekBar", "Delay between aims (0-500ms)"}
            };
            menus_.push_back(std::move(aimbotMenu));
            
            // Initialize Wallhack menu
            auto wallhackMenu = std::make_unique<MenuInfo>();
            wallhackMenu->index = 2;
            wallhackMenu->name = "WALLHACK";
            wallhackMenu->displayName = "👁️ Wallhack Features";
            wallhackMenu->features = {
                {10, "ESP Players", "Toggle", "Show player ESP"},
                {11, "Wallhack", "Toggle", "See through walls"},
                {12, "Item ESP", "Toggle", "Show item locations"},
                {13, "Show Distance", "CheckBox", "Display distance to targets"},
                {14, "Show Health", "CheckBox", "Display target health"},
                {15, "Show Names", "CheckBox", "Display player names"},
                {16, "ESP Distance", "SeekBar", "Maximum ESP distance (50-1000)"},
                {17, "ESP Style", "Spinner", "Choose ESP visualization style"},
                {18, "Show Weapons", "CheckBox", "Display equipped weapons"},
                {19, "Radar Hack", "Toggle", "Enable radar functionality"}
            };
            menus_.push_back(std::move(wallhackMenu));
            
            // Initialize Extras menu
            auto extrasMenu = std::make_unique<MenuInfo>();
            extrasMenu->index = 3;
            extrasMenu->name = "EXTRAS";
            extrasMenu->displayName = "⚡ Extra Features";
            extrasMenu->features = {
                {20, "Speed Hack", "Toggle", "Increase movement speed"},
                {21, "No Recoil", "Toggle", "Remove weapon recoil"},
                {22, "Unlimited Ammo", "Toggle", "Infinite ammunition"},
                {23, "Anti Ban", "CheckBox", "Enable anti-detection measures"},
                {24, "Hide Root", "CheckBox", "Hide root access from game"},
                {25, "Game Speed", "SeekBar", "Adjust game speed (1-5)"},
                {26, "Add Money", "Button", "Add in-game currency"},
                {27, "Custom Level", "InputValue", "Set custom player level (1-999)"},
                {28, "Fly Mode", "Toggle", "Enable flying ability"},
                {Config::TEXTBOX_GAME_STATS, "Game Stats", "TextBox", "Real-time game statistics"},
                {Config::TEXTBOX_DEBUG_INFO, "Debug Info", "TextBox", "System debug information"},
                {Config::TEXTBOX_USER_NOTES, "User Notes", "TextBox", "Your custom notes and settings"}
            };
            menus_.push_back(std::move(extrasMenu));
            
            LOGI(OBFUSCATE("✅ Enhanced menus initialized successfully"));
            return true;
        }
        
        std::vector<std::string> getMenuFeatures(int menuIndex) {
            std::vector<std::string> features;
            
            auto menu = getMenu(menuIndex);
            if (!menu) {
                LOGW(OBFUSCATE("Menu %d not found"), menuIndex);
                features.push_back(OBFUSCATE("Category_No Features Available"));
                return features;
            }
            
            // Add category header
            features.push_back("Category_" + menu->displayName);
            
            // Add features
            for (const auto& feature : menu->features) {
                std::string featureStr;
                
                if (feature.type == "Toggle") {
                    featureStr = "Toggle";
                    if (feature.enabled) featureStr += "_True";
                    featureStr += "_" + feature.name;
                } else if (feature.type == "SeekBar") {
                    featureStr = "SeekBar_" + feature.name + "_1_100"; // Default range
                } else if (feature.type == "CheckBox") {
                    featureStr = "CheckBox";
                    if (feature.enabled) featureStr += "_True";
                    featureStr += "_" + feature.name;
                } else if (feature.type == "Button") {
                    featureStr = "Button_" + feature.name;
                } else if (feature.type == "Spinner") {
                    featureStr = "Spinner_" + feature.name + "_Option1,Option2,Option3";
                } else if (feature.type == "InputValue") {
                    featureStr = "InputValue_" + feature.name + "_1_999";
                } else if (feature.type == "TextBox") {
                    featureStr = std::to_string(feature.id) + "_TextBox_" + feature.name + "_" + feature.description;
                } else {
                    featureStr = feature.type + "_" + feature.name;
                }
                
                features.push_back(featureStr);
            }
            
            return features;
        }
        
        MenuInfo* getMenu(int index) {
            for (const auto& menu : menus_) {
                if (menu->index == index) {
                    return menu.get();
                }
            }
            return nullptr;
        }
    };
    
    // Initialize static member
    std::unique_ptr<MenuManager> MenuManager::instance_ = nullptr;
}

#endif // ENHANCED_MENU_H
