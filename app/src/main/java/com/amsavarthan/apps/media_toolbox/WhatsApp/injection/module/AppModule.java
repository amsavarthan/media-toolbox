package com.amsavarthan.apps.media_toolbox.WhatsApp.injection.module;

import android.app.Application;
import android.content.Context;

import com.amsavarthan.apps.media_toolbox.WhatsApp.data.local.FileHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class AppModule {

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    public Application provideApplication() {
        return application;
    }

    @Provides
    public Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    public FileHelper provideFileHelper(Context context) {
        return new FileHelper(context);
    }

}
