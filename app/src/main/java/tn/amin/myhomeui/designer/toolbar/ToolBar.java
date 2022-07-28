package tn.amin.myhomeui.designer.toolbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.google.android.material.slider.Slider;

import tn.amin.myhomeui.R;
import tn.amin.myhomeui.serializer.factory.ISerializableView;
import tn.amin.myhomeui.serializer.factory.container.DynamicViewContainer;
import tn.amin.myhomeui.util.ViewUtil;

public class ToolBar extends LinearLayout {
//    private ToolBarTextFormatting mTextFormattingLayout;
//    private ToolBarImageFormatting mImageFormattingLayout;
    private ToolBarSlider mSliderBar;

    private ToolBarFormatting mFormattingLayout = null;

    public ToolBar(Context context) {
        super(context);
    }

    public ToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ToolBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        inflate(getContext(), R.layout.toolbar, this);
        mSliderBar = findViewById(R.id.toolbar_slider);
        mFormattingLayout = findViewById(R.id.toolbar_formatting_layout);
        mFormattingLayout.setParent(this);
    }

    public void hideFormatting() {
        hideFormattingInternal(() -> {});
    }

    private void showFormattingInternal(ISerializableView view, Class<?> cls) {
        if (mFormattingLayout.getCurrentView() == view) return;
        if (!mFormattingLayout.hasViewType(cls) && mFormattingLayout.getVisibility() == VISIBLE) {
            hideFormattingInternal(() -> showFormattingInternal(view, cls));
            return;
        }
        if (mSliderBar.getVisibility() == VISIBLE) {
            hideSliderInternal(() -> { showFormattingInternal(view, cls); });
        }
        mFormattingLayout.bind(view);
        if (mFormattingLayout.getVisibility() != VISIBLE) {
            mFormattingLayout.setVisibility(VISIBLE);
            ViewUtil.animate(mFormattingLayout, R.anim.enter_from_top, () -> {});
        }
    }

    private void hideFormattingInternal(ViewUtil.OnAnimationEndListener listener) {
        mFormattingLayout.unbind();
        mSliderBar.unbind();

        if (mSliderBar.getVisibility() == VISIBLE) {
            hideSliderInternal(this::hideFormatting);
        }
        else if (mFormattingLayout.getVisibility() == VISIBLE) {
            ViewUtil.animate(mFormattingLayout, R.anim.leave_from_top, () -> {
                mFormattingLayout.setVisibility(INVISIBLE);
                listener.onAnimationEnd();
            });
        }
    }

    public ToolBarSlider getSliderBar() {
        return mSliderBar;
    }

    private void hideSliderInternal(ViewUtil.OnAnimationEndListener listener) {
        ViewUtil.animate(mSliderBar, R.anim.leave_from_top, () -> {
            mSliderBar.setVisibility(INVISIBLE);
            listener.onAnimationEnd();
        });
    }

    public void hideSlider() {
        hideSliderInternal(() -> {});
    }

    public void showSlider(Slider.OnChangeListener listener) {
        mSliderBar.getSlider().clearOnChangeListeners();
        mSliderBar.getSlider().addOnChangeListener(listener);
        if (mSliderBar.getVisibility() != VISIBLE) {
            mSliderBar.setVisibility(VISIBLE);
            ViewUtil.animate(mSliderBar, R.anim.enter_from_top, () -> {});
        }
    }

    public boolean isShown() {
        return mFormattingLayout.getVisibility() == VISIBLE;
    }

    public void showFormatting(ISerializableView view) {
        showFormattingInternal(view, view.getClass());
    }
}
