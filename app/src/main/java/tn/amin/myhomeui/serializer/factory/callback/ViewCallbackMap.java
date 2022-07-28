package tn.amin.myhomeui.serializer.factory.callback;

import android.graphics.Color;
import android.graphics.PointF;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import tn.amin.myhomeui.R;
import tn.amin.myhomeui.serializer.factory.callback.CallbackMap;
import tn.amin.myhomeui.serializer.factory.container.DynamicViewContainer;
import tn.amin.myhomeui.util.PrimitiveUtil;
import tn.amin.myhomeui.util.ViewUtil;

public class ViewCallbackMap<T extends View> extends CallbackMap<String, Object> {
    public DynamicViewContainer<T> container;
    public ViewCallbackMap(T child, DynamicViewContainer<T> parent, String type) {
        container = parent;

        put("type", type);
        addCallback("alpha", 1f, (value) -> child.setAlpha(PrimitiveUtil.unboxFloat(value)));
        addCallback("rotation", 0, (value) -> child.setRotation(PrimitiveUtil.unboxFloat(value)));
        // These will be overridden by DraggableViewContainer
        PointF location = ViewUtil.getRelativeLocation(child, parent);
        addCallback("x", Float.MIN_VALUE, (value) -> container.setX(PrimitiveUtil.unboxFloat(value)), new CallbackMap.GetterCallback<Object>() {
            @Override
            public Object onValueGet() {
                return location.x;
            }
            @Override
            public boolean needToFire(Object value) {
                return PrimitiveUtil.unboxFloat(value) == Float.MIN_VALUE;
            }
        });
        addCallback("y", Float.MIN_VALUE, (value) -> container.setY(PrimitiveUtil.unboxFloat(value)), new CallbackMap.GetterCallback<Object>() {
            @Override
            public Object onValueGet() {
                return location.y;
            }
            @Override
            public boolean needToFire(Object value) {
                return PrimitiveUtil.unboxFloat(value) == Float.MIN_VALUE;
            }
        });

        child.setPadding(0, 0, 0, 0);
    }
}
