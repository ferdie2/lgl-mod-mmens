# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Enhanced ProGuard rules for performance optimization

# Keep native method signatures
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep main application components
-keep class com.android.support.MainActivity {
    public *;
}

-keep class com.android.support.Main {
    public static void Start(android.content.Context);
    public static void StartWithoutPermission(android.content.Context);
}

-keep class com.android.support.Menu {
    public <init>(android.content.Context);
    public void SetWindowManagerActivity();
    public void SetWindowManagerWindowService();
    public void SetWindowManagerWithoutPermission();
    public void ShowMenu();
    public void onDestroy();
    public void setVisibility(int);
    native <methods>;
}

-keep class com.android.support.Launcher {
    public <init>();
}

-keep class com.android.support.Preferences {
    public static void changeFeatureInt(java.lang.String, int, int);
    public static void changeFeatureString(java.lang.String, int, java.lang.String);
    public static void changeFeatureBool(java.lang.String, int, boolean);
    public static int loadPrefInt(java.lang.String, int);
    public static boolean loadPrefBool(java.lang.String, int, boolean);
    public static java.lang.String loadPrefString(java.lang.String, int);
    native <methods>;
}

-keep class com.android.support.CrashHandler {
    public static void init(android.content.Context, boolean);
}

# Keep JNI-related classes and methods
-keepclasseswithmembers class * {
    native <methods>;
}

# Optimization settings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Keep exception attributes for better crash reports
-keepattributes Exceptions

# Keep annotations
-keepattributes *Annotation*

# Keep generic signatures
-keepattributes Signature

# Keep source file names and line numbers for crash reports
-keepattributes SourceFile,LineNumberTable

# Don't warn about missing classes
-dontwarn java.lang.invoke.*
-dontwarn **$$serializer
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Performance optimizations
-repackageclasses ''
-mergeinterfacesaggressively

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
