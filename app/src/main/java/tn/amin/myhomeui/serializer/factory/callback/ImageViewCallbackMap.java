package tn.amin.myhomeui.serializer.factory.callback;

import android.widget.ImageView;

import java.io.File;

import tn.amin.myhomeui.serializer.factory.container.DynamicViewContainer;
import tn.amin.myhomeui.storage.StorageManager;
import tn.amin.myhomeui.util.PrimitiveUtil;

public class ImageViewCallbackMap extends ViewCallbackMap<ImageView> {
    public ImageViewCallbackMap(ImageView child, DynamicViewContainer<ImageView> parent, String type) {
        super(child, parent, type);

        addCallback("image", null, value -> setImageFile((File) value));
        addCallback("width", 0f, value -> {
            child.getLayoutParams().width = PrimitiveUtil.unboxInt(value);
            child.requestLayout();
        });
        addCallback("height", 0f, value -> {
            child.getLayoutParams().height = PrimitiveUtil.unboxInt(value);
            child.requestLayout();
        });

        child.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    public void setImageFile(File file) {
        container.setCorrupted(!StorageManager.loadImageInto(file, container.getChild()));
    }
}
