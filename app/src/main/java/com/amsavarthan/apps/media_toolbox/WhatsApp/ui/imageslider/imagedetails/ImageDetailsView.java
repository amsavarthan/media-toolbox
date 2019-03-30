package com.amsavarthan.apps.media_toolbox.WhatsApp.ui.imageslider.imagedetails;


import com.amsavarthan.apps.media_toolbox.WhatsApp.ui.base.MvpView;

/**
 * Created by shaz on 14/2/17.
 */

public interface ImageDetailsView extends MvpView {
    void displayLoadingAnimation(boolean status);
    void displayImageSavedMsg();
    void displayDeleteSuccessMsg();
}
