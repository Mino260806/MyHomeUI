package tn.amin.myhomeui.designer.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;

import tn.amin.myhomeui.R;

public class ToolBarSlider extends FrameLayout {
    private Slider mSlider;
    private View mBoundButton;

    public ToolBarSlider(@NonNull Context context) {
        super(context);
    }

    public ToolBarSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolBarSlider(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ToolBarSlider(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        inflate(getContext(), R.layout.toolbar_slider, this);

        mSlider = findViewById(R.id.slider);
    }

    public Slider getSlider() {
        return mSlider;
    }

    public void bind(View toolBarButton) {
        mBoundButton = toolBarButton;
    }

    public void unbind() {
        mBoundButton = null;
    }

    public View getBoundButton() {
        return mBoundButton;
    }
}
