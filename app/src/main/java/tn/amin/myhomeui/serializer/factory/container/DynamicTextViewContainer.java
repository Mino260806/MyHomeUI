package tn.amin.myhomeui.serializer.factory.container;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tn.amin.myhomeui.serializer.factory.callback.TextViewCallbackMap;

public class DynamicTextViewContainer extends DynamicViewContainer<TextView> {
    public boolean isClock = false;

    public DynamicTextViewContainer(@NonNull Context context) {
        super(context);
        initView();
    }

    public DynamicTextViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DynamicTextViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public DynamicTextViewContainer(@NonNull Context context, boolean isClock) {
        super(context);
        this.isClock = isClock;
        if (isClock) initViewWithClock();
        else initView();
    }

    private void initViewWithClock() {
        initView(TextClock.class, TextViewCallbackMap.class);
    }

    private void initView() {
        initView(TextView.class, TextViewCallbackMap.class);
    }
}
