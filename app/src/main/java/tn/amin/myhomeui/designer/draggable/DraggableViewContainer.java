package tn.amin.myhomeui.designer.draggable;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import java.util.ArrayList;
import java.util.Map;

import tn.amin.myhomeui.R;
import tn.amin.myhomeui.serializer.factory.container.DynamicViewContainer;
import tn.amin.myhomeui.storage.StorageManager;
import tn.amin.myhomeui.util.LogUtil;

public class DraggableViewContainer<T extends View> extends DynamicViewContainer<T>
        implements IDraggable, View.OnTouchListener, View.OnFocusChangeListener {
    private View border = null;
    private ImageButton closeButton = null;
    private ImageButton moveButton = null;

    private final ArrayList<OnFocusChangeListener> mOnFocusChangeListeners = new ArrayList<>();

    public DraggableViewContainer(@NonNull Context context) {
        super(context);
        initView();
    }

    public DraggableViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DraggableViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.draggable_view, this);

        closeButton = findViewById(R.id.draggable_view_component_close_button);
        moveButton = findViewById(R.id.draggable_view_component_move_button);
        closeButton.setOnClickListener((v) -> deleteMySelf());
        border = findViewById(R.id.draggable_view_component_border);

        addOnFocusChangeListener(this);
    }

    protected void deleteMySelf() {
        child.clearFocus();
        ViewGroup parent = (ViewGroup)(DraggableViewContainer.this.getParent());
        parent.removeView(DraggableViewContainer.this);
        StorageManager.disposeOfView(this);
    }

    @Override
    public void addChild() {
        super.addChild();

        removeView(child);
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ViewGroup childParent = findViewById(R.id.draggable_view_component_main);
        childParent.addView(child, params);

        child.setClickable(true);
        child.setFocusable(true);
        child.setFocusableInTouchMode(true);
        child.setOnTouchListener(this);
        child.setOnFocusChangeListener((v, hasFocus) -> {
            for (OnFocusChangeListener listener: mOnFocusChangeListeners) {
                listener.onFocusChange(v, hasFocus);
            }
        });
    }

    public ImageButton getMoveButton() {
        return moveButton;
    }

    private static float margin = -1;
    public float getMargin() {
        if (margin == -1)
            margin = getResources().getDimension(R.dimen.draggable_view_margin);
        return margin;
    }

    public void addOnFocusChangeListener(OnFocusChangeListener listener) {
        mOnFocusChangeListeners.add(listener);
    }

    public boolean isChildLocked() {
        return get("locked", false);
    }

    public static DraggableViewContainer<?> getParentForChild(View child) {
        if (child == null) return null;
        if (child.getParent() == null) return null;
        try {
            return (DraggableViewContainer<?>) child.getParent().getParent();
        } catch (ClassCastException ignored) {
            return null;
        }
    }


    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return getChild().requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (!isChildLocked())
                closeButton.setVisibility(GONE);
            border.setVisibility(GONE);
        }
    }

    private float dX, dY;
    private boolean canRequestFocus = false;
    private boolean hasMoved = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Runnable focusCanceller = () -> canRequestFocus = false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!isLocked()) {
                    canRequestFocus = true;
                    hasMoved = false;
                    mHandler.postDelayed(focusCanceller, 500);

                    dX = getX() - event.getRawX();
                    dY = getY() - event.getRawY();
                }


                if (!isChildLocked())
                    closeButton.setVisibility(VISIBLE);
                border.setVisibility(VISIBLE);
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isLocked()) {
                    hasMoved = true;
                    float x = event.getRawX() + dX;
                    float y = event.getRawY() + dY;
                    animate()
                            .x(x)
                            .y(y)
                            .setDuration(0)
                            .withEndAction(() -> {
                                set("x", x + getMargin(), false);
                                set("y", y + getMargin(), false);
                            })
                            .start();
                }
                break;

            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(focusCanceller);
                if (!hasMoved || canRequestFocus) {
                    canRequestFocus = false;
                    float ex, ey;
                    if (v == this) { ex = event.getX() - getMargin(); ey = event.getY() - getMargin(); }
                    else { ex = event.getX(); ey = event.getY(); }
                    child.setOnTouchListener(null);
                    child.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, ex, ey, 0));
                    mHandler.postDelayed(() -> {
                        child.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, ex, ey, 0));
                        child.setOnTouchListener(this);
                    }, 100);
                } else {
                    onFocusChange(this, false);
                }
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean isLocked() {
        return false;
    }
}
