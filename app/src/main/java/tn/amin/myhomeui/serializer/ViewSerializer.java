package tn.amin.myhomeui.serializer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import tn.amin.myhomeui.designer.draggable.DraggableImageViewContainer;
import tn.amin.myhomeui.designer.draggable.DraggableTextViewContainer;
import tn.amin.myhomeui.serializer.factory.ISerializableView;
import tn.amin.myhomeui.serializer.factory.callback.ViewCallbackMap;
import tn.amin.myhomeui.serializer.factory.container.DynamicImageViewContainer;
import tn.amin.myhomeui.serializer.factory.container.DynamicTextViewContainer;
import tn.amin.myhomeui.serializer.factory.container.DynamicViewContainer;

public class ViewSerializer {
    public static Map<String, Object> serialize(View view, ViewGroup parent) {
        HashMap<String, Object> map = new HashMap<>();
        String type = "View";

        view.clearFocus();
        if (view instanceof ISerializableView) {
            // Originated from designer
            map.putAll(((ISerializableView) view).getAttributes());
        }
        else {
            // Originated from lockscreen
            ViewDumper dump = new ViewDumper().dumpView(view);
            if (dump != null)
                map.putAll(dump.getAttributes());
        }
        map.putIfAbsent("type", type);
        return map;
    }

    public static View deserialize(ViewGroup parent, Map<String, Object> attrs, boolean editable) throws JSONException {
        return deserialize(parent, attrs, editable, () -> {});
    }

    @SuppressWarnings("unchecked")
    public static View deserialize(ViewGroup parent, Map<String, Object> attrs, boolean editable, Runnable corruptedCallback) throws JSONException {
        Context context = parent.getContext();
        DynamicViewContainer<?> view = null;
        Object type = attrs.get("type");
        if (type == null) return null;
        switch (type.toString()) {
            case "EditText":
            case "TextView":
                // TODO use factory
                if (editable) view = new DraggableTextViewContainer(context);
                else {
                    boolean isClock = attrs.getOrDefault("textClock", null) != null;

                    view = new DynamicTextViewContainer(context, isClock);
                }
                break;
            case "ImageView":
                if (editable) view = new DraggableImageViewContainer(context);
                else view = new DynamicImageViewContainer(context);
                break;

            default:
                break;
        }

        if (view != null) {
            parent.addView(view);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(params);
            view.setAttributes(attrs);
            if (view.isCorrupted()) {
                parent.removeView(view);
                corruptedCallback.run();
                view = null;
            }
        }
        return view;
    }
}
