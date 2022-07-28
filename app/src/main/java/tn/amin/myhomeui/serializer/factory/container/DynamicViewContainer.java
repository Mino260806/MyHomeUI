package tn.amin.myhomeui.serializer.factory.container;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import tn.amin.myhomeui.serializer.factory.ISingletonLayout;
import tn.amin.myhomeui.serializer.factory.callback.ICallbackView;
import tn.amin.myhomeui.serializer.factory.callback.ViewCallbackMap;
import tn.amin.myhomeui.util.LogUtil;

public class DynamicViewContainer<T extends View> extends ConstraintLayout
        implements ICallbackView, ISingletonLayout<T> {
    protected ViewCallbackMap<T> map = null;
    protected T child = null;
    protected String type = "View";

    private boolean isCorrupted = false;
    private Class<?> mapClass;

    public DynamicViewContainer(@NonNull Context context) {
        super(context);
    }

    public DynamicViewContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DynamicViewContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected final void initView(Class<? extends T> childcls, Class<?> mapcls) {
        mapClass = mapcls;
        try {
            child = childcls.getDeclaredConstructor(Context.class).newInstance(getContext());
            type = childcls.getSimpleName();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            LogUtil.wtf("", e);
        }
        addChild();
    }

    protected void addChild() {
        if (child == null) throw new RuntimeException("child == null");

        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        child.setTag(this);
        addView(child, params);
    }

    @Override
    public <V> void set(String attribute, V value) {
        if (map == null) setupCallbacks();
        set(attribute, value, true);
    }

    @Override
    public <V> void set(String attribute, V value, boolean fireCallback) {
        if (fireCallback) map.putAndCallback(attribute, value);
        else map.put(attribute, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V get(String attribute) {
        if (map == null) setupCallbacks();
        return (V) map.get(attribute);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> V get(String attribute, V defaultValue) {
        if (map == null) setupCallbacks();
        return (V) map.getOrDefault(attribute, defaultValue);
    }

    @Override
    public void setAttributes(Map<String, Object> attributes) {
        if (map == null) setupCallbacks();
        for (Map.Entry<String, Object> attribute: attributes.entrySet()) {
            set(attribute.getKey(), attribute.getValue());
        }
    }

    @Override
    public Map<String, Object> getAttributes() {
        if (map == null) setupCallbacks();
        return map;
    }

    public void setCorrupted(boolean corrupted) {
        isCorrupted = corrupted;
    }

    @Override
    public boolean isCorrupted() {
        return isCorrupted;
    }

    @Override
    public T getChild() {
        return child;
    }

    @SuppressWarnings("unchecked")
    protected void setupCallbacks() {
        if (mapClass == null) throw new RuntimeException("call initView first");
        try {
            map = (ViewCallbackMap<T>) mapClass.getDeclaredConstructors()[0].newInstance(child, this, type);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            LogUtil.wtf("", e);
            System.exit(-1);
        }
    }
}
