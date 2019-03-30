package com.amsavarthan.apps.media_toolbox;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class About extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_about);
    }

    public void openPlayStore(View view) {

        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://play.google.com/store/apps/dev?id=8738176098315595821"));
        startActivity(i);

    }

    public void openWebsite(View view) {

        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://lvamsavarthan.github.io/lvstore"));
        startActivity(i);

    }

    public void openInstagram(View view) {
        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://www.instagram.com/lvamsavarthan"));
        startActivity(i);
    }

    public void openGithub(View view) {
        Intent i=new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://github.com/lvamsavarthan"));
        startActivity(i);
    }

    public void openEmail(View view) {

        //Send email
        Intent email=new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL,new String[]{"amsavarthan.a@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT,"Sent from Media Downloader v1.2.0 ("+ Build.BRAND+", "+Build.VERSION.SDK_INT+")");
        email.putExtra(Intent.EXTRA_TEXT,"");
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email,"Send mail using..."));

    }

}
