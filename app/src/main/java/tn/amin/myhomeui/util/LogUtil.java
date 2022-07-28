package tn.amin.myhomeui.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class LogUtil {
    static String TAG = "LSPosed-Bridge/MyHomeUI";
    static WeakReference<Context> context = null;

    public static void debug(String message) {
        Log.d(TAG, message);
    }

    public static void warn(String message) {
        Log.w(TAG, message);
    }

    public static void error(Throwable t) {
        error("", t);
    }
    public static void error(String message, Throwable t) {
        Log.e(TAG, message, t);
        if (context != null && !message.isEmpty())
            Toast.makeText(context.get(),
                    t.getClass().getSimpleName() + ": " + message,
                    Toast.LENGTH_LONG).show();
    }

    public static void errorAndExit(Throwable t) {
        errorAndExit("", t);
    }
    public static void errorAndExit(String message, Throwable t) {
        error(message, t);
        System.exit(-1);
    }

    public static void wtf(String message, Throwable t) {
        Log.wtf(TAG, message, t);
        System.exit(-1);
    }

    public static void setContext(Context context) {
        LogUtil.context = new WeakReference<>(context);
    }
}
