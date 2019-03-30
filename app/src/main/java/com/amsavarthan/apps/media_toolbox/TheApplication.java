package com.amsavarthan.apps.media_toolbox;

import android.app.Application;

import com.amsavarthan.apps.media_toolbox.WhatsApp.injection.component.AppComponent;
import com.amsavarthan.apps.media_toolbox.WhatsApp.injection.component.DaggerAppComponent;
import com.amsavarthan.apps.media_toolbox.WhatsApp.injection.module.AppModule;


public class TheApplication extends Application {

    AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}