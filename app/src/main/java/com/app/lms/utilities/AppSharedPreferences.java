package com.app.lms.utilities;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.lms.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class AppSharedPreferences {

    /*store string value into shared preference**/
    public static void setStringPreference(Context context, String key, String value) {
        //if (isPreferenceExist) {
        SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE).edit();
        preferenceEditor.putString(key, value);
        preferenceEditor.apply();
    }

    public static void setIntPreference(Context context, String key, Integer value) {
        //if (isPreferenceExist) {
        SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE).edit();
        preferenceEditor.putInt(key, value);
        preferenceEditor.apply();
    }

    public static void setChannelName(Context context, String value) {
        //if (isPreferenceExist) {
        SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE).edit();
        preferenceEditor.putString("channel", value);
        preferenceEditor.apply();
    }

    public static String getChannelName(Context context) {
        //if (isPreferenceExist) {
        SharedPreferences preference = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE);
        return preference.getString("channel", "");
    }

    public static boolean isKeyExist(Context context, String key) {
        //if (isPreferenceExist) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        return preferences.contains(key);
    }

    /*store string value into shared preference**/
    public static void setBooleanPreference(Context context, String key, boolean value) {
        //if (isPreferenceExist) {
        SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE).edit();
        preferenceEditor.putBoolean(key, value);
        preferenceEditor.apply();
    }

    /*retrieve boolean value from shared preference**/
    public static Boolean getBoolean(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE);
        //if (isPreferenceExist) {
        return prefs.getBoolean(key, false);
    }

    /*retrieve string value from shared preference**/
    public static String getStringSharedPreference(Context context, String key) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE);
            //if (isPreferenceExist) {
            return prefs.getString(key, "");
        } else return "";
    }

    public static Integer getIntSharedPreference(Context context, String key) {
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE);
            //if (isPreferenceExist) {
            return prefs.getInt(key, 0);
        } else return 0;
    }

    /*remove string value from shared preference**/
    public static void removeStringSharedPreference(Context context, String key) {
        //if (isPreferenceExist) {
        SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(context.getString(R.string.app_name), MODE_PRIVATE).edit();
        preferenceEditor.remove(key);
        preferenceEditor.apply();
    }

    /*store integer value into shared preference**/
    public static void setIntToSharedPreference(Context context, String fileName, String key, int value) {
        //if (isPreferenceExist) {
        SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(fileName, MODE_PRIVATE).edit();
        preferenceEditor.putInt(key, value);
        preferenceEditor.apply();
    }

    /*remove integer value from shared preference**/
    public static void removeIntegerSharedPreference(Context context, String fileName, String key) {
        //if (isPreferenceExist) {
        SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(fileName, MODE_PRIVATE).edit();
        preferenceEditor.remove(key);
        preferenceEditor.apply();
    }

    /*retrieve integer value from shared preference**/
    public static Integer getIntFromSharedPreference(Context context, String fileName, String key) {
        SharedPreferences prefs = context.getSharedPreferences(fileName, MODE_PRIVATE);
        //if (isPreferenceExist) {
        return prefs.getInt(key, 0);
    }

    /*retrieve integer value from shared preference**/
    public static Long getLongFromSharedPreference(Context context, String fileName, String key) {
        SharedPreferences prefs = context.getSharedPreferences(fileName, MODE_PRIVATE);
        //if (isPreferenceExist) {
        return prefs.getLong(key, 0);
    }

    /*remove integer value from shared preference**/
    public static void clearSharedPreference(Context context, String fileName) {
        //if (isPreferenceExist) {
        SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(fileName, MODE_PRIVATE).edit();
        preferenceEditor.clear();
        preferenceEditor.apply();
    }


    public static void removesharedpref(Context context, String fileName) {
        SharedPreferences prefs = context.getSharedPreferences(fileName, MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(fileName, MODE_PRIVATE).edit();
        preferenceEditor.remove(fileName);
        preferenceEditor.apply();
    }

    /*store string value into shared preference**/
    public static void setBooleanHashMapPreference(Context context, String fileName, HashMap<String, Boolean> isHindiAvailable) {
        //if (isPreferenceExist) {
        SharedPreferences prefs = context.getSharedPreferences(fileName, MODE_PRIVATE);
        SharedPreferences.Editor preferenceEditor = context.getSharedPreferences(fileName, MODE_PRIVATE).edit();
        for (String s : isHindiAvailable.keySet()) {
            if (prefs.contains(s)) {
                preferenceEditor.remove(s);
            }
            preferenceEditor.putBoolean(s, isHindiAvailable.get(s));
        }
        preferenceEditor.commit();
    }

    /*store BooleanHashMap value into shared preference**/
    public static void saveBooleanHashMap(Context context, HashMap<String, Boolean> inputMap, String mapKey) {
        SharedPreferences pSharedPref = context.getSharedPreferences(mapKey, Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove(mapKey).apply();
            editor.putString(mapKey, jsonString);
            editor.commit();
        }
    }

    /*load BooleanHashMap value from shared preference**/
    public static HashMap<String, Boolean> loadBooleanHashMap(Context context, String mapKey) {
        HashMap<String, Boolean> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = context.getSharedPreferences(mapKey, Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString(mapKey, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    outputMap.put(key, jsonObject.getBoolean(key));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }
}
