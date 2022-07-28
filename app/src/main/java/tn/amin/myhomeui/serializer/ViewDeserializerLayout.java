package tn.amin.myhomeui.serializer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import tn.amin.myhomeui.Constants;
import tn.amin.myhomeui.serializer.factory.ISerializableView;
import tn.amin.myhomeui.serializer.factory.callback.ViewCallbackMap;
import tn.amin.myhomeui.storage.StorageManager;
import tn.amin.myhomeui.util.LogUtil;

public class ViewDeserializerLayout extends FrameLayout {
    public ViewDeserializerLayout(@NonNull Context context) {
        super(context);
        initView();
    }

    public ViewDeserializerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ViewDeserializerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ViewDeserializerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setClipChildren(false);
    }

    public void render(Set<Map<String, Object>> serializedViews) {
        removeViewIf((v) -> v instanceof ISerializableView);
        for (Map<String, Object> attrs: serializedViews) {
            View view;
            try {
                view = ViewSerializer.deserialize(this, attrs, false);
                if (view != null) {
                    view.setTranslationZ(1f);
                    view.setTag(Constants.LOCKSCREEN_VIEW_TAG);
                }
            } catch (JSONException e) {
                LogUtil.error("An error occured while deserializing a view", e);
            }
        }
        StorageManager.onRenderingFinished();
    }

    public void removeViewIf(Predicate<View> ifClause) {
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View view = getChildAt(i);
            if (ifClause.test(view)) {
                removeView(view);
            }
        }
    }
}
