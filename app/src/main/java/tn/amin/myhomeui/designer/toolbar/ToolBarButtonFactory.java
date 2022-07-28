package tn.amin.myhomeui.designer.toolbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import tn.amin.myhomeui.R;

public class ToolBarButtonFactory {
    private @DrawableRes int iconRes = android.R.drawable.btn_star; // Dummy
    private int size = 50; // Dummy
    private boolean toggleable = false;
    private ToolBarButton.OnSelectionChangedListener listener = (b, s) -> {};
    private ToolBarButton.SelectionCallback callback = () -> false;
    private ArrayList<ToolBarButton> group = new ArrayList<>();

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public void setOnSelectionChangedListener(ToolBarButton.OnSelectionChangedListener listener) {
        this.listener = listener;
    }

    public void setDefaultSelectionCallback(ToolBarButton.SelectionCallback callback) {
        this.callback = callback;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setToggleable(boolean toggleable) {
        this.toggleable = toggleable;
    }

    public void setGroup(ArrayList<ToolBarButton> group) {
        this.group = group;
    }

    public ToolBarButton build(Context context) {
        ToolBarButton button = new ToolBarButton(context);
        button.setBackground(getBackground(context));

        if (toggleable) {
            button.setOnClickListener((b) -> {
                //Set the button's appearance
                button.setSelected(!button.isSelected());
                if (button.isSelected() && group != null) {
                    for (ToolBarButton sameGroupButton: group) {
                        if (sameGroupButton != button) sameGroupButton.setSelected(false);
                    }
                }
                listener.onSelectionChanged(button, button.isSelected());
            });
            button.setDefaultSelectionCallback(callback);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        params.rightMargin = size / 4;
        button.setLayoutParams(params);
        return button;
    }

    private Drawable getBackground(Context context) {
        Drawable icon = ContextCompat.getDrawable(context, iconRes);
        @DrawableRes int backgroundRes = toggleable? R.drawable.selector_togglable_toolbar_button:
                R.drawable.selector_pressable_toolbar_button;
        StateListDrawable background = (StateListDrawable) ContextCompat.getDrawable(context, backgroundRes);
        assert background != null;
        for (int i=0; i < background.getStateCount(); i++) {
            LayerDrawable layerDrawable = (LayerDrawable) background.getStateDrawable(i);
            layerDrawable.setDrawableByLayerId(R.id.toolbar_button_icon, icon);
        }

        background.setEnterFadeDuration(context.getResources().getInteger(android.R.integer.config_mediumAnimTime));
        return background;
    }
}
