package com.amsavarthan.apps.media_toolbox.WhatsApp.ui.base;

import android.support.v4.app.Fragment;

import com.amsavarthan.apps.media_toolbox.TheApplication;


public class BaseFragment extends Fragment {

    public TheApplication getTheApplication() {
        return ((TheApplication) getActivity().getApplication());
    }

}