package tn.amin.myhomeui.designer.draggable;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tn.amin.myhomeui.serializer.factory.callback.ImageViewCallbackMap;

public class LockscreenItemContainer extends DraggableViewContainer<ImageView> {
    public LockscreenItemContainer(@NonNull Context context) {
        super(context);
        initView();
    }

    public LockscreenItemContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LockscreenItemContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        initView(ImageView.class, ImageViewCallbackMap.class);
    }

    @Override
    public boolean isLocked() {
        return true;
    }

    @Override
    protected void setupCallbacks() {
        super.setupCallbacks();

        DraggableCallbackMapExtension.addDraggableListeners(map, type, getContext());
    }

    @Override
    protected void deleteMySelf() {
        setVisibility(INVISIBLE);
    }
}
