package tn.amin.myhomeui.lockscreen.features;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Rotation;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import tn.amin.myhomeui.Constants;
import tn.amin.myhomeui.MyHomeHook;

public class KonfettiFeature extends BaseFeature {
    private FrameLayout panelView;
    private KonfettiView konfettiView;
    private Party party;

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            if (konfettiView.getParent() != null) {
                ((ViewGroup) konfettiView.getParent()).removeView(konfettiView);
            }
            return;
        }
        konfettiView.start(party);
    }

    @Override
    public String getPrefName() {
        return "pref_konfetti";
    }

    @Override
    protected boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public void init() {
        panelView = MyHomeHook.getInstance().panelView;
        konfettiView = new KonfettiView(panelView.getContext());
        konfettiView.setTag(Constants.LOCKSCREEN_VIEW_TAG);
        party = new Party(
                0,
                360,
                0f,
                30f,
                0.9f,
                Arrays.asList(Size.Companion.getSMALL(), Size.Companion.getMEDIUM(), Size.Companion.getLARGE()),
                Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE),
                2000,
                true,
                new Position.Relative(0.5, 0.3),
                0,
                new Rotation(),
                new Emitter(100, TimeUnit.MILLISECONDS).max(100)
        );
        konfettiView.setTranslationZ(2f);
        panelView.addView(konfettiView);
    }
}
