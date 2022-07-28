package tn.amin.myhomeui.serializer.factory.callback;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;

import tn.amin.myhomeui.R;
import tn.amin.myhomeui.serializer.factory.container.DynamicViewContainer;

public class WidgetCallbackMap extends ViewCallbackMap<AppWidgetHostView> {
    public WidgetCallbackMap(AppWidgetHostView child, DynamicViewContainer<AppWidgetHostView> parent, String type) {
        super(child, parent, type);


    }
}
