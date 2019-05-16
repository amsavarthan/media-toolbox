package com.amsavarthan.apps.media_toolbox.Youtube;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.apps.media_toolbox.R;
import java.util.ArrayList;
import java.util.List;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.amsavarthan.apps.media_toolbox.Youtube.VideosDatabase.VIDEO_COLUMN_THUMBNAIL_MAX_URL;
import static com.amsavarthan.apps.media_toolbox.Youtube.VideosDatabase.VIDEO_COLUMN_THUMBNAIL_URL;
import static com.amsavarthan.apps.media_toolbox.Youtube.VideosDatabase.VIDEO_COLUMN_TITLE;
import static com.amsavarthan.apps.media_toolbox.Youtube.VideosDatabase.VIDEO_COLUMN_URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private EditText url_text;
    private ImageView proceed;
    private ProgressDialog pDialog;
    private List<Video_Model> videos;
    private RecyclerAdapter mAdapter;
    private String youtubeLink;
    private VideosDatabase database;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_youtube);

        ButterKnife.bind(this);

        try {
            getSupportActionBar().setSubtitle(getResources().getString(R.string.sub_title_youtube));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        videos = new ArrayList<>();
        mAdapter = new RecyclerAdapter(videos,this);
        proceed = findViewById(R.id.proceed);
        url_text = findViewById(R.id.url);
        mRecyclerView = findViewById(R.id.recyclerview);
        database = new VideosDatabase(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        getDatafromDatabase();

        String intentData=getIntent().getStringExtra("url");
        if(intentData!=null){
            youtubeLink=intentData;
            if(youtubeLink.contains("://youtu.be/")||youtubeLink.contains("youtube.com/watch?v=")){
                getYoutubeDownloadUrl(youtubeLink);
            }else {
                Toast.makeText(MainActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
            }
        }
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtubeLink=url_text.getText().toString();
                if(youtubeLink.contains("://youtu.be/")||youtubeLink.contains("youtube.com/watch?v=")){
                    getYoutubeDownloadUrl(youtubeLink);
                }else {
                    Toast.makeText(MainActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    private void getYoutubeDownloadUrl(final String youtubeLink) {

        new YouTubeExtractor(this){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // Showing progress dialog
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();

            }
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {

                if (pDialog.isShowing())
                    pDialog.dismiss();

                url_text.setText("");

                if(ytFiles==null){
                    videos.clear();
                    mAdapter.notifyDataSetChanged();
                    getDatafromDatabase();
                    Toast.makeText(MainActivity.this, "Error: No video found", Toast.LENGTH_SHORT).show();
                    return;
                }

                findViewById(R.id.default_view).setVisibility(View.GONE);
                Video_Model video = new Video_Model("yes",youtubeLink, videoMeta.getMaxResImageUrl(), videoMeta.getHqImageUrl(),videoMeta.getTitle());
                videos.add(0,video);
                database.insertVideo(youtubeLink,videoMeta.getHqImageUrl(),videoMeta.getTitle(),videoMeta.getMaxResImageUrl());
                mAdapter.notifyDataSetChanged();

            }
        }.extract(youtubeLink, true, false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.help_menu_white,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.action_clear:
                clearAllRecents();

        }
        return super.onOptionsItemSelected(item);
    }

    private void clearAllRecents() {


        new MaterialDialog.Builder(this)
                .title("Clear Recents")
                .icon(getResources().getDrawable(R.drawable.ic_recents_24dp))
                .content("Are you sure do you want to clear all?")
                .cancelable(false)
                .positiveColor(getResources().getColor(R.color.black))
                .neutralColor(getResources().getColor(R.color.black))
                .positiveText("Yes")
                .neutralText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        database.deleteAll();
                        videos.clear();
                        mAdapter.notifyDataSetChanged();
                        getDatafromDatabase();
                        Toast.makeText(MainActivity.this, "Recents Cleared", Toast.LENGTH_SHORT).show();

                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void getDatafromDatabase() {

        if(database.getCount()==0){

            findViewById(R.id.default_view).setVisibility(View.INVISIBLE);
            findViewById(R.id.default_view).setAlpha(0.0f);
            findViewById(R.id.default_view).animate().alpha(1.0f).setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            findViewById(R.id.default_view).setVisibility(View.VISIBLE);
                        }
                    });


        }else {

            for (int i = 1; i <= database.getCount(); i++) {

                Cursor cursor = database.getData(i);
                cursor.moveToFirst();

                Video_Model video = new Video_Model("no",
                        cursor.getString(cursor.getColumnIndex(VIDEO_COLUMN_URL)),
                        cursor.getString(cursor.getColumnIndex(VIDEO_COLUMN_THUMBNAIL_MAX_URL)),
                        cursor.getString(cursor.getColumnIndex(VIDEO_COLUMN_THUMBNAIL_URL)),
                        cursor.getString(cursor.getColumnIndex(VIDEO_COLUMN_TITLE))
                        );


                if (!cursor.isClosed()) {
                    cursor.close();
                }

                videos.add(0, video);
                mAdapter.notifyDataSetChanged();

            }
        }

    }


}
