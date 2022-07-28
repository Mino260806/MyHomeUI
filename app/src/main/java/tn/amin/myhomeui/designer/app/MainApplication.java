package tn.amin.myhomeui.designer.app;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.DialogConfigurationBuilder;
import org.acra.config.MailSenderConfigurationBuilder;
import org.acra.data.StringFormat;

import tn.amin.myhomeui.BuildConfig;
import tn.amin.myhomeui.Constants;
import tn.amin.myhomeui.R;

public class MainApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this, new CoreConfigurationBuilder()
                .withBuildConfigClass(BuildConfig.class)
                .withReportFormat(StringFormat.JSON)
                .withPluginConfigurations(
                        new DialogConfigurationBuilder()
                                .withText("An unexpected crash has occurred, please press ok to send a report to the developer")
                                .withResTheme(R.style.Theme_MyHomeUi)
                                .withCommentPrompt("You can add a comment here")
                                .withEnabled(true)
                                .build(),
                        new MailSenderConfigurationBuilder()
                                .withMailTo(Constants.DEV_MAIL)
                                .withReportAsFile(true)
                                .withReportFileName("crash.txt")
                                .withSubject("Crash report " + BuildConfig.APPLICATION_ID)
                                .build()
                )
        );
    }
}
