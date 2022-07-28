package tn.amin.myhomeui.designer.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/*
* Adapted from https://hiteshkrsahu.medium.com/how-to-create-vertical-seekbar-in-android-without-external-library-kotin-androidx-2ed84788e3e1
* */
public class VerticalSlider extends Slider {
    private final void initView(Context context) {
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(@NotNull Canvas c) {
        c.rotate(-90.0F);
        c.translate(-((float)getHeight()), 0.0F);
        super.onDraw(c);
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NotNull MotionEvent event) {
        if (!isEnabled()) {
            return false;
        } else {
            switch(event.getAction()) {
                case 0:
                case 1:
                case 2:
                    float value =  (getValueFrom() + (int)((float)(getValueTo() - getValueFrom()) * (getHeight() - event.getY()) / (float)getHeight()));
                    if (value < getValueFrom())
                        value = getValueFrom();
                    if (value > getValueTo())
                        value = getValueTo();
                    setValue(value);
                    onSizeChanged(getWidth(), getHeight(), 0, 0);
                case 3:
                default:
                    return true;
            }
        }
    }

    public VerticalSlider(@NotNull Context context) {
        super(context);
        initView(context);
    }

    public VerticalSlider(@NotNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public VerticalSlider(@NotNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
}
