package com.ttc.contactsgrid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
 
/**
 * Class use to manager shared preference use to in application
 * 
 * @author OPTIMUS
 * 
 */
public class MyPreference {
 
    private static MyPreference instance;
    private SharedPreferences preferences;
 
    /**
     * Contractor
     * 
     * @param context
     */
    private MyPreference(Context context) {
    	//if use one file default
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    	
        //if use many file
        //preferences = context.getSharedPreferences("filename", 0);
    }
 
    /**
     * Use to initialization and get object MyPreferenceII
     * 
     * @param context
     */
    public static MyPreference getInstance(Context context) {
        if (instance == null) {
            instance = new MyPreference(context);
        }
 
        return instance;
    }
 
    /**
     * Set an int value in the preferences editor
     * 
     * @param key
     *            : name of preference to modify
     * @param value
     *            : the new value of preference
     * @return : MyPreference to continue edit
     */
    public MyPreference putInt(String key, int value) {
        preferences.edit().putInt(key, value).commit();
        return instance;
    }
 
    /**
     * Retrieve an int value from the preferences.
     * 
     * @param key
     *            : the name of preference to retrieve
     * @param defaultValue
     *            : value to return if this preference does not exist
     * @return : return the preference value if it exist, else return default
     *         value
     */
    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }
 
    /**
     * Set a long value in preference editor
     * 
     * @param key
     *            : name of preference to modify
     * @param value
     *            : the new value of preference
     * @return : MyPreference to continue edit
     */
    public MyPreference putLong(String key, long value) {
        preferences.edit().putLong(key, value).commit();
        return instance;
    }
 
    /**
     * Retrieve an long value from the preferences
     * 
     * @param key
     *            : the name of preference to retrieve
     * @param defaultValue
     *            : value to return if this preference does not exist
     * @return : return the preference value if exist, else return default value
     */
    public long getLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }
 
    /**
     * Set a String value in preference editor
     * 
     * @param key
     *            : name of preference to modify
     * @param value
     *            : the new value of preference
     * @return : return MyPreference to continue edit
     */
    public MyPreference putString(String key, String value) {
        preferences.edit().putString(key, value).commit();
        return instance;
    }
 
    /**
     * Retrieve a String value from the preference
     * 
     * @param key
     *            : the name of preference to retrieve
     * @param defaultValue
     *            : value to return if preference does not exist
     * @return : return the preference value if exist, else return default value
     */
    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }
 
    /**
     * Set a boolean value in preference editor
     * 
     * @param key
     *            : name of preference to modify
     * @param value
     *            : the new value of preference
     * @return : return MyPreference to continue edit
     */
    public MyPreference putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).commit();
        return instance;
    }
 
    /**
     * Retrieve a boolean value from the preferences
     * 
     * @param key
     *            : the name of preference to retrieve
     * @param defaultValue
     *            : value to return if preference does not exist
     * @return : return the preference value if exist, else return default value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }
 
    /**
     * Set a float value in preference editor
     * 
     * @param key
     *            : name of preference to modify
     * @param value
     *            : the new value of preference
     * @return : return MyPreference to continue edit
     */
    public MyPreference putFloat(String key, float value) {
        preferences.edit().putFloat(key, value).commit();
        return instance;
    }
 
    /**
     * Retrieve a float value from the preferences
     * 
     * @param key
     *            : the name of preference to retrieve
     * @param defaultValue
     *            : value to return if preference dose not exist
     * @return : return the preference value if exist, else return default value
     */
    public float getFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }
 
}