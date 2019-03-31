package com.amsavarthan.apps.media_toolbox;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.apps.media_toolbox.Instagram.DP_View;
import com.amsavarthan.apps.media_toolbox.WhatsApp.util.DialogFactory;
import com.amsavarthan.apps.media_toolbox.WhatsApp.util.PermissionUtil;

import java.io.File;

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
                    .setCancelable(false)
                    .setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            requestPermission();
                        }
                    })
                    .setNegativeButton("Never show again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           sharedPref.edit().putBoolean("showDialog",false).apply();
                           dialog.dismiss();
                           requestPermission();
                        }
                    })
                    .show();

        }
    }

    private static final int PERMISSION_REQUEST_CODE_EXT_STORAGE = 10;
    private void requestPermission() {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionUtil.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE_EXT_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE_EXT_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/MediaDownloader");
                if (!f.exists()) {
                    f.mkdirs();
                }

            }else{
                // Permission denied, show rational
                if (PermissionUtil.shouldShowRational(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    DialogFactory
                            .createSimpleOkErrorDialog(this, "Access required", "Permission to access local files is required for the app to perform as intended.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            requestPermission();
                                        }
                                    })
                            .show();
                }else{
                    // Exit maybe?
                }
            }
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
