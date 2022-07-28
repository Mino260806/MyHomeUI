package tn.amin.myhomeui.designer.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.HashMap;

import tn.amin.myhomeui.R;
import tn.amin.myhomeui.serializer.factory.ISerializableView;
import tn.amin.myhomeui.serializer.factory.container.DynamicViewContainer;
import tn.amin.myhomeui.util.ViewUtil;

public class BaseToolBarFormatting extends HorizontalScrollView {
    private int buttonSize;
    private int separatorWidth;

    private HashMap<Integer, ArrayList<ToolBarButton>> mToggleButtonGroups = new HashMap<>();
    private ISerializableView mCurrentView;
    protected final ArrayList<ToolBarButton> mToggleButtons = new ArrayList<>();
    protected ToolBar mParent;
    protected LinearLayout mLayout;

    public BaseToolBarFormatting(Context context) {
        super(context);
    }

    public BaseToolBarFormatting(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseToolBarFormatting(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseToolBarFormatting(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setVisibility(INVISIBLE);

        buttonSize = getResources().getDimensionPixelSize(R.dimen.toolbar_button_size);
        separatorWidth = getResources().getDimensionPixelSize(R.dimen.separator_width);
        int padding = getResources().getDimensionPixelSize(R.dimen.average_padding);

        mLayout = new LinearLayout(getContext());
        mLayout.setOrientation(LinearLayout.HORIZONTAL);
        mLayout.setPadding(padding, padding, padding, padding);
        addView(mLayout);
    }

    @SuppressWarnings("unchecked")
    public void bind(ISerializableView view) {
        mCurrentView = view;
        for (ToolBarButton button: mToggleButtons) {
            button.resetSelection();
        }
    }

    public void unbind() {
        mCurrentView = null;
    }

    protected ISerializableView getCurrentView() {
        return mCurrentView;
    }

    protected ToolBarButton addSliderButton(@DrawableRes int icon, Slider.OnChangeListener listener,
                                   ViewUtil.SliderCallback callback, int min, int max) {
        ToolBarButton button = addToggleButtonInternal(icon, (b, selected) -> {
            // If slider bar is already bound to another button, unselect the button
            View boundButton = mParent.getSliderBar().getBoundButton();
            if (boundButton != null) {
                boundButton.setSelected(false);
            }
            if (selected) {
                mParent.getSliderBar().bind(b);
                mParent.getSliderBar().getSlider().setValueFrom(min);
                mParent.getSliderBar().getSlider().setValueTo(max);
                mParent.getSliderBar().getSlider().setValue(callback.getValue());
                mParent.showSlider(listener);
            }
            else {
                mParent.getSliderBar().unbind();
                mParent.hideSlider();
            }
        }, () -> false, Integer.MIN_VALUE);
        return button;
    }

    protected ToolBarButton addClickableButton(@DrawableRes int icon, OnClickListener listener) {
        ToolBarButtonFactory factory = new ToolBarButtonFactory();
        factory.setIconRes(icon);
        factory.setSize(buttonSize);
        ToolBarButton button = factory.build(getContext());
        button.setOnClickListener(listener);
        mLayout.addView(button);
        return button;
    }

    protected ToolBarButton addToggleButton(@DrawableRes int icon,
                                            ToolBarButton.OnSelectionChangedListener listener,
                                            ToolBarButton.SelectionCallback callback,
                                            int groupId) {
        return addToggleButtonInternal(icon, listener, callback, groupId);
    }

    protected ToolBarButton addToggleButton(@DrawableRes int icon,
                                        ToolBarButton.OnSelectionChangedListener listener,
                                        ToolBarButton.SelectionCallback callback) {
        return addToggleButtonInternal(icon, listener, callback, Integer.MIN_VALUE);
    }

    private ToolBarButton addToggleButtonInternal(@DrawableRes int icon,
                                                  ToolBarButton.OnSelectionChangedListener listener,
                                                  ToolBarButton.SelectionCallback callback,
                                                  int groupId) {
        if (groupId != Integer.MIN_VALUE && !mToggleButtonGroups.containsKey(groupId))
            mToggleButtonGroups.put(groupId, new ArrayList<>());
        ArrayList<ToolBarButton> group = mToggleButtonGroups.getOrDefault(groupId, null);

        ToolBarButtonFactory factory = new ToolBarButtonFactory();
        factory.setIconRes(icon);
        factory.setOnSelectionChangedListener(listener);
        factory.setDefaultSelectionCallback(callback);
        factory.setSize(buttonSize);
        factory.setToggleable(true);
        factory.setGroup(group);
        ToolBarButton button = factory.build(getContext());
        mLayout.addView(button);
        mToggleButtons.add(button);
        if (group != null) group.add(button);
        return button;
    }

    public void setParent(ToolBar parent) {
        this.mParent = parent;
    }

    protected View addSeparator() {
        View separator = new View(getContext());
        separator.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark_grey));
        LayoutParams params = new LayoutParams(separatorWidth, buttonSize);
        params.rightMargin = buttonSize / 4;
        mLayout.addView(separator, params);
        return separator;
    }
}
