package tn.amin.myhomeui.designer.other;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import tn.amin.myhomeui.designer.previewframe.PreviewFrame;
import tn.amin.myhomeui.storage.StorageManager;
import tn.amin.myhomeui.util.DisplayUtil;
import tn.amin.myhomeui.util.ViewUtil;

public class ViewFreezerOverlay extends androidx.appcompat.widget.AppCompatImageView {
    public ViewFreezerOverlay(@NonNull Context context) {
        super(context);
    }

    public ViewFreezerOverlay(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewFreezerOverlay(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void show() {
        View target = (View) getParent();
        Point screenSize = DisplayUtil.getSize(getContext());
        setImageDrawable(StorageManager.tryLoadPreviewImage());
        setVisibility(VISIBLE);
    }

    public void hide() {
        ViewGroup parent = (ViewGroup)getParent();
        if (parent != null)
            parent.removeView(this);
    }
}
