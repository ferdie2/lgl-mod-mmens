package com.android.support;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Preferences {
    
    private static final String TAG = "Preferences";
    private static final String PREFERENCES_NAME = "_preferences";
    
    private static SharedPreferences sharedPreferences;
    private static Preferences prefsInstance;
    public static Context context;
    public static volatile boolean loadPref = false;
    public static volatile boolean isExpanded = false;

    // Thread safety improvements
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    // Cache for frequently accessed preferences
    private static final ConcurrentHashMap<Integer, Object> cache = new ConcurrentHashMap<>();
    
    // Constants for default values
    private static final String LENGTH = "_length";
    private static final String DEFAULT_STRING_VALUE = "";
    private static final int DEFAULT_INT_VALUE = 0;
    private static final double DEFAULT_DOUBLE_VALUE = 0d;
    private static final float DEFAULT_FLOAT_VALUE = 0f;
    private static final long DEFAULT_LONG_VALUE = 0L;
    private static final boolean DEFAULT_BOOLEAN_VALUE = false;

    // Native method declaration
    public static native void Changes(Context con, int fNum, String fName, int i, boolean bool, String str);

    /**
     * Thread-safe preference change for integers
     */
    public static void changeFeatureInt(String featureName, int featureNum, int value) {
        try {
            lock.writeLock().lock();
            
            Preferences.with(context).writeInt(featureNum, value);
            cache.put(featureNum, value); // Update cache
            
            // Call native method in background to prevent UI blocking
            try {
                Changes(context, featureNum, featureName, value, false, null);
            } catch (UnsatisfiedLinkError e) {
                Log.w(TAG, "Native method not available for feature: " + featureName);
            } catch (Exception e) {
                Log.e(TAG, "Error calling native Changes method", e);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error changing feature int: " + featureName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Thread-safe preference change for strings
     */
    public static void changeFeatureString(String featureName, int featureNum, String str) {
        try {
            lock.writeLock().lock();
            
            Preferences.with(context).writeString(featureNum, str);
            cache.put(featureNum, str); // Update cache
            
            try {
                Changes(context, featureNum, featureName, 0, false, str);
            } catch (UnsatisfiedLinkError e) {
                Log.w(TAG, "Native method not available for feature: " + featureName);
            } catch (Exception e) {
                Log.e(TAG, "Error calling native Changes method", e);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error changing feature string: " + featureName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Thread-safe preference change for booleans
     */
    public static void changeFeatureBool(String featureName, int featureNum, boolean bool) {
        try {
            lock.writeLock().lock();
            
            Preferences.with(context).writeBoolean(featureNum, bool);
            cache.put(featureNum, bool); // Update cache
            
            try {
                Changes(context, featureNum, featureName, 0, bool, null);
            } catch (UnsatisfiedLinkError e) {
                Log.w(TAG, "Native method not available for feature: " + featureName);
            } catch (Exception e) {
                Log.e(TAG, "Error calling native Changes method", e);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error changing feature bool: " + featureName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Optimized integer preference loading with caching
     */
    public static int loadPrefInt(String featureName, int featureNum) {
        try {
            lock.readLock().lock();
            
            // Check cache first
            Object cached = cache.get(featureNum);
            if (cached instanceof Integer) {
                return (Integer) cached;
            }
            
            if (loadPref) {
                int value = Preferences.with(context).readInt(featureNum);
                cache.put(featureNum, value); // Cache the result
                
                try {
                    Changes(context, featureNum, featureName, value, false, null);
                } catch (Exception e) {
                    Log.w(TAG, "Error calling native method for " + featureName, e);
                }
                
                return value;
            }
            return DEFAULT_INT_VALUE;
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading preference int: " + featureName, e);
            return DEFAULT_INT_VALUE;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Optimized boolean preference loading with caching and special handling
     */
    public static boolean loadPrefBool(String featureName, int featureNum, boolean defaultValue) {
        try {
            lock.readLock().lock();
            
            // Check cache first for non-special feature numbers
            if (featureNum >= 0) {
                Object cached = cache.get(featureNum);
                if (cached instanceof Boolean) {
                    return (Boolean) cached;
                }
            }
            
            boolean result = Preferences.with(context).readBoolean(featureNum, defaultValue);
            
            // Handle special feature numbers
            if (featureNum == -1) {
                loadPref = result;
            } else if (featureNum == -3) {
                isExpanded = result;
            }
            
            // Use loaded preference if loadPref is enabled or for special features
            if (loadPref || featureNum < 0) {
                defaultValue = result;
                // Cache non-special features
                if (featureNum >= 0) {
                    cache.put(featureNum, result);
                }
            }

            try {
                Changes(context, featureNum, featureName, 0, defaultValue, null);
            } catch (Exception e) {
                Log.w(TAG, "Error calling native method for " + featureName, e);
            }
            
            return defaultValue;
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading preference bool: " + featureName, e);
            return defaultValue;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Optimized string preference loading with caching
     */
    public static String loadPrefString(String featureName, int featureNum) {
        try {
            lock.readLock().lock();
            
            // Check cache first
            Object cached = cache.get(featureNum);
            if (cached instanceof String) {
                return (String) cached;
            }
            
            if (loadPref || featureNum <= 0) {
                String result = Preferences.with(context).readString(featureNum);
                cache.put(featureNum, result); // Cache the result
                
                try {
                    Changes(context, featureNum, featureName, 0, false, result);
                } catch (Exception e) {
                    Log.w(TAG, "Error calling native method for " + featureName, e);
                }
                
                return result;
            }
            return DEFAULT_STRING_VALUE;
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading preference string: " + featureName, e);
            return DEFAULT_STRING_VALUE;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Thread-safe singleton constructor
     */
    private Preferences(Context context) {
        try {
            sharedPreferences = context.getApplicationContext().getSharedPreferences(
                    context.getPackageName() + PREFERENCES_NAME,
                    Context.MODE_PRIVATE
            );
        } catch (Exception e) {
            Log.e(TAG, "Error creating SharedPreferences", e);
        }
    }

    /**
     * Constructor with custom preferences name
     */
    private Preferences(Context context, String preferencesName) {
        try {
            sharedPreferences = context.getApplicationContext().getSharedPreferences(
                    preferencesName,
                    Context.MODE_PRIVATE
            );
        } catch (Exception e) {
            Log.e(TAG, "Error creating SharedPreferences with name: " + preferencesName, e);
        }
    }

    /**
     * Thread-safe singleton instance getter
     * @param context Application context
     * @return Preferences instance
     */
    public static synchronized Preferences with(Context context) {
        if (prefsInstance == null && context != null) {
            prefsInstance = new Preferences(context);
        }
        return prefsInstance;
    }

    /**
     * Clear cache for memory optimization
     */
    public static void clearCache() {
        try {
            lock.writeLock().lock();
            cache.clear();
            Log.d(TAG, "Preferences cache cleared");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get cache size for debugging
     */
    public static int getCacheSize() {
        return cache.size();
    }

    // String related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public String readString(String what) {
        return sharedPreferences.getString(what, DEFAULT_STRING_VALUE);
    }

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public String readString(int what) {
        try {
            return sharedPreferences.getString(String.valueOf(what), DEFAULT_STRING_VALUE);
        } catch (java.lang.ClassCastException ex) {
            return "";
        }
    }

    /**
     * @param what
     * @param defaultString
     * @return Returns the stored value of 'what'
     */
    public String readString(String what, String defaultString) {
        return sharedPreferences.getString(what, defaultString);
    }

    /**
     * @param where
     * @param what
     */
    public void writeString(String where, String what) {
        sharedPreferences.edit().putString(where, what).apply();
    }

    /**
     * @param where
     * @param what
     */
    public void writeString(int where, String what) {
        sharedPreferences.edit().putString(String.valueOf(where), what).apply();
    }

    // int related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public int readInt(String what) {
        return sharedPreferences.getInt(what, DEFAULT_INT_VALUE);
    }


    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public int readInt(int what) {
        try {
            return sharedPreferences.getInt(String.valueOf(what), DEFAULT_INT_VALUE);
        } catch (java.lang.ClassCastException ex) {
            return 0;
        }
    }

    /**
     * @param what
     * @param defaultInt
     * @return Returns the stored value of 'what'
     */
    public int readInt(String what, int defaultInt) {
        return sharedPreferences.getInt(what, defaultInt);
    }

    /**
     * @param where
     * @param what
     */
    public void writeInt(String where, int what) {
        sharedPreferences.edit().putInt(where, what).apply();
    }

    /**
     * @param where
     * @param what
     */
    public void writeInt(int where, int what) {
        sharedPreferences.edit().putInt(String.valueOf(where), what).apply();
    }

    // double related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public double readDouble(String what) {
        if (!contains(what))
            return DEFAULT_DOUBLE_VALUE;
        return Double.longBitsToDouble(readLong(what));
    }

    /**
     * @param what
     * @param defaultDouble
     * @return Returns the stored value of 'what'
     */
    public double readDouble(String what, double defaultDouble) {
        if (!contains(what))
            return defaultDouble;
        return Double.longBitsToDouble(readLong(what));
    }

    /**
     * @param where
     * @param what
     */
    public void writeDouble(String where, double what) {
        writeLong(where, Double.doubleToRawLongBits(what));
    }

    // float related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public float readFloat(String what) {
        return sharedPreferences.getFloat(what, DEFAULT_FLOAT_VALUE);
    }

    /**
     * @param what
     * @param defaultFloat
     * @return Returns the stored value of 'what'
     */
    public float readFloat(String what, float defaultFloat) {
        return sharedPreferences.getFloat(what, defaultFloat);
    }

    /**
     * @param where
     * @param what
     */
    public void writeFloat(String where, float what) {
        sharedPreferences.edit().putFloat(where, what).apply();
    }

    // long related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public long readLong(String what) {
        return sharedPreferences.getLong(what, DEFAULT_LONG_VALUE);
    }

    /**
     * @param what
     * @param defaultLong
     * @return Returns the stored value of 'what'
     */
    public long readLong(String what, long defaultLong) {
        return sharedPreferences.getLong(what, defaultLong);
    }

    /**
     * @param where
     * @param what
     */
    public void writeLong(String where, long what) {
        sharedPreferences.edit().putLong(where, what).apply();
    }

    // boolean related methods

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public boolean readBoolean(String what) {
        return sharedPreferences.getBoolean(what, DEFAULT_BOOLEAN_VALUE);
    }

    /**
     * @param what
     * @return Returns the stored value of 'what'
     */
    public boolean readBoolean(int what) {
        return sharedPreferences.getBoolean(String.valueOf(what), DEFAULT_BOOLEAN_VALUE);
    }

    /**
     * @param what
     * @param defaultBoolean
     * @return Returns the stored value of 'what'
     */
    public boolean readBoolean(String what, boolean defaultBoolean) {
        /*if (defaultBoolean == true && !sharedPreferences.contains(what))
            writeBoolean(what, true);*/
        return sharedPreferences.getBoolean(what, defaultBoolean);
    }

    /**
     * @param what
     * @param defaultBoolean
     * @return Returns the stored value of 'what'
     */
    public boolean readBoolean(int what, boolean defaultBoolean) {
        /*if (defaultBoolean == true && !sharedPreferences.contains(String.valueOf(what)))
            writeBoolean(what, true);*/
        try {
            return sharedPreferences.getBoolean(String.valueOf(what), defaultBoolean);
        } catch (java.lang.ClassCastException ex) {
            return defaultBoolean;
        }
    }

    /**
     * @param where
     * @param what
     */
    public void writeBoolean(String where, boolean what) {
        sharedPreferences.edit().putBoolean(where, what).apply();
    }

    /**
     * @param where
     * @param what
     */
    public void writeBoolean(int where, boolean what) {
        sharedPreferences.edit().putBoolean(String.valueOf(where), what).apply();
    }

    // String set methods

    /**
     * @param key
     * @param value
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void putStringSet(final String key, final Set<String> value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            sharedPreferences.edit().putStringSet(key, value).apply();
        } else {
            // Workaround for pre-HC's lack of StringSets
            putOrderedStringSet(key, value);
        }
    }

    /**
     * @param key
     * @param value
     */
    public void putOrderedStringSet(String key, Set<String> value) {
        int stringSetLength = 0;
        if (sharedPreferences.contains(key + LENGTH)) {
            // First read what the value was
            stringSetLength = readInt(key + LENGTH);
        }
        writeInt(key + LENGTH, value.size());
        int i = 0;
        for (String aValue : value) {
            writeString(key + "[" + i + "]", aValue);
            i++;
        }
        for (; i < stringSetLength; i++) {
            // Remove any remaining values
            remove(key + "[" + i + "]");
        }
    }

    /**
     * @param key
     * @param defValue
     * @return Returns the String Set with HoneyComb compatibility
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getStringSet(final String key, final Set<String> defValue) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return sharedPreferences.getStringSet(key, defValue);
        } else {
            // Workaround for pre-HC's missing getStringSet
            return getOrderedStringSet(key, defValue);
        }
    }

    /**
     * @param key
     * @param defValue
     * @return Returns the ordered String Set
     */
    public Set<String> getOrderedStringSet(String key, final Set<String> defValue) {
        if (contains(key + LENGTH)) {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            int stringSetLength = readInt(key + LENGTH);
            if (stringSetLength >= 0) {
                for (int i = 0; i < stringSetLength; i++) {
                    set.add(readString(key + "[" + i + "]"));
                }
            }
            return set;
        }
        return defValue;
    }

    // end related methods

    /**
     * @param key
     */
    public void remove(final String key) {
        if (contains(key + LENGTH)) {
            // Workaround for pre-HC's lack of StringSets
            int stringSetLength = readInt(key + LENGTH);
            if (stringSetLength >= 0) {
                sharedPreferences.edit().remove(key + LENGTH).apply();
                for (int i = 0; i < stringSetLength; i++) {
                    sharedPreferences.edit().remove(key + "[" + i + "]").apply();
                }
            }
        }
        sharedPreferences.edit().remove(key).apply();
    }

    /**
     * @param key
     * @return Returns if that key exists
     */
    public boolean contains(final String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * Clear all the preferences
     */
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}