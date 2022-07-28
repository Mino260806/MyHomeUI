package tn.amin.myhomeui.util;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.AnimRes;
import androidx.annotation.IdRes;

import java.lang.reflect.Field;
import java.util.function.Predicate;

public class ViewUtil {
    public static void doOnPreDraw(final View view, final Predicate<View> predicate) {
        final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                if (!predicate.test(view)) {
                    doOnPreDraw(view, predicate);
                }
                return true;
            }
        };
        view.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }

    public static <T extends View> T findViewByStringId(ViewGroup root, String name) {
        @IdRes int idRes = root.getContext().getResources().getIdentifier(name, "", "");
        return root.findViewById(idRes);
    }

    public static final String NO_ID = "";
    public static String getStringId(View view) {
        if (view == null) return NO_ID;
        if (view.getId() == View.NO_ID) return NO_ID;
        String resourceName;
        try {
            resourceName = view.getResources().getResourceName(view.getId());
        } catch (Resources.NotFoundException e) {
            return NO_ID;
        }
        return resourceName;
    }

    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
    @SuppressWarnings("unchecked")
    public static <T> T getListener(View view, Class<T> cls) {
        T retrievedListener = null;
        String viewStr = "android.view.View";
        String lInfoStr = "android.view.View$ListenerInfo";

        try {
            Field listenerInfoField = Class.forName(viewStr).getDeclaredField("mListenerInfo");
            listenerInfoField.setAccessible(true);
            Object listenerInfo = listenerInfoField.get(view);

            Field listenerField = Class.forName(lInfoStr).getDeclaredField("m" + cls.getSimpleName());
            listenerField.setAccessible(true);

            if (listenerInfo != null) {
                retrievedListener = (T) listenerField.get(listenerInfo);
            }
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            LogUtil.errorAndExit("Could not retrieve OnClickListener", e);
        }

        return (T) retrievedListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    public static <T> T addAnotherListener(View view, T listener, Class<T> listenerCls) {
        T oldListener = getListener(view, listenerCls);
        if (listenerCls == View.OnLongClickListener.class) {
            view.setOnLongClickListener((v) -> {
                if (oldListener != null) ((View.OnLongClickListener) oldListener).onLongClick(v);
                return ((View.OnLongClickListener) listener).onLongClick(v);
            });
        } else if (listenerCls == View.OnTouchListener.class) {
            view.setOnTouchListener((v, motionEvent) -> {
                if (oldListener != null) ((View.OnTouchListener) oldListener).onTouch(v, motionEvent);
                return ((View.OnTouchListener) listener).onTouch(v, motionEvent);
            });
        } else {
            throw new UnsupportedOperationException();
        }
        return oldListener;
    }

    public static void animate(View view, @AnimRes int animRes, OnAnimationEndListener listener) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), animRes);
        view.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onAnimationEnd();
            }
        });
    }

    public interface OnAnimationEndListener {

        void onAnimationEnd();
    }
    public interface SliderCallback {

        float getValue();
    }
    public static Bitmap screenshot(View view, int width, int height) {
        final Bitmap bmp = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        view.measure(
                View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY));
        view.layout(0,0,view.getMeasuredWidth(),view.getMeasuredHeight());
        view.draw(canvas);
        return bmp;
    }

    public static Bitmap screenshot(View v) {
        if (v.getWidth() <= 0 || v.getHeight() <= 0) {
            LogUtil.warn("width or height is null");
            return null;
        }
        int oldVisibility = v.getVisibility();
        v.setVisibility(View.VISIBLE);
        Bitmap b = null;
        try {
            v.setDrawingCacheEnabled(true);
            b = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);
        } catch (NullPointerException ignored) {}
        if (b == null) {
            b = Bitmap.createBitmap(v.getWidth() , v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.draw(c);
        }
        v.setVisibility(oldVisibility);
        return b;
    }

    public static PointF getRelativeLocation(View child, ViewGroup parent) {
        LogUtil.debug("Getting relative location");

        if (parent == null) return new PointF(0, 0);
        if (parent.getScaleX() == child.getScaleX()
                && parent.getScaleY() == child.getScaleY()) {
            int[] childLoc = new int[2];
            int[] parentLoc = new int[2];
            child.getLocationOnScreen(childLoc);
            parent.getLocationOnScreen(parentLoc);
            return new PointF(childLoc[1] - parentLoc[0] , childLoc[1] - parentLoc[1]);
        }
        if (child.getParent() == parent)
            return new PointF(child.getX(), child.getY());
        else {
            PointF point = getRelativeLocation(parent, (ViewGroup) child.getParent());
            point.x += child.getX();
            point.y += child.getY();
            return point;
        }
    }

    public static void iterateView(View root, ViewCallback callback) {
        if (root instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) root;
            if (callback.onViewFound(viewGroup)) {
                for (int i=0; i < viewGroup.getChildCount(); i++) {
                    View child = viewGroup.getChildAt(i);
                    iterateView(child, callback);
                }
            }
        } else {
            callback.onViewFound(root);
        }
    }

    public interface ViewCallback {

        boolean onViewFound(View view);
    }

    public static boolean isVisible(View view) {
        return isVisible(view, VisibilityFlags.ALL);
    }

    public static boolean isVisible(View view, int flags) {
        if (view == null)
            return false;
        if (((flags & VisibilityFlags.IS_SHOWN) != 0) && !view.isShown())
            return false;
        if ((flags & VisibilityFlags.IS_OUT_OF_SCREEN) != 0) {
            final Rect actualPosition = new Rect();
            boolean isGlobalVisible = view.getGlobalVisibleRect(actualPosition);
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
            final Rect screen = new Rect(0, 0, screenWidth, screenHeight);
            return isGlobalVisible && actualPosition.intersect(screen);
        }
        return true;
    }

    public static class VisibilityFlags {
        public final static int ALL = 0b0011;

        public final static int IS_SHOWN = 0b00001;
        public final static int IS_OUT_OF_SCREEN = 0b00010;
    }
}
