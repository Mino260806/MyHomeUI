package tn.amin.myhomeui.designer.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ToolBarButton extends View {
    private SelectionCallback callback = () -> false;

    public ToolBarButton(Context context) {
        super(context);
    }

    public ToolBarButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolBarButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ToolBarButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setDefaultSelectionCallback(SelectionCallback callback) {
        this.callback = callback;
    }

    public void resetSelection() {
        setSelected(callback.getSelection());
    }

    public interface OnSelectionChangedListener {
        void onSelectionChanged(View view, boolean selected);
    }

    public interface SelectionCallback {
        boolean getSelection();
    }
}
