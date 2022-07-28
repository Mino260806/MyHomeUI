package tn.amin.myhomeui.util;

import android.view.View;
import android.widget.TextView;

import org.intellij.lang.annotations.RegExp;

import java.util.Stack;
import java.util.regex.Pattern;

public class ViewFilter {
    @RegExp
    public static final String CLOCK_FILTER = "\\s*\\d{1,2}[:\\s]+\\d{1,2}\\s*";

    private Stack<ViewFilterConfig> mConfigs = new Stack<>();

    public ViewFilter() {
        add();
    }

    public ViewFilter add() {
        mConfigs.add(new ViewFilterConfig());
        return this;
    }

    public ViewFilter regex(@RegExp String re) {
        mConfigs.peek().pattern = Pattern.compile(re);
        return this;
    }

    public ViewFilter callback(ViewUtil.ViewCallback callback) {
        mConfigs.peek().callback = callback;
        return this;
    }

    public static class ViewFilterConfig {
        Pattern pattern = null;
        ViewUtil.ViewCallback callback = (v) -> true;
    }

    public boolean filter(View view) {
        boolean[] propagate = new boolean[1];
        mConfigs.removeIf((config) -> {
            boolean remove = view instanceof TextView && config.pattern.matcher(((TextView) view).getText()).matches();
            if (remove) {
                propagate[0] = config.callback.onViewFound(view);
            }
            return remove;
        });
        return propagate[0];
    }
}
