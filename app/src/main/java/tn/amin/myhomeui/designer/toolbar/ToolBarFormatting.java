package tn.amin.myhomeui.designer.toolbar;

import static eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener.BUTTON_POSITIVE;

import android.content.Context;
import android.util.AttributeSet;

import java.io.File;
import java.util.Objects;

import eltos.simpledialogfragment.color.SimpleColorWheelDialog;
import tn.amin.myhomeui.R;
import tn.amin.myhomeui.designer.DesignerActivity;
import tn.amin.myhomeui.designer.draggable.DraggableImageViewContainer;
import tn.amin.myhomeui.designer.draggable.DraggableTextViewContainer;
import tn.amin.myhomeui.designer.fontpicker.FontPickerDialog;
import tn.amin.myhomeui.serializer.factory.ISerializableView;
import tn.amin.myhomeui.util.DimensionUtil;
import tn.amin.myhomeui.util.PrimitiveUtil;

public class ToolBarFormatting extends BaseToolBarFormatting {
    public ToolBarFormatting(Context context) {
        super(context);
    }
    public ToolBarFormatting(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToolBarFormatting(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ToolBarFormatting(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public Class<?> getBoundViewType() {
        return getCurrentView() == null? null : getCurrentView().getClass();
    }

    public boolean hasViewType(Class<?> cls) {
        return Objects.equals(getBoundViewType(), cls);
    }

    @Override
    public void bind(ISerializableView view) {
        if (!isButtonsRelatedToViewType(view.getClass())) {
            configureButtons(view.getClass());
        }
        super.bind(view);
    }

    private void configureButtons(Class<?> viewType) {
        mLayout.removeAllViews();
        mToggleButtons.clear();
        addSliderButton(R.drawable.ic_toolbar_rotate, (slider, value, fromUser) -> {
            if (getCurrentView() == null) return;
            if (fromUser) getCurrentView().set("rotation", value);
        }, () -> PrimitiveUtil.unboxFloat(getCurrentView().get("rotation")), 0, 360);
        addSliderButton(R.drawable.ic_toolbar_alpha, (slider, value, fromUser) -> {
            if (getCurrentView() == null) return;
            if (fromUser) getCurrentView().set("alpha", value / 100f);
        }, () -> PrimitiveUtil.unboxFloat(getCurrentView().get("alpha")) * 100, 0, 100);
        addSeparator();

        if (Objects.equals(viewType, DraggableTextViewContainer.class)) {
            addToggleButton(R.drawable.ic_toolbar_bold, (b, selected) -> {
                if (getCurrentView() == null) return;
                getCurrentView().set("bold", selected);
            }, () -> getCurrentView().get("bold"));
            addToggleButton(R.drawable.ic_toolbar_italic, (b, selected) -> {
                getCurrentView().set("italic", selected);
            }, () -> getCurrentView().get("italic"));
            addToggleButton(R.drawable.ic_toolbar_underline, (b, selected) -> {
                if (getCurrentView() == null) return;
                getCurrentView().set("underline", selected);
            }, () -> getCurrentView().get("underline"));
            addToggleButton(R.drawable.ic_toolbar_strikethrough, (b, selected) -> {
                if (getCurrentView() == null) return;
                getCurrentView().set("strikethrough", selected);
            }, () -> getCurrentView().get("strikethrough"));
            addToggleButton(R.drawable.ic_toolbar_align_start, (b, selected) -> {
                if (getCurrentView() == null) return;
                getCurrentView().set("textAlignment", TEXT_ALIGNMENT_VIEW_START);
            }, () -> getCurrentView().get("textAlignment", TEXT_ALIGNMENT_VIEW_START) == TEXT_ALIGNMENT_VIEW_START, 0);
            addToggleButton(R.drawable.ic_toolbar_align_center, (b, selected) -> {
                if (getCurrentView() == null) return;
                getCurrentView().set("textAlignment", TEXT_ALIGNMENT_CENTER);
            }, () -> getCurrentView().get("textAlignment", TEXT_ALIGNMENT_VIEW_START) == TEXT_ALIGNMENT_CENTER, 0);
            addToggleButton(R.drawable.ic_toolbar_align_end, (b, selected) -> {
                if (getCurrentView() == null) return;
                getCurrentView().set("textAlignment", TEXT_ALIGNMENT_VIEW_END);
            }, () -> getCurrentView().get("textAlignment", TEXT_ALIGNMENT_VIEW_START) == TEXT_ALIGNMENT_VIEW_END, 0);

            addSliderButton(R.drawable.ic_toolbar_textsize, (slider, value, fromUser) -> {
                if (getCurrentView() == null) return;
                if (fromUser)
                    getCurrentView().set("textSize", DimensionUtil.spToPx(value, getContext()));
            }, () -> DimensionUtil.pxToSp(PrimitiveUtil.unboxInt(getCurrentView().get("textSize")), getContext()), 10, 150);
            addClickableButton(R.drawable.ic_toolbar_textcolor, (b) -> {
                if (getCurrentView() == null) return;
                DesignerActivity activity = (DesignerActivity) getContext();
                activity.addDialogListener("COLOR_PICKER_TOOLBAR", (tag, which, extras) -> {
                    if (which == BUTTON_POSITIVE) {
                        int color = extras.getInt(SimpleColorWheelDialog.COLOR);
                        getCurrentView().set("textColor", color);
                        return true;
                    }
                    return false;
                });
                SimpleColorWheelDialog.build()
                        .color(0xFFCF4747) // optional initial color
                        .show(activity, "COLOR_PICKER_TOOLBAR");
            });
            addClickableButton(R.drawable.ic_toolbar_fontfamily, (b) -> {
                if (getCurrentView() == null) return;
                DesignerActivity activity = (DesignerActivity) getContext();
                String path = getCurrentView().get("fontfamily");
                File file;
                if (path != null)
                    file = new File(path);
                else
                    file = null;
                new FontPickerDialog()
                        .setFontListener((typeface, newPath) -> getCurrentView().set("fontfamily", newPath))
                        .setSelectedFont(file)
                        .show(activity.getSupportFragmentManager(), null, activity);
            });
        } else if (Objects.equals(viewType, DraggableImageViewContainer.class)) {
            addSliderButton(R.drawable.ic_toolbar_resize, (slider, value, fromUser) -> {
                if (getCurrentView() == null) return;
                if (fromUser) ((DraggableImageViewContainer) getCurrentView()).setRatio(value / 100f);
            }, () -> ((DraggableImageViewContainer) getCurrentView()).getRatio() * 100f, 10, 500);
        }

        mButtonsViewType = viewType;
    }
    private Class<?> mButtonsViewType = null;

    private boolean isButtonsRelatedToViewType(Class<?> type) {
        return Objects.equals(mButtonsViewType, type);
    }
}
