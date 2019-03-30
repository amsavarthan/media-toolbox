package com.amsavarthan.apps.media_toolbox.WhatsApp.ui.imageslider;


import com.amsavarthan.apps.media_toolbox.WhatsApp.data.local.FileHelper;
import com.amsavarthan.apps.media_toolbox.WhatsApp.data.model.ImageModel;
import com.amsavarthan.apps.media_toolbox.WhatsApp.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;
public class ImageSliderPresenter extends BasePresenter<ImageSliderView> {

    private static final String TAG = ImageSliderPresenter.class.getSimpleName();
    private final FileHelper fileHelper;

    @Inject
    public ImageSliderPresenter(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    void setLoadingAnimation(boolean status) {
        getMvpView().displayLoadingAnimation(status);
    }

    void loadRecentImageSlider(ImageModel imageModel) {

        // Get images
        List<ImageModel> items = fileHelper.getRecentImages();

        // Get position of image to be displayed in the list
        int position = items.indexOf(imageModel);

        if (position != -1) {
            getMvpView().displayImageSlider(items, position);
        }
    }

    void loadSavedImageSlider(ImageModel imageModel) {
        // Get images
        List<ImageModel> items = fileHelper.getSavedImages();

        // Get position of image to be displayed in the list
        int position = items.indexOf(imageModel);

        if (position != -1) {
            getMvpView().displayImageSlider(items, position);
        }
    }

}
