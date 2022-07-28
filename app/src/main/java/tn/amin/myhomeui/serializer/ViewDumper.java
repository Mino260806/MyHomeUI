package tn.amin.myhomeui.serializer;

import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import tn.amin.myhomeui.util.LogUtil;
import tn.amin.myhomeui.util.ViewUtil;


public class ViewDumper {
    private HashMap<String, Object> dumpedAttrs = new HashMap<>();

    public ViewDumper dumpView(View view) {
        if (dumpViewId(view)) {
            dumpViewInternal(view);
            dumpIfTextView(view);
            return this;
        }
        return null;
    }

    private boolean dumpViewId(View view) {
        String id = ViewUtil.getStringId(view);
        if (id.equals(ViewUtil.NO_ID)) return false;
        dumpedAttrs.put("id", id);
        return true;
    }

    private void dumpViewInternal(View view) {
        dumpedAttrs.put("locked", true);
        dumpedAttrs.put("type", "View");

        PointF location = ViewUtil.getRelativeLocation(view, (ViewGroup) view.getRootView());
        dumpedAttrs.put("x", location.x);
        dumpedAttrs.put("y", location.y);

        dumpedAttrs.put("rotation", view.getRotation());
        dumpedAttrs.put("paddingTop", view.getPaddingTop());
        dumpedAttrs.put("paddingBottom", view.getPaddingBottom());
        dumpedAttrs.put("paddingRight", view.getPaddingRight());
        dumpedAttrs.put("paddingLeft", view.getPaddingLeft());
    }

    private void dumpIfTextView(View view) {
        if (!(view instanceof TextView)) return;
        TextView textView = (TextView) view;

        dumpedAttrs.put("type", "TextView");

        dumpedAttrs.put("text", textView.getText().toString());
        dumpedAttrs.put("textSize", textView.getTextSize());
        dumpedAttrs.put("textColor", textView.getTextColors().getDefaultColor());
    }

    public Map<String, Object> getAttributes() { return dumpedAttrs; }
}
