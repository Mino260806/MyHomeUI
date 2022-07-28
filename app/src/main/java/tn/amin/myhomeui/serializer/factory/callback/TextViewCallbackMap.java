package tn.amin.myhomeui.serializer.factory.callback;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.text.LineBreaker;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import tn.amin.myhomeui.serializer.factory.container.DynamicTextViewContainer;
import tn.amin.myhomeui.serializer.factory.container.DynamicViewContainer;
import tn.amin.myhomeui.util.PrimitiveUtil;

public class TextViewCallbackMap extends ViewCallbackMap<TextView> {
    public TextViewCallbackMap(TextView child, DynamicViewContainer<TextView> parent, String type) {
        super(child, parent, type);

        if (parent instanceof DynamicTextViewContainer &&
                ((DynamicTextViewContainer) parent).isClock)
            addCallback("textClock", "'Text'",
                    value -> {
                        ((TextClock) child).setFormat12Hour((CharSequence) value);
                        ((TextClock) child).setFormat24Hour((CharSequence) value);
                    });
        else addCallback("text", "Text", value -> child.setText((CharSequence) value));
        Map<Integer, Integer> gravityRelation = new HashMap<Integer, Integer>() {{
            put(TextView.TEXT_ALIGNMENT_VIEW_START, Gravity.START);
            put(TextView.TEXT_ALIGNMENT_CENTER, Gravity.CENTER_HORIZONTAL);
            put(TextView.TEXT_ALIGNMENT_VIEW_END, Gravity.END);
        }};
        addCallback("textAlignment", TextView.TEXT_ALIGNMENT_VIEW_START, value -> {
            child.setTextAlignment((int) value);
            child.setGravity(PrimitiveUtil.unboxInt(gravityRelation.get((int) value)));
        });
        addCallback("textColor", Color.BLACK, value -> child.setTextColor(PrimitiveUtil.unboxInt(value)));
        addCallback("textSize", 120f, value -> child.setTextSize(TypedValue.COMPLEX_UNIT_PX, PrimitiveUtil.unboxFloat(value)));
        addCallback("fontfamily", null, value -> setFontFamily((String) value));
        addCallback("bold", false, value -> updateTypeFace());
        addCallback("italic", false, value -> updateTypeFace());
        addCallback("underline", false, value -> updatePaintFlags());
        addCallback("strikethrough", false, value -> updatePaintFlags());

        fireSetter("text");
        fireSetter("textColor");
        fireSetter("textSize");
        updateTypeFace();
        updatePaintFlags();

        child.setHorizontallyScrolling(true);
        child.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                parent.set("text", text, false);
                if (text.isEmpty()) parent.setCorrupted(true);
            }
        });
    }

    private Typeface typeface = null;

    public void setFontFamily(String path) {
        if (path != null) {
            if (!new File(path).exists()) {
                put("fontfamily", null);
                return;
            }
            typeface = Typeface.createFromFile(path);
        }
        else typeface = null;
        updateTypeFace();
    }

    public void updateTypeFace() {
        if ((Boolean) get("bold") && (Boolean) get("italic"))
            container.getChild().setTypeface(typeface, Typeface.BOLD_ITALIC);
        else if ((Boolean) get("bold"))
            container.getChild().setTypeface(typeface, Typeface.BOLD);
        else if ((Boolean) get("italic"))
            container.getChild().setTypeface(typeface, Typeface.ITALIC);
        else
            container.getChild().setTypeface(typeface, Typeface.NORMAL);
    }

    public void updatePaintFlags() {
        int flags = container.getChild().getPaintFlags();
        if ((Boolean) get("underline"))
            flags |= Paint.UNDERLINE_TEXT_FLAG;
        else
            flags &= ~Paint.UNDERLINE_TEXT_FLAG;
        if ((Boolean) get("strikethrough"))
            flags |= Paint.STRIKE_THRU_TEXT_FLAG;
        else
            flags &= ~Paint.STRIKE_THRU_TEXT_FLAG;
        container.getChild().setPaintFlags(flags);
    }
}
