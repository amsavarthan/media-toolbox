package com.amsavarthan.apps.media_toolbox.WhatsApp.injection.component;


import com.amsavarthan.apps.media_toolbox.WhatsApp.data.local.FileHelper;
import com.amsavarthan.apps.media_toolbox.WhatsApp.injection.module.AppModule;
import com.amsavarthan.apps.media_toolbox.WhatsApp.ui.imageslider.ImageSliderActivity;
import com.amsavarthan.apps.media_toolbox.WhatsApp.ui.imageslider.imagedetails.ImageDetailsFragment;
import com.amsavarthan.apps.media_toolbox.WhatsApp.ui.main.MainActivity;
import com.amsavarthan.apps.media_toolbox.WhatsApp.ui.main.recentscreen.RecentPicsFragment;
import com.amsavarthan.apps.media_toolbox.WhatsApp.ui.main.saved.SavedPicsFragment;


import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(RecentPicsFragment fragment);
    void inject(SavedPicsFragment fragment);
    void inject(ImageSliderActivity activity);
    void inject(ImageDetailsFragment fragment);
    FileHelper fileHelper();
}
