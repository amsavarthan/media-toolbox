package com.amsavarthan.apps.media_toolbox.WhatsApp.ui.main;


import com.amsavarthan.apps.media_toolbox.WhatsApp.ui.base.MvpView;

public interface MainView extends MvpView {
    void displayWelcomeMessage(String msg);
    void displayLoadingAnimation(boolean status);
    void displayRecentAndSavedPics();
}
