package tn.amin.myhomeui.designer.previewframe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.json.JSONException;

import java.io.File;
import java.util.Map;

import tn.amin.myhomeui.R;
import tn.amin.myhomeui.serializer.factory.ISerializableView;
import tn.amin.myhomeui.serializer.factory.container.DynamicViewContainer;
import tn.amin.myhomeui.storage.StorageManager;
import tn.amin.myhomeui.designer.draggable.DraggableTextViewContainer;
import tn.amin.myhomeui.designer.draggable.DraggableImageViewContainer;
import tn.amin.myhomeui.designer.draggable.DraggableViewContainer;
import tn.amin.myhomeui.designer.toolbar.ToolBar;
import tn.amin.myhomeui.storage.preference.CustomizationBundle;
import tn.amin.myhomeui.storage.preference.SharedPreferenceManager;
import tn.amin.myhomeui.util.DisplayUtil;
import tn.amin.myhomeui.util.LogUtil;
import tn.amin.myhomeui.serializer.ViewSerializer;
import tn.amin.myhomeui.util.ViewUtil;

public class PreviewFrame extends FrameLayout {
    private float baseRatio = 0.94f;

    private ToolBar mToolBar;
    private Drawable mWallpaper = null;
    private CustomizationBundle loadedBundle;

    private TutorialListener mTutorialListener;

    public PreviewFrame(@NonNull Context context) {
        super(context);
    }

    public PreviewFrame(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PreviewFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView();
    }

    private void initView() {
        setClipChildren(false);
        setClipToPadding(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setOnClickListener((v) -> clearFocus());
        getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus, newFocus) -> {
            if (mWallpaper == null) return;
            DraggableViewContainer<?> oldFocusParent = DraggableViewContainer.getParentForChild(oldFocus);
            DraggableViewContainer<?> newFocusParent = DraggableViewContainer.getParentForChild(newFocus);
            if (newFocusParent != null && !newFocusParent.isLocked()) {
                mToolBar.showFormatting(newFocusParent);
            } else {
                mToolBar.hideFormatting();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child instanceof DraggableViewContainer) {
            child.setTranslationZ(1);
        }
    }

    public void insertText() {
        DraggableTextViewContainer textView = new DraggableTextViewContainer(getContext());
        insertViewInternal(textView);
    }

    public void insertImage(File file) {
        DraggableImageViewContainer imageView = new DraggableImageViewContainer(getContext());
        insertViewInternal(imageView);
        // Order is very important
        imageView.set("image", file);
        imageView.scaleToDefaultSize();
    }

    private void insertViewInternal(DraggableViewContainer<?> view) {
        view.set("x", 200f);
        view.set("y", 200f);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(view, params);
        
        showTutorialForViewIfFirstTime(view);
    }

    private void showTutorialForViewIfFirstTime(DraggableViewContainer<?> view) {
        SharedPreferenceManager pref = SharedPreferenceManager.getInstance();
        if (!pref.designer().knowsHowToCustomizeInsertedItem()) {
            TapTargetView.showFor((Activity) getContext(),
                TapTarget.forView(view, "Inserted Item",
                                "This item is draggable around the lockscreen. " +
                                        "To customize it, click on it to show the toolbar.")
                        .cancelable(false)
                        .tintTarget(false),
                new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView v) {
                        super.onTargetClick(v);
                        view.requestFocus();
                        pref.designer().setKnowsHowToCustomizeInsertedItem(true);
                        mTutorialListener.onFinished();
                    }
                });
        }
    }

    public CustomizationBundle putInBundle() {
        CustomizationBundle bundle = new CustomizationBundle();
        for (int i=0; i < getChildCount(); i++) {
            if (!(getChildAt(i) instanceof DraggableViewContainer))
                continue;
            DraggableViewContainer<?> child = (DraggableViewContainer<?>) getChildAt(i);
            if (!child.isLocked()) {
                Map<String, Object> attrs = ViewSerializer.serialize(child, this);
                bundle.insertedItems.add(attrs);
            } else {
                if (child.getVisibility() != VISIBLE)
                    bundle.hiddenItems.add(child.get("lockscreen_id"));
            }
        }
        return bundle;
    }

    public void setWallpaper(@Nullable Drawable wallpaper) {
        mWallpaper = wallpaper;
    }

    public void initWallpaper() {
        ((PreviewFrameContainer) getParent()).loadWallpaper();
        if (mWallpaper != null) {
            Point screenSize = DisplayUtil.getSize(getContext());

            int newWidth = screenSize.x;
            int newHeight = screenSize.y;

            float availableHeight = getHeight();
            baseRatio *= availableHeight / newHeight;

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) getLayoutParams();
            params.width = newWidth;
            params.height = newHeight;
            setLayoutParams(params);
            setBackground(mWallpaper);
        }
    }

    public void setBundle(CustomizationBundle bundle) {
        this.loadedBundle = bundle;
        removeAllViews();
        ViewUtil.doOnPreDraw(this, (v) -> {
            initWallpaper();
            scale(1f);

            addLockscreenItems();
            addCustomizations();
            return true;
        });
    }

    public void addCustomizations() {
        for (Map<String, Object> attrs: loadedBundle.insertedItems) {
            try {
                ViewSerializer.deserialize(this, attrs, true, () -> {
                    // View corrupted callback
                    SharedPreferenceManager.getInstance().lockscreen().removeInsertedItem(attrs);
                });
            } catch (JSONException e) {
                LogUtil.error("Could not deserialize back into editable state", e);
            }
        }
    }

    private void addLockscreenItems() {
        StorageManager.gatherLockscreenItems(getContext(), (view) -> {
            String lockscreenId = ((ISerializableView) view).get("lockscreen_id", null);
            addView(view, new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            if (loadedBundle.hiddenItems.contains(lockscreenId)) {
                view.setVisibility(INVISIBLE);
            }
        }, true);
    }

    public void setToolBar(ToolBar toolBar) {
        mToolBar = toolBar;
    }

    public void scale(float scale) {
        float ratio = baseRatio * scale;
        setScaleX(ratio);
        setScaleY(ratio);
    }

    public void setTutorialListener(TutorialListener tutorialListener) {
        this.mTutorialListener = tutorialListener;
    }

    public interface TutorialListener {
        void onFinished();
    }
}
