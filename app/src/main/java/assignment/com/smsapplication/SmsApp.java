package assignment.com.smsapplication;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import assignment.com.smsapplication.dagger.AppComponent;
import assignment.com.smsapplication.dagger.AppModule;
import assignment.com.smsapplication.dagger.DaggerAppComponent;
import assignment.com.smsapplication.dagger.PresenterModule;
import assignment.com.smsapplication.utils.SmsAPI;

public class SmsApp extends Application {
    public AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initAppComponent().inject(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public AppComponent initAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent
                    .builder().appModule(new AppModule(this))
                    .presenterModule(new PresenterModule())
                    .build();
        }

        return appComponent;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
