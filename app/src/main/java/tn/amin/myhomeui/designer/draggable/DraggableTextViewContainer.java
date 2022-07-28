package tn.amin.myhomeui.designer.draggable;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tn.amin.myhomeui.serializer.factory.callback.TextViewCallbackMap;

public class DraggableTextViewContainer extends DraggableViewContainer<EditText> {
    public DraggableTextViewContainer(@NonNull Context context) {
        super(context);
        initView();
    }

    public DraggableTextViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DraggableTextViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    private void initView() {
        initView(EditText.class, TextViewCallbackMap.class);

        // Hide the underbar
        child.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        //child.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        addOnFocusChangeListener((v, hasFocus) -> {
            InputMethodManager imm =  (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!hasFocus) {
                // Hide Keyboard
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
            } else {
                // Show keyboard
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });
    }

    @Override
    protected void setupCallbacks() {
        super.setupCallbacks();
        DraggableCallbackMapExtension.addDraggableListeners(map, type, getContext());

        child.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String dateFormat = DateTimeInterpreter.interpretLiteral(s.toString());
                set("textClock", dateFormat, false);
            }
        });
    }
}
