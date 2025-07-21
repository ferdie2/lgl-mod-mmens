#include <list>
#include <vector>
#include <string.h>
#include <pthread.h>
#include <thread>
#include <cstring>
#include <jni.h>
#include <unistd.h>
#include <fstream>
#include <iostream>
#include <dlfcn.h>
#include "Includes/Logger.h"
#include "Includes/obfuscate.h"
#include "Includes/Utils.h"
#include "KittyMemory/MemoryPatch.h"
#include "Menu/Setup.h"

//Target lib here
#define targetLibName OBFUSCATE("libFileA.so")

#include "Includes/Macros.h"

bool feature1, feature2, featureHookToggle, Health;
int sliderValue = 1, level = 0;
void *instanceBtn;
int currentMenu = 1;

// String storage for TextBox features
std::string playerInfo = "Player: Level 45 | HP: 100/100 | XP: 2350";
std::string debugLog = "System initialized successfully\nMemory allocated: 512MB\nConnected to server";
std::string userNotes = "Add your custom notes here";

// Hooking examples. Assuming you know how to write hook
void (*AddMoneyExample)(void *instance, int amount);

bool (*old_get_BoolExample)(void *instance);
bool get_BoolExample(void *instance) {
    if (instance != NULL && featureHookToggle) {
        return true;
    }
    return old_get_BoolExample(instance);
}

float (*old_get_FloatExample)(void *instance);
float get_FloatExample(void *instance) {
    if (instance != NULL && sliderValue > 1) {
        return (float) sliderValue;
    }
    return old_get_FloatExample(instance);
}

int (*old_Level)(void *instance);
int Level(void *instance) {
    if (instance != NULL && level) {
        return (int) level;
    }
    return old_Level(instance);
}

void (*old_FunctionExample)(void *instance);
void FunctionExample(void *instance) {
    instanceBtn = instance;
    if (instance != NULL) {
        if (Health) {
            *(int *) ((uint64_t) instance + 0x48) = 999;
        }
    }
    return old_FunctionExample(instance);
}

// we will run our hacks in a new thread so our while loop doesn't block process main thread
void *hack_thread(void *) {
    LOGI(OBFUSCATE("pthread created"));

    //Check if target lib is loaded
    do {
        sleep(1);
    } while (!isLibraryLoaded(targetLibName));

    //Anti-lib rename
    /*
    do {
        sleep(1);
    } while (!isLibraryLoaded("libYOURNAME.so"));*/

    LOGI(OBFUSCATE("%s has been loaded"), (const char *) targetLibName);

#if defined(__aarch64__) //To compile this code for arm64 lib only. Do not worry about greyed out highlighting code, it still works
    // Hook example. Comment out if you don't use hook
    // Strings in macros are automatically obfuscated. No need to obfuscate!
    HOOK("str", FunctionExample, old_FunctionExample);
    HOOK_LIB("libFileB.so", "0x123456", FunctionExample, old_FunctionExample);
    HOOK_NO_ORIG("0x123456", FunctionExample);
    HOOK_LIB_NO_ORIG("libFileC.so", "0x123456", FunctionExample);
    HOOKSYM("__SymbolNameExample", FunctionExample, old_FunctionExample);
    HOOKSYM_LIB("libFileB.so", "__SymbolNameExample", FunctionExample, old_FunctionExample);
    HOOKSYM_NO_ORIG("__SymbolNameExample", FunctionExample);
    HOOKSYM_LIB_NO_ORIG("libFileB.so", "__SymbolNameExample", FunctionExample);

    // Patching offsets directly. Strings are automatically obfuscated too!
    PATCH("0x20D3A8", "00 00 A0 E3 1E FF 2F E1");
    PATCH_LIB("libFileB.so", "0x20D3A8", "00 00 A0 E3 1E FF 2F E1");

    AddMoneyExample = (void(*)(void *,int))getAbsoluteAddress(targetLibName, 0x123456);

#else //To compile this code for armv7 lib only.

    // Hook example. Comment out if you don't use hook
    // Strings in macros are automatically obfuscated. No need to obfuscate!
    HOOK("str", FunctionExample, old_FunctionExample);
    HOOK_LIB("libFileB.so", "0x123456", FunctionExample, old_FunctionExample);
    HOOK_NO_ORIG("0x123456", FunctionExample);
    HOOK_LIB_NO_ORIG("libFileC.so", "0x123456", FunctionExample);
    HOOKSYM("__SymbolNameExample", FunctionExample, old_FunctionExample);
    HOOKSYM_LIB("libFileB.so", "__SymbolNameExample", FunctionExample, old_FunctionExample);
    HOOKSYM_NO_ORIG("__SymbolNameExample", FunctionExample);
    HOOKSYM_LIB_NO_ORIG("libFileB.so", "__SymbolNameExample", FunctionExample);

    // Patching offsets directly. Strings are automatically obfuscated too!
    PATCH("0x20D3A8", "00 00 A0 E3 1E FF 2F E1");
    PATCH_LIB("libFileB.so", "0x20D3A8", "00 00 A0 E3 1E FF 2F E1");

    //Restore changes to original
    RESTORE("0x20D3A8");
    RESTORE_LIB("libFileB.so", "0x20D3A8");

    AddMoneyExample = (void (*)(void *, int)) getAbsoluteAddress(targetLibName, 0x123456);

    LOGI(OBFUSCATE("Done"));
#endif

    //Anti-leech
    /*if (!iconValid || !initValid || !settingsValid) {
        //Bad function to make it crash
        sleep(5);
        int *p = 0;
        *p = 0;
    }*/

    return NULL;
}

// Do not change or translate the first text unless you know what you are doing
// Assigning feature numbers is optional. Without it, it will automatically count for you, starting from 0
// Assigned feature numbers can be like any numbers 1,3,200,10... instead in order 0,1,2,3,4,5...
// ButtonLink, Category, RichTextView and RichWebView is not counted. They can't have feature number assigned
// Toggle, ButtonOnOff and Checkbox can be switched on by default, if you add True_. Example: CheckBox_True_The Check Box
// To learn HTML, go to this page: https://www.w3schools.com/

const char* getMenuFeatures(int menuIndex, int &totalFeatures) {
    // Menu 1: AIMBOT
    static const char *menu1[] = {
            OBFUSCATE("Category_AIMBOT FEATURES"),
            OBFUSCATE("Toggle_True_Aimbot"),
            OBFUSCATE("Toggle_Auto Aim"),
            OBFUSCATE("SeekBar_Aim Speed_1_10"),
            OBFUSCATE("SeekBar_FOV Range_10_180"),
            OBFUSCATE("CheckBox_Smooth Aim"),
            OBFUSCATE("CheckBox_Silent Aim"),
            OBFUSCATE("Spinner_Target Priority_Head,Body,Closest,Weakest")
    };
    
    // Menu 2: WALLHACK  
    static const char *menu2[] = {
            OBFUSCATE("Category_WALLHACK FEATURES"),
            OBFUSCATE("Toggle_True_ESP Players"),
            OBFUSCATE("Toggle_Wallhack"),
            OBFUSCATE("Toggle_Item ESP"),
            OBFUSCATE("CheckBox_Show Distance"),
            OBFUSCATE("CheckBox_Show Health"),
            OBFUSCATE("CheckBox_Show Names"),
            OBFUSCATE("SeekBar_ESP Distance_50_1000"),
            OBFUSCATE("Spinner_ESP Style_Box,Line,Dot")
    };
    
    // Menu 3: EXTRAS
    static const char *menu3[] = {
            OBFUSCATE("Category_EXTRA FEATURES"),
            OBFUSCATE("Toggle_Speed Hack"),
            OBFUSCATE("Toggle_No Recoil"),
            OBFUSCATE("Toggle_Unlimited Ammo"),
            OBFUSCATE("CheckBox_Anti Ban"),
            OBFUSCATE("CheckBox_Hide Root"),
            OBFUSCATE("SeekBar_Game Speed_1_5"),
            OBFUSCATE("Button_Add Money"),
            OBFUSCATE("InputValue_Custom Level_1_999"),
            OBFUSCATE("100_TextBox_Game Info_Player Stats and Information"),
            OBFUSCATE("101_TextBox_Debug Log_System debug messages"),
            OBFUSCATE("102_TextBox_User Notes_Custom user notes")
    };

    switch(menuIndex) {
        case 1:
            totalFeatures = sizeof(menu1) / sizeof(menu1[0]);
            return (const char*)menu1;
        case 2:
            totalFeatures = sizeof(menu2) / sizeof(menu2[0]);
            return (const char*)menu2;
        case 3:
            totalFeatures = sizeof(menu3) / sizeof(menu3[0]);
            return (const char*)menu3;
        default:
            totalFeatures = sizeof(menu1) / sizeof(menu1[0]);
            return (const char*)menu1;
    }
}

jobjectArray GetFeatureList(JNIEnv *env, jobject context) {
    if (env == NULL) {
        LOGE(OBFUSCATE("JNIEnv is null in GetFeatureList"));
        return NULL;
    }
    
    jobjectArray ret;
    int totalFeatures;
    
    const char **features = (const char**)getMenuFeatures(currentMenu, totalFeatures);
    
    if (features == NULL || totalFeatures <= 0) {
        LOGW(OBFUSCATE("No features found for menu %d"), currentMenu);
        totalFeatures = 1;
        static const char* defaultFeature = OBFUSCATE("Category_No Features Available");
        features = &defaultFeature;
    }

    jclass stringClass = env->FindClass(OBFUSCATE("java/lang/String"));
    if (stringClass == NULL) {
        LOGE(OBFUSCATE("Failed to find String class"));
        return NULL;
    }
    
    ret = env->NewObjectArray(totalFeatures, stringClass, env->NewStringUTF(""));
    if (ret == NULL) {
        LOGE(OBFUSCATE("Failed to create object array"));
        return NULL;
    }

    for (int i = 0; i < totalFeatures; i++) {
        jstring feature = env->NewStringUTF(features[i]);
        if (feature != NULL) {
            env->SetObjectArrayElement(ret, i, feature);
            env->DeleteLocalRef(feature); // Clean up local reference
        }
    }
    
    env->DeleteLocalRef(stringClass); // Clean up local reference

    return (ret);
}

void OnMenuSelected(JNIEnv *env, jobject obj, jint menuIndex) {
    LOGD(OBFUSCATE("Menu selected: %d"), menuIndex);
    
    // Add bounds checking and thread safety
    if (menuIndex >= 1 && menuIndex <= 3) {
        // Only update if actually changing menu to prevent unnecessary processing
        if (currentMenu != menuIndex) {
            currentMenu = menuIndex;
            LOGD(OBFUSCATE("Menu changed to: %d"), currentMenu);
        }
    } else {
        LOGW(OBFUSCATE("Invalid menu index: %d, defaulting to 1"), menuIndex);
        currentMenu = 1; // Default to first menu if invalid index
    }
}

jstring GetTextBoxContent(JNIEnv *env, jobject obj, jint featNum) {
    std::string content;
    
    switch (featNum) {
        case 100:  // Game Info TextBox
            // Update with real-time game data
            playerInfo = "Player: Level " + std::to_string(level > 0 ? level : 45) + 
                        " | HP: 100/100 | XP: " + std::to_string(2350 + (sliderValue * 100));
            content = playerInfo;
            break;
        case 101:  // Debug Log TextBox  
            content = debugLog;
            break;
        case 102:  // User Notes TextBox
            content = userNotes;
            break;
        default:
            content = "No content available";
            break;
    }
    
    return env->NewStringUTF(content.c_str());
}

void Changes(JNIEnv *env, jclass clazz, jobject obj,
                                        jint featNum, jstring featName, jint value,
                                        jboolean boolean, jstring str) {

    LOGD(OBFUSCATE("Feature name: %d - %s | Value: = %d | Bool: = %d | Text: = %s"), featNum,
         env->GetStringUTFChars(featName, 0), value,
         boolean, str != NULL ? env->GetStringUTFChars(str, 0) : "");

    //BE CAREFUL NOT TO ACCIDENTLY REMOVE break;

    switch (featNum) {
        case 0:
            // A much simpler way to patch hex via KittyMemory without need to specify the struct and len. Spaces or without spaces are fine
            // ARMv7 assembly example
            // MOV R0, #0x0 = 00 00 A0 E3
            // BX LR = 1E FF 2F E1
            PATCH_LIB_SWITCH("libil2cpp.so", "0x100000", "00 00 A0 E3 1E FF 2F E1", boolean);
            break;
        case 13600:
            //Reminder that the strings are auto obfuscated
            //Switchable patch
            PATCH_SWITCH("0x400000", "00 00 A0 E3 1E FF 2F E1", boolean);
            PATCH_LIB_SWITCH("libil2cpp.so", "0x200000", "00 00 A0 E3 1E FF 2F E1", boolean);
            PATCH_SYM_SWITCH("_SymbolExample", "00 00 A0 E3 1E FF 2F E1", boolean);
            PATCH_LIB_SYM_SWITCH("libNativeGame.so", "_SymbolExample", "00 00 A0 E3 1E FF 2F E1", boolean);

            //Restore patched offset to original
            RESTORE("0x400000");
            RESTORE_LIB("libil2cpp.so", "0x400000");
            RESTORE_SYM("_SymbolExample");
            RESTORE_LIB_SYM("libil2cpp.so", "_SymbolExample");
            break;
        case 110:
            break;
        case 1:
            if (value >= 1) {
                sliderValue = value;
            }
            break;
        case 2:
            switch (value) {
                //For noobies
                case 0:
                    RESTORE("0x0");
                    break;
                case 1:
                    PATCH("0x0", "01 00 A0 E3 1E FF 2F E1");
                    break;
                case 2:
                    PATCH("0x0", "02 00 A0 E3 1E FF 2F E1");
                    break;
            }
            break;
        case 3:
            switch (value) {
                case 0:
                    LOGD(OBFUSCATE("Selected item 1"));
                    break;
                case 1:
                    LOGD(OBFUSCATE("Selected item 2"));
                    break;
                case 2:
                    LOGD(OBFUSCATE("Selected item 3"));
                    break;
            }
            break;
        case 4:
            // Since we have instanceBtn as a field, we can call it out of Update hook function
            if (instanceBtn != NULL)
                AddMoneyExample(instanceBtn, 999999);
            // MakeToast(env, obj, OBFUSCATE("Button pressed"), Toast::LENGTH_SHORT);
            break;
        case 5:
            break;
        case 6:
            featureHookToggle = boolean;
            break;
        case 7:
            level = value;
            break;
        case 8:
            break;
        case 9:
            break;
        case 100000: // Game Info TextBox - read-only
            // This is handled by GetTextBoxContent function
            break;
        case 10001: // Debug Log TextBox - read-only
            // This is handled by GetTextBoxContent function  
            break;
        case 102: // User Notes TextBox - read-only
            // This is handled by GetTextBoxContent function
            break;
        // Menu-specific features (feature numbers will vary by menu)
        case 10: // Example for menu-specific features
            switch (currentMenu) {
                case 1: // Player Hacks
                    if (boolean) {
                        // God Mode implementation
                        PATCH_SWITCH("0x123456", "01 00 A0 E3 1E FF 2F E1", boolean);
                    }
                    break;
                case 2: // Weapon Mods
                    // Unlimited Ammo implementation
                    featureHookToggle = boolean;
                    break;
                case 3: // Visual Hacks
                    // ESP implementation
                    break;
                // Add more cases for other menus
            }
            break;
    }
}

__attribute__((constructor))
void lib_main() {
    // Create a new thread so it does not block the main thread, means the game would not freeze
    pthread_t ptid;
    pthread_create(&ptid, NULL, hack_thread, NULL);
}

int RegisterMenu(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {OBFUSCATE("Icon"), OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(Icon)},
            {OBFUSCATE("IconWebViewData"),  OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(IconWebViewData)},
            {OBFUSCATE("IsGameLibLoaded"),  OBFUSCATE("()Z"), reinterpret_cast<void *>(isGameLibLoaded)},
            {OBFUSCATE("Init"),  OBFUSCATE("(Landroid/content/Context;Landroid/widget/TextView;Landroid/widget/TextView;)V"), reinterpret_cast<void *>(Init)},
            {OBFUSCATE("SettingsList"),  OBFUSCATE("()[Ljava/lang/String;"), reinterpret_cast<void *>(SettingsList)},
            {OBFUSCATE("GetFeatureList"),  OBFUSCATE("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetFeatureList)},
            {OBFUSCATE("onMenuSelected"), OBFUSCATE("(I)V"), reinterpret_cast<void *>(OnMenuSelected)},
            {OBFUSCATE("getTextBoxContent"), OBFUSCATE("(I)Ljava/lang/String;"), reinterpret_cast<void *>(GetTextBoxContent)},
            {OBFUSCATE("getCurrentMenuIndex"), OBFUSCATE("()I"), reinterpret_cast<void *>(GetCurrentMenuIndex)},
    };

    jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Menu"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;
    return JNI_OK;
}

int RegisterPreferences(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {OBFUSCATE("Changes"), OBFUSCATE("(Landroid/content/Context;ILjava/lang/String;IZLjava/lang/String;)V"), reinterpret_cast<void *>(Changes)},
    };
    jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Preferences"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;
    return JNI_OK;
}

int RegisterMain(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {OBFUSCATE("CheckOverlayPermission"), OBFUSCATE("(Landroid/content/Context;)V"), reinterpret_cast<void *>(CheckOverlayPermission)},
    };
    jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Main"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;

    return JNI_OK;
}

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    if (RegisterMenu(env) != 0)
        return JNI_ERR;
    if (RegisterPreferences(env) != 0)
        return JNI_ERR;
    if (RegisterMain(env) != 0)
        return JNI_ERR;
    return JNI_VERSION_1_6;
}
