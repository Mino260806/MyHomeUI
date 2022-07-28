package tn.amin.myhomeui.serializer.factory.container;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tn.amin.myhomeui.serializer.factory.callback.ImageViewCallbackMap;
import tn.amin.myhomeui.serializer.factory.callback.ViewCallbackMap;
import tn.amin.myhomeui.storage.StorageManager;

public class DynamicImageViewContainer extends DynamicViewContainer<ImageView> {
    public DynamicImageViewContainer(@NonNull Context context) {
        super(context);
        initView();
    }

    public DynamicImageViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DynamicImageViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public DynamicImageViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        initView(ImageView.class, ImageViewCallbackMap.class);
    }
}
