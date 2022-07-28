package tn.amin.myhomeui.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {
    public static String getString(JSONObject o, String key, String defaultValue) {
        try {
            return o.getString(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static int getInt(JSONObject o, String key, int defaultValue) {
        try {
            return o.getInt(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static double getDouble(JSONObject o, String key, double defaultValue) {
        try {
            return o.getDouble(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static float getFloat(JSONObject o, String key, float defaultValue) {
        return (float) getDouble(o, key, (double) defaultValue);
    }

    public static boolean getBoolean(JSONObject o, String key, boolean defaultValue) {
        try {
            return o.getBoolean(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }
}
