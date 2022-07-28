package tn.amin.myhomeui.designer.previewframe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.slider.Slider;

import jp.wasabeef.blurry.Blurry;
import tn.amin.myhomeui.R;
import tn.amin.myhomeui.storage.StorageManager;

public class PreviewFrameContainer extends ConstraintLayout {
    public PreviewFrame previewFrame;
    public PreviewFrameBackground background;
    private TextView overlay;

    public PreviewFrameContainer(@NonNull Context context) {
        super(context);
        initView();
    }

    public PreviewFrameContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PreviewFrameContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public PreviewFrameContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void initView() {
        inflate(getContext(), R.layout.previewframe_container, this);

        Slider slider = findViewById(R.id.designer_preview_slider);
        previewFrame = findViewById(R.id.designer_preview);
        background = findViewById(R.id.designer_preview_background);
        overlay = findViewById(R.id.designer_preview_overlay);
        slider.addOnChangeListener((s, value, fromUser) -> {
            previewFrame.scale(value / 100f);
        });
    }

    public void loadWallpaper() {
        BitmapDrawable wallpaper = (BitmapDrawable) StorageManager.tryLoadWallpaper();
        previewFrame.setWallpaper(wallpaper);

        if (wallpaper != null) {
            overlay.setVisibility(INVISIBLE);
            Bitmap wallpaperBitmap = wallpaper.getBitmap();
            Blurry.with(getContext())
                    .from(wallpaperBitmap)
                    .into(background);
        } else {
            overlay.setVisibility(VISIBLE);
        }
    }
}
