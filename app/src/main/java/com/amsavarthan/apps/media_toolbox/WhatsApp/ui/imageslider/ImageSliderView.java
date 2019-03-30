package com.amsavarthan.apps.media_toolbox.WhatsApp.ui.imageslider;
import com.amsavarthan.apps.media_toolbox.WhatsApp.data.model.ImageModel;
import com.amsavarthan.apps.media_toolbox.WhatsApp.ui.base.MvpView;

import java.util.List;


public interface ImageSliderView extends MvpView {
    void displayLoadingAnimation(boolean status);
    void displayImageSlider(List<ImageModel> mediaItems, int imageToDisplayPosition);
}
