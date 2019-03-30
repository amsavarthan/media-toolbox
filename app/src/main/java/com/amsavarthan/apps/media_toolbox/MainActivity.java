package com.amsavarthan.apps.media_toolbox;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.apps.media_toolbox.Instagram.DP_View;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);
        final SharedPreferences sharedPref=getSharedPreferences("Media Toolbox",MODE_PRIVATE);
        if(sharedPref.getBoolean("firstRun",true)){
            startActivity(new Intent(this,IntroActivity.class));
            finish();
            return;
        }

        if(sharedPref.getBoolean("showDialog",true)){

            new AlertDialog.Builder(this)
                    .setTitle("Important")
                    .setMessage("All the images and videos you download or save are located in you Pictures folder.")
                    .setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Never show again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           sharedPref.edit().putBoolean("showDialog",false).apply();
                           dialog.dismiss();
                        }
                    })
                    .show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about:
                startActivity(new Intent(this,About.class));
                break;
            case R.id.action_intro:
                startActivity(new Intent(this,IntroAgainActivity.class));


        }
        return super.onOptionsItemSelected(item);
    }

    public void openInstagram(View view) {
        startActivity(new Intent(this, com.amsavarthan.apps.media_toolbox.Instagram.MainActivity.class));
    }

    public void openWhatsapp(View view) {
        startActivity(new Intent(this, com.amsavarthan.apps.media_toolbox.WhatsApp.ui.main.MainActivity.class));
    }

    public void openInstagramDP(View view) {
        startActivity(new Intent(this, DP_View.class));
    }
}
