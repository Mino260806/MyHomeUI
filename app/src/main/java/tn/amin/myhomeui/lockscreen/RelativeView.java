package tn.amin.myhomeui.lockscreen;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.Arrays;

import tn.amin.myhomeui.util.DimensionUtil;
import tn.amin.myhomeui.util.DisplayUtil;
import tn.amin.myhomeui.util.LogUtil;
import tn.amin.myhomeui.util.ViewUtil;

public class RelativeView extends View {
    public RelativeView(Context context) {
        super(context);
    }

    public RelativeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RelativeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private static int topMargin = -1;
    public void setAlignment(int alignment) {
        if (getParent() == null || !(getParent() instanceof ConstraintLayout)) {
            LogUtil.warn("RelativeView should be added to a ConstraintLayout");
            return;
        }

        ViewUtil.iterateView((View) getParent(), (view) -> {
            view.setId(generateViewId());
            return true;
        });

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                DimensionUtil.dpToPx(70, getContext()),
                DimensionUtil.dpToPx(70, getContext())
        );
        setLayoutParams(params);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) getParent());
        if ((alignment & ParentAlignment.TOP) != 0) {
            // When put on top, it will be under the statusbar, so it wont be clickable
            if (topMargin == -1)
                topMargin = DisplayUtil.getStatusBarHeight((ViewGroup) getRootView());
            constraintSet.connect(getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            params.height += topMargin;
        }
        if ((alignment & ParentAlignment.BOTTOM) != 0)  constraintSet.connect(getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        if ((alignment & ParentAlignment.RIGHT) != 0)  constraintSet.connect(getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        if ((alignment & ParentAlignment.LEFT) != 0)  constraintSet.connect(getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        constraintSet.applyTo((ConstraintLayout) getParent());
    }

    final static class ParentAlignment {
        public static final int LEFT = 0b1000;
        public static final int TOP = 0b0100;
        public static final int RIGHT = 0b0010;
        public static final int BOTTOM = 0b0001;
    }
}
