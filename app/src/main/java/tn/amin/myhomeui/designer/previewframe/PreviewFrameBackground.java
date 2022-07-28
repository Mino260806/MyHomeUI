package tn.amin.myhomeui.designer.previewframe;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import tn.amin.myhomeui.util.ViewUtil;


public class PreviewFrameBackground extends AppCompatImageView {
    public PreviewFrameBackground(@NonNull Context context) {
        super(context);
        initView();
    }

    public PreviewFrameBackground(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PreviewFrameBackground(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
    }
}
