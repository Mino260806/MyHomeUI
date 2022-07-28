package tn.amin.myhomeui.designer;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.google.android.material.color.MaterialColors;

import tn.amin.myhomeui.R;

public class TutorialActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(
                "Welcome...",
                "This is the first slide of the example",
                0,
                MaterialColors.getColor(this, com.google.android.material.R.attr.colorSecondary, Color.TRANSPARENT),
                Color.BLACK,
                Color.BLACK
        ));
        addSlide(AppIntroFragment.newInstance(
                "...Let's get started!",
                "This is the last slide, I won't annoy you more :)"
        ));
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        finish();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        finish();
    }
}
