package tn.amin.myhomeui.util;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class DisplayUtil {
    public static Point getSize(Context context) {
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        return outPoint;
    }

    private static int statusBarHeight = -1;
    public static int getStatusBarHeight(View root) {
        if (statusBarHeight != -1f) return statusBarHeight;

        View statusBar = ViewUtil.findViewByStringId((ViewGroup) root, "com.android.systemui:id/keyguard_header");
        if (statusBar != null && statusBar.getHeight() > 0) {
            statusBarHeight = statusBar.getHeight();
            return statusBarHeight;
        }

        // fallback
        int result = 0;
        int resourceId = root.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = root.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        statusBarHeight = result;
        return statusBarHeight;
    }
}
