package tn.amin.myhomeui.serializer.factory;

import android.view.View;

import java.util.Map;

public interface ISerializableView {
    <T> void set(String attribute, T value);
    <T> void set(String attribute, T value, boolean fireCallback);
    <T> T get(String attribute);

    <T> T get(String attribute, T defaultValue);

    void setAttributes(Map<String, Object> attributes);
    Map<String, Object> getAttributes();
    boolean isCorrupted();
}
