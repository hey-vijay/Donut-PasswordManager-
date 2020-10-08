package vijay.bhadolia.key.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SmartPreferences {
    private static final String TAG = SmartPreferences.class.getName();

    private SharedPreferences sharedPreferences;
    private static SmartPreferences smartPreferences;

    public static SmartPreferences getInstance(Context context) {
        if(smartPreferences == null) {
            smartPreferences = new SmartPreferences(context);
        }
        return smartPreferences;
    }

    SmartPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.APP_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    //Overloaded setter functions for key value pairs calls
    public void saveValue(String key,String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }
    public void saveValue(String key,Integer value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply();
    }
    public void saveValue(String key,Boolean value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }
    public void saveValue(String key,Float value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putFloat(key, value);
        prefsEditor.apply();
    }



    //Overloaded getter functions for key default value pairs
    public String getValue(String key, String defaultValue) {
        if (sharedPreferences!= null) {
            return sharedPreferences.getString(key, defaultValue);
        }
        return defaultValue;
    }
    public Integer getValue(String key, Integer defaultValue) {
        if (sharedPreferences!= null) {
            return sharedPreferences.getInt(key, defaultValue);
        }
        return defaultValue;
    }
    public Boolean getValue(String key, Boolean defaultValue) {
        if (sharedPreferences!= null) {
            return sharedPreferences.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }
    public Float getValue(String key, Float defaultValue) {
        if (sharedPreferences!= null) {
            return sharedPreferences.getFloat(key, defaultValue);
        }
        return defaultValue;
    }
}
