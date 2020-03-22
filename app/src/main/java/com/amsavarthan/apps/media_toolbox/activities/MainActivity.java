package com.amsavarthan.apps.media_toolbox.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.amsavarthan.apps.media_toolbox.BuildConfig;
import com.amsavarthan.apps.media_toolbox.R;
import com.amsavarthan.apps.media_toolbox.adapters.MainCardsAdapter;
import com.amsavarthan.apps.media_toolbox.models.Card;
import com.amsavarthan.apps.media_toolbox.utils.NotificationUtils;
import com.amsavarthan.apps.media_toolbox.whatsapp.home.HomeActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    TextView version;
    RecyclerView recyclerView;
    MainCardsAdapter cardsAdapter;
    List<Card> cardList=new ArrayList<>();
    private int num;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action=intent.getAction();
        String type=intent.getType();
        if(Intent.ACTION_SEND.equals(action) && type!=null){
            if(type.equals("text/plain")){
                String intentData=intent.getStringExtra(Intent.EXTRA_TEXT);
                if(intentData.contains("://youtu.be/")||intentData.contains("youtube.com/watch?v=")){

                    if(num<=0) {

                        startActivity(new Intent(this, VideoDownloadActivity.class).putExtra("url",intentData));
                        finish();

                    }else{

                        Toast.makeText(this, "You can't download Youtube videos now, You may have to check our github repo for details from about screen", Toast.LENGTH_LONG).show();

                    }

                }else if(intentData.contains("://www.instagram.com/")){

                    startActivity(new Intent(this, RepostActivity.class).putExtra("url",intentData));
                    finish();

                }
            }
        }

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
        setContentView(R.layout.activity_main);

        sharedPreferences=getSharedPreferences("easter_egg",MODE_PRIVATE);
        num=sharedPreferences.getInt("clicks",10);

        if(num>0){
            sharedPreferences.edit().putInt("clicks",10).apply();
        }

        String action=getIntent().getAction();
        String type=getIntent().getType();
        if(Intent.ACTION_SEND.equals(action) && type!=null){
            if(type.equals("text/plain")){
                String intentData=getIntent().getStringExtra(Intent.EXTRA_TEXT);
                if(intentData.contains("://youtu.be/")||intentData.contains("youtube.com/watch?v=")){

                    if(num<=0) {

                        startActivity(new Intent(this, VideoDownloadActivity.class).putExtra("url",intentData));
                        finish();

                    }else{

                        Toast.makeText(this, "You can't download Youtube videos now, You may have to check our github repo for more details", Toast.LENGTH_LONG).show();

                    }

                }else if(intentData.contains("://www.instagram.com/")){

                    startActivity(new Intent(this, RepostActivity.class).putExtra("url",intentData));
                    finish();

                }
            }
        }

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted()){
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                getSharedPreferences("directory",MODE_PRIVATE).edit().putString("path",getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()).apply();
                            }else{
                                String directory=getSharedPreferences("directory",MODE_PRIVATE).getString("path",Environment.getExternalStorageDirectory()+"/Media Toolbox/");
                                File storageDir=new File(directory);
                                boolean success=true;
                                if(!storageDir.exists()){
                                    success=storageDir.mkdirs();
                                }

                            }*/
                            String directory=getSharedPreferences("directory",MODE_PRIVATE).getString("path",Environment.getExternalStorageDirectory()+"/Media Toolbox/");
                            File storageDir=new File(directory);
                            boolean success=true;
                            if(!storageDir.exists()){
                                success=storageDir.mkdirs();
                            }
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();

        version = findViewById(R.id.version);
        recyclerView=findViewById(R.id.recyclerView);

        cardsAdapter=new MainCardsAdapter(cardList, new MainCardsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Card cardItem) {

                switch (cardItem.getText()){
                    case "Repost":
                        startActivity(new Intent(MainActivity.this,RepostActivity.class));
                        return;
                    case "HD Profile Picture":
                        startActivity(new Intent(MainActivity.this,HDPictureActivity.class));
                        return;
                    case "Settings":
                        startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                        return;
                    case "Status Saver":
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        return;
                    case "Video Downloader":
                        startActivity(new Intent(MainActivity.this, VideoDownloadActivity.class));
                    }

            }
        });

        version.setText(String.format("v%s", BuildConfig.VERSION_NAME));

        addCards();
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        if(cardList.size()%2!=0) {
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return position == cardList.size()-1 ? 2 : 1;
                }
            });
        }
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardsAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.createNotificationChannels(this);
        }

    }

    private void addCards() {

        cardList.clear();

        Card card=new Card("Repost",R.mipmap.repost,R.mipmap.instagram,R.color.instaAccent);
        cardList.add(card);

        card=new Card("HD Profile Picture",R.mipmap.crop,R.mipmap.instagram,R.color.instaAccent);
        cardList.add(card);

        card=new Card("Status Saver",R.mipmap.save_status,R.mipmap.whatsapp,R.color.whatsappAccent);
        cardList.add(card);

        if(num<=0){
            card=new Card("Video Downloader",R.mipmap.download_video,R.mipmap.youtube,R.color.youtubeAccent);
            cardList.add(card);
        }

        card=new Card("Settings",R.mipmap.settings,R.mipmap.settings,R.color.commonToolAccent);
        cardList.add(card);
        
        cardsAdapter.notifyDataSetChanged();
    }

    public void unlockYoutube(View view) {

        Animation animation=AnimationUtils.loadAnimation(this,R.anim.shake);
        findViewById(R.id.top).startAnimation(animation);

        if(num==0){
            sharedPreferences.edit().putInt("clicks",--num).apply();
            Toast.makeText(this, "Something's unlocked", Toast.LENGTH_SHORT).show();
            addCards();
            return;
        }
        sharedPreferences.edit().putInt("clicks",--num).apply();


    }

    public void openGithub(View view) {

        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/lvamsavarthan/Media-Toolbox"));
        startActivity(intent);

    }
}
