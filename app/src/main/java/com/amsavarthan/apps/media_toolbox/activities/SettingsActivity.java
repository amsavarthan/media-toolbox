package com.amsavarthan.apps.media_toolbox.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amsavarthan.apps.media_toolbox.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private TextView directory_txt;
    private String directory;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int theme=getSharedPreferences("theme",MODE_PRIVATE).getInt("mode",2);
        switch (theme){
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        RadioGroup radioGroup=findViewById(R.id.radiogroup);
        directory_txt=findViewById(R.id.directory);

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted()){
                            directory=getSharedPreferences("directory",MODE_PRIVATE).getString("path",Environment.getExternalStorageDirectory()+"/Media Toolbox/");
                            File storageDir=new File(directory);
                            boolean success=true;
                            if(!storageDir.exists()){
                                success=storageDir.mkdirs();
                            }
                            if(success){
                                directory_txt.setText(directory);
                            }
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();

        switch (theme){
            case 0:
                radioGroup.check(R.id.light);
                break;
            case 1:
                radioGroup.check(R.id.dark);
                break;
            case 2:
                radioGroup.check(R.id.auto);

        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.light:
                        getSharedPreferences("theme",MODE_PRIVATE).edit().putInt("mode",0).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case R.id.dark:
                        getSharedPreferences("theme",MODE_PRIVATE).edit().putInt("mode",1).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    case R.id.auto:
                        getSharedPreferences("theme",MODE_PRIVATE).edit().putInt("mode",2).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

                }
            }
        });

    }

    public void openDirectoryChooser(View view) {

        new ChooserDialog(this)
                .withFilter(true, false)
                .withStartFile(directory)
                .withChosenListener((dir, dirFile) -> {
                    onSelect(dir);
                })
                .build()
                .show();
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            new ChooserDialog(this)
                    .withFilter(true, false)
                    .withStartFile(directory)
                    .withChosenListener((dir, dirFile) -> {
                        onSelect(dir);
                    })
                    .build()
                    .show();
        }else {
            Toast.makeText(this,"Due to restrictions in android Q you cannot save to custom directory",Toast.LENGTH_LONG).show();
        }*/

    }

    public void onSelect(String path) {

        Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();
        if(!path.endsWith("/Media Toolbox")) {
            getSharedPreferences("directory", MODE_PRIVATE).edit().putString("path", path + "/Media Toolbox/").apply();
        }else{
            getSharedPreferences("directory", MODE_PRIVATE).edit().putString("path", path ).apply();
        }
        String directory=getSharedPreferences("directory",MODE_PRIVATE).getString("path",Environment.getExternalStorageDirectory()+"/Media Toolbox/");
        File storageDir=new File(directory);
        boolean success=true;
        if(!storageDir.exists()){
            success=storageDir.mkdirs();
        }
        if(success){
            directory_txt.setText(directory);
        }
    }

    public void openGithub(View view) {

        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/lvamsavarthan/Media-Toolbox"));
        startActivity(intent);

    }
}
