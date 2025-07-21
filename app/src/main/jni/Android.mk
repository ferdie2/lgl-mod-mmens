LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

# Here is the name of your lib.
# When you change the lib name, change also on System.loadLibrary("") under OnCreate method on StaticActivity.java
# Both must have same name
LOCAL_MODULE := MyLibName

# Enhanced compilation flags for better performance
LOCAL_CFLAGS := -O3 -ffast-math -fvisibility=hidden -fpermissive -fexceptions -DNDEBUG
LOCAL_CFLAGS += -Wno-error=format-security -ffunction-sections -fdata-sections
LOCAL_CFLAGS += -march=armv7-a -mfloat-abi=softfp -mfpu=neon

LOCAL_CPPFLAGS := -O3 -ffast-math -fvisibility=hidden -std=c++17 -fexceptions
LOCAL_CPPFLAGS += -Wno-error=format-security -Wno-error=c++11-narrowing -fpermissive
LOCAL_CPPFLAGS += -ffunction-sections -fdata-sections -Wall
LOCAL_CPPFLAGS += -march=armv7-a -mfloat-abi=softfp -mfpu=neon

# Enhanced linker flags for better performance and smaller binary size
LOCAL_LDFLAGS += -Wl,--gc-sections,--strip-all,-llog
LOCAL_LDFLAGS += -Wl,--as-needed,--no-undefined
LOCAL_LDFLAGS += -ffast-math -O3

LOCAL_LDLIBS := -llog -landroid -lEGL -lGLESv2 -ljnigraphics
LOCAL_ARM_MODE := arm

# Enable Link Time Optimization for release builds
ifeq ($(APP_OPTIM),release)
    LOCAL_CFLAGS += -flto
    LOCAL_CPPFLAGS += -flto
    LOCAL_LDFLAGS += -flto
endif

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/Includes

# Here you add the cpp file to compile
LOCAL_SRC_FILES := Main.cpp \
	Substrate/hde64.c \
	Substrate/SubstrateDebug.cpp \
	Substrate/SubstrateHook.cpp \
	Substrate/SubstratePosixMemory.cpp \
	Substrate/SymbolFinder.cpp \
	KittyMemory/KittyMemory.cpp \
	KittyMemory/MemoryPatch.cpp \
    KittyMemory/MemoryBackup.cpp \
    KittyMemory/KittyUtils.cpp \
	And64InlineHook/And64InlineHook.cpp

include $(BUILD_SHARED_LIBRARY)
