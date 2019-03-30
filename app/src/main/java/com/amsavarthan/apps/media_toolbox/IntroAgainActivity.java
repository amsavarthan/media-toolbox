package com.amsavarthan.apps.media_toolbox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

public class IntroAgainActivity extends AppIntro2 {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntro2Fragment.newInstance("Welcome to Media Toolbox","Media Toolbox contains all tools for social media like Instagram and WhatsApp",R.mipmap.large, getResources().getColor(R.color.blue_light)));
        addSlide(AppIntro2Fragment.newInstance("Instagram Posts Downloader","All you need for downloading the post is it's link from the Instagram App",R.mipmap.insta_large, getResources().getColor(R.color.black)));
        addSlide(AppIntro2Fragment.newInstance("WhatsApp Status Saver","Never ever ask for status from your friends any more, We will get it for you ",R.mipmap.whatsapp, getResources().getColor(R.color.green)));
        addSlide(AppIntro2Fragment.newInstance("Stay Tuned","Extra tools will be available in every new releases",R.mipmap.large, getResources().getColor(R.color.blue_light)));
        addSlide(AppIntro2Fragment.newInstance("OpenSource","Media Toolbox is OpenSource. Join with me and improve the user experience",R.mipmap.github, getResources().getColor(R.color.black)));


        showSkipButton(true);

    }

    @Override
    public void onSkipPressed() {
        super.onSkipPressed();
        finish();
    }

    @Override
    public void onDonePressed() {
        super.onDonePressed();
        finish();
    }
}
