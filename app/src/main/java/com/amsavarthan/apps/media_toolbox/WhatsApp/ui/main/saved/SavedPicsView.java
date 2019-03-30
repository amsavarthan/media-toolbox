package com.amsavarthan.apps.media_toolbox.WhatsApp.ui.main.saved;


import com.amsavarthan.apps.media_toolbox.WhatsApp.data.model.ImageModel;
import com.amsavarthan.apps.media_toolbox.WhatsApp.ui.base.MvpView;

import java.util.List;

public interface SavedPicsView extends MvpView {
    void displayLoadingAnimation(boolean status);
    void displaySavedImages(List<ImageModel> images);
    void displayNoImagesInfo();
    void displayImage(int position, ImageModel imageModel);
    void displayDeleteSuccessMsg();
    void displayDeleteConfirm(List<ImageModel> imageModels);
}
