# Enhanced native build configuration for better performance
APP_ABI := armeabi-v7a arm64-v8a x86 x86_64
APP_PLATFORM := android-21  # Match with minSdkVersion
APP_STL := c++_static
APP_OPTIM := release

# Enhanced performance flags
APP_CPPFLAGS += -std=c++17 -ffast-math -O3 -DNDEBUG
APP_CFLAGS += -O3 -ffast-math -DNDEBUG

# Memory and size optimizations
APP_THIN_ARCHIVE := true
APP_PIE := true
APP_STRIP_MODE := --strip-unneeded

# Link Time Optimization
APP_LDFLAGS += -flto

# Threading support
APP_LDFLAGS += -pthread
