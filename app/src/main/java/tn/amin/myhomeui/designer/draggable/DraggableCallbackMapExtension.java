package tn.amin.myhomeui.designer.draggable;

import android.content.Context;
import android.view.View;

import tn.amin.myhomeui.R;
import tn.amin.myhomeui.serializer.factory.callback.ViewCallbackMap;
import tn.amin.myhomeui.util.PrimitiveUtil;

public class DraggableCallbackMapExtension {
    static float margin = -1;
    public static <T extends View> void addDraggableListeners(ViewCallbackMap<T> map, String type, Context context) {
        map.put("type", type);
        if (margin == -1) {
            margin = context.getResources().getDimension(R.dimen.draggable_view_margin);
        }
        map.addCallback("x", 0f, (value) -> map.container.setX(PrimitiveUtil.unboxFloat(value) - margin));
        map.addCallback("y", 0f, (value) -> map.container.setY(PrimitiveUtil.unboxFloat(value) - margin));
    }
}
