package tn.amin.myhomeui.lockscreen;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tn.amin.myhomeui.util.DisplayUtil;
import tn.amin.myhomeui.util.LogUtil;

public class TouchInterceptorView extends RelativeLayout {
    public TouchInterceptorView(Context context) {
        super(context);
    }

    public TouchInterceptorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchInterceptorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TouchInterceptorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        requestLayout();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        LogUtil.debug("onInterceptTouchEvent");
        return true;
    }
}
