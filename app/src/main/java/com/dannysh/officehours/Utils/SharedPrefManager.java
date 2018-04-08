package com.dannysh.officehours.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by Danny on 03-Apr-18.
 */
public class SharedPrefManager {
    private static SharedPreferences prefs = null;

    private static SharedPreferences getSettings(Context context){
        if (prefs == null){
            prefs = context.getApplicationContext().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        }
        return prefs;
    }

    public static Boolean getBoolean(Context context, String key, Boolean defValue){
        return getSettings(context).getBoolean(key, defValue);
    }

    public static void setBoolean(Context context, String key, Boolean value){
        saveChanges(getSettings(context).edit().putBoolean(key, value));
    }

    public static String getString(Context context, String key, String defValue){
        return getSettings(context).getString(key, defValue);
    }

    public static void setString(Context context, String key, String value){
        saveChanges(getSettings(context).edit().putString(key, value));
    }

    public static Long getLong(Context context, String key, Long defValue){
        return getSettings(context).getLong(key, defValue);
    }

    public static void setLong(Context context, String key, Long value){
        saveChanges(getSettings(context).edit().putLong(key, value));
    }

    public static void setDouble(Context context, String key, Double value){
        saveChanges(getSettings(context).edit().putLong(key, Double.doubleToRawLongBits(value)));
    }
    public static double getDouble(Context context, String key, double defValue){
        return getSettings(context).getLong(key, Double.doubleToLongBits(defValue));
    }
    private static void saveChanges(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD)
            editor.commit();
        else
            editor.apply();
    }

}