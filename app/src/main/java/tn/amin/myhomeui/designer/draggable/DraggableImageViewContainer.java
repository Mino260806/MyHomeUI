package tn.amin.myhomeui.designer.draggable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tn.amin.myhomeui.R;
import tn.amin.myhomeui.serializer.factory.callback.ImageViewCallbackMap;
import tn.amin.myhomeui.util.LogUtil;
import tn.amin.myhomeui.util.PrimitiveUtil;
import tn.amin.myhomeui.util.ViewUtil;

public class DraggableImageViewContainer extends DraggableViewContainer<ImageView> {
    public DraggableImageViewContainer(@NonNull Context context) {
        super(context);
        initView();
    }

    public DraggableImageViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DraggableImageViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected static float defaultSize;
    private void initView() {
        defaultSize = getResources().getDimension(R.dimen.inserted_image_size);

        initView(ImageView.class, ImageViewCallbackMap.class);
    }

    public void scaleToDefaultSize() {
        setVisibility(INVISIBLE);

        Drawable image = child.getDrawable();
        if (image == null) {
            // TODO query size from Glide
            ViewUtil.doOnPreDraw(child, (v) -> {
                scaleToDefaultSize();
                return true;
            } );
            return;
        }
        int imageWidth = image.getIntrinsicWidth();
        int imageHeight = image.getIntrinsicHeight();
        int width, height;
        float scale = 1f;
        if (imageWidth > imageHeight) {
            width = Math.round(defaultSize);
            scale = (float) width / imageWidth;
            height = Math.round(imageHeight * scale);
        } else {
            height = Math.round(defaultSize);
            scale = (float) height / imageHeight;
            width = Math.round(imageWidth * scale);
        }
        set("baseWidth", width);
        set("baseHeight", height);

        set("width", width);
        set("height", height);

        setVisibility(VISIBLE);
    }

    private float ratio = 1f;
    public void setRatio(float ratio) {
        this.ratio = ratio;
        int baseWidth = PrimitiveUtil.unboxInt(map.getOrDefault("baseWidth", -1));
        int baseHeight = PrimitiveUtil.unboxInt(map.getOrDefault("baseHeight", -1));
        if (baseWidth == -1 || baseHeight == -1) return;
        set("width", baseWidth * ratio);
        set("height", baseHeight * ratio);
    }
    public float getRatio() {
        return ratio;
    }

    @Override
    protected void setupCallbacks() {
        super.setupCallbacks();
        DraggableCallbackMapExtension.addDraggableListeners(map, type, getContext());
    }
}
