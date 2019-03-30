package com.amsavarthan.apps.media_toolbox.WhatsApp.ui.main.recentscreen;

import com.amsavarthan.apps.media_toolbox.WhatsApp.data.local.FileHelper;
import com.amsavarthan.apps.media_toolbox.WhatsApp.data.model.ImageModel;
import com.amsavarthan.apps.media_toolbox.WhatsApp.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

import rx.functions.Action1;


public class RecentPicsPresenter extends BasePresenter<RecentPicsView> {

    @Inject
    public FileHelper fileHelper;

    @Inject
    public RecentPicsPresenter(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    void setLoadingAnimation(boolean status) {
        getMvpView().displayLoadingAnimation(status);
    }

    void loadRecentImages() {
        List<ImageModel> mediaItems = fileHelper.getRecentImages();
        if (!mediaItems.isEmpty()) {
            getMvpView().displayRecentImages(mediaItems);
        }else{
            getMvpView().displayNoImagesInfo();
        }

        fileHelper.getMediaStateObservable().subscribe(new Action1<ImageModel>() {
            @Override
            public void call(ImageModel imageModel) {
                List<ImageModel> mediaItems = fileHelper.getRecentImages();
                if (!mediaItems.isEmpty()) {
                    getMvpView().displayRecentImages(mediaItems);
                }else{
                    getMvpView().displayNoImagesInfo();
                }
            }
        });
    }

    void saveMedia(ImageModel imageModel) {
        boolean status = fileHelper.saveMediaToLocalDir(imageModel);
        getMvpView().displayImageSavedMsg();
    }

    void loadImageViewer(ImageModel imageModel, int position) {
        getMvpView().displayImage(position, imageModel);
    }

    void deleteLocalImage(ImageModel imageModel) {
        boolean status = fileHelper.deleteImageFromLocalDir(imageModel);
        if (status) {
            getMvpView().displayDeleteSuccessMsg();
        }
    }


    public void confirmDeleteAction(ImageModel imageModel, int itemPosition) {
        getMvpView().displayDeleteConfirmPrompt(imageModel, itemPosition);
    }
}
