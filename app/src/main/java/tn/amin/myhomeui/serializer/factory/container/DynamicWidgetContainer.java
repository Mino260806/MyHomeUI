package tn.amin.myhomeui.serializer.factory.container;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tn.amin.myhomeui.serializer.factory.callback.WidgetCallbackMap;

public class DynamicWidgetContainer extends DynamicViewContainer<AppWidgetHostView> {
    public DynamicWidgetContainer(@NonNull Context context) {
        super(context);
        initView();
    }

    public DynamicWidgetContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DynamicWidgetContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public DynamicWidgetContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        initView(AppWidgetHostView.class, WidgetCallbackMap.class);
    }
}
