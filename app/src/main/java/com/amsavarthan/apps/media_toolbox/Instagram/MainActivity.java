package com.amsavarthan.apps.media_toolbox.Instagram;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.apps.media_toolbox.Utils.HttpHandler;
import com.amsavarthan.apps.media_toolbox.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.amsavarthan.apps.media_toolbox.Instagram.PostsDatabase.POST_COLUMN_CAPTION;
import static com.amsavarthan.apps.media_toolbox.Instagram.PostsDatabase.POST_COLUMN_DISPLAY_URL;
import static com.amsavarthan.apps.media_toolbox.Instagram.PostsDatabase.POST_COLUMN_FULL_NAME;
import static com.amsavarthan.apps.media_toolbox.Instagram.PostsDatabase.POST_COLUMN_IS_VIDEO;
import static com.amsavarthan.apps.media_toolbox.Instagram.PostsDatabase.POST_COLUMN_LINK;
import static com.amsavarthan.apps.media_toolbox.Instagram.PostsDatabase.POST_COLUMN_PIC_URL;
import static com.amsavarthan.apps.media_toolbox.Instagram.PostsDatabase.POST_COLUMN_THUMBNAIL;
import static com.amsavarthan.apps.media_toolbox.Instagram.PostsDatabase.POST_COLUMN_TIME;
import static com.amsavarthan.apps.media_toolbox.Instagram.PostsDatabase.POST_COLUMN_USERNAME;
import static com.amsavarthan.apps.media_toolbox.Instagram.PostsDatabase.POST_COLUMN_VDO_URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private static String URL;
    private EditText url_text;
    private ImageView proceed;
    private ProgressDialog pDialog;
    private List<Post_Model> post;
    private RecyclerAdapter mAdapter;
    private static String full_url;
    private PostsDatabase database;

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
        setContentView(R.layout.activity_main_instagram);
        ButterKnife.bind(this);

        try {
            getSupportActionBar().setSubtitle(getResources().getString(R.string.sub_title_insta));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        post=new ArrayList<>();
        mAdapter=new RecyclerAdapter(post,this);
        proceed=findViewById(R.id.proceed);
        url_text=findViewById(R.id.url);
        mRecyclerView=findViewById(R.id.recyclerview);
        database=new PostsDatabase(this);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        getDatafromDatabase();

        String intentData=getIntent().getStringExtra("url");
        if(intentData!=null){
            full_url=intentData;

            int index=full_url.lastIndexOf("/");
            if(index>0){
                full_url=full_url.substring(0,index+1);
            }

            URL=full_url+"?__a=1";
            if(URL.contains("://www.instagram.com/")){
                new GetData().execute();
            }else {
                Toast.makeText(MainActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
            }
        }

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                full_url=url_text.getText().toString();

                int index=full_url.lastIndexOf("/");
                if(index>0){
                    full_url=full_url.substring(0,index+1);
                }

                URL=full_url+"?__a=1";
                if(URL.contains("://www.instagram.com/")){
                    new GetData().execute();
                }else {
                    Toast.makeText(MainActivity.this, "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

                Post_Model post_model = new Post_Model(
                        "no",
                        cursor.getString(cursor.getColumnIndex(POST_COLUMN_PIC_URL)),
                        cursor.getString(cursor.getColumnIndex(POST_COLUMN_VDO_URL)),
                        cursor.getString(cursor.getColumnIndex(POST_COLUMN_THUMBNAIL)),
                        cursor.getString(cursor.getColumnIndex(POST_COLUMN_CAPTION)),
                        cursor.getString(cursor.getColumnIndex(POST_COLUMN_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(POST_COLUMN_FULL_NAME)),
                        cursor.getString(cursor.getColumnIndex(POST_COLUMN_IS_VIDEO)),
                        cursor.getString(cursor.getColumnIndex(POST_COLUMN_DISPLAY_URL)),
                        cursor.getString(cursor.getColumnIndex(POST_COLUMN_LINK)),
                        cursor.getString(cursor.getColumnIndex(POST_COLUMN_TIME))
                );


                if (!cursor.isClosed()) {
                    cursor.close();
                }

                post.add(0, post_model);
                mAdapter.notifyDataSetChanged();

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.help_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_help:
                startActivity(new Intent(this,HelpActivity.class));
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
                        post.clear();
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

    private class GetData extends AsyncTask<Void, Void, Void> {

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
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(URL);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String caption="";
                    String video_url="",thumbnail="";

                    JSONObject graphql = jsonObj.getJSONObject("graphql");
                    JSONObject shortcode_media = graphql.getJSONObject("shortcode_media");
                    String display_url=shortcode_media.getString("display_url");
                    String is_video=shortcode_media.getString("is_video");

                    if(is_video.equals("true")){
                        video_url=shortcode_media.getString("video_url");
                        thumbnail=shortcode_media.getString("thumbnail_src");
                    }

                    JSONObject owner=shortcode_media.getJSONObject("owner");
                    String username=owner.getString("username");
                    String full_name=owner.getString("full_name");
                    String profile_pic_url=owner.getString("profile_pic_url");

                    JSONObject edge_media_to_caption=shortcode_media.getJSONObject("edge_media_to_caption");
                    JSONArray edges_caption=edge_media_to_caption.getJSONArray("edges");
                    if(edges_caption!=null && edges_caption.length()>0) {
                        caption = edges_caption
                                .getJSONObject(0)
                                .getJSONObject("node")
                                .getString("text");
                    }

                    JSONObject edge_web_media_to_related_media=shortcode_media.getJSONObject("edge_web_media_to_related_media");
                    JSONArray edges=edge_web_media_to_related_media.getJSONArray("edges");

                    JSONArray edges1=null;
                    try {
                        JSONObject edge_sidecar_to_children = shortcode_media.getJSONObject("edge_sidecar_to_children");
                        edges1 = edge_sidecar_to_children.getJSONArray("edges");
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //Checks for multiple posts
                    if(edges!=null && edges.length()>0){

                        for (int i = 0; i < edges.length(); i++) {

                            JSONObject node=edges.getJSONObject(i).getJSONObject("node");

                            String edge_display_url=node.getString("display_url");
                            String edge_is_video=node.getString("is_video");

                            String edge_video_url="",edge_thumbnail="multiple";
                            if(edge_is_video.equals("true")){
                                edge_video_url=node.getString("video_url");
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.default_view).setVisibility(View.GONE);
                                }
                            });
                            Post_Model post_model = new Post_Model("yes",profile_pic_url,edge_video_url, edge_thumbnail, caption, username, full_name, edge_is_video, edge_display_url, URL, String.valueOf(System.currentTimeMillis()));
                            post.add(0,post_model);
                            database.insertContact(username,full_name,edge_display_url,edge_is_video,URL,profile_pic_url,edge_video_url,String.valueOf(System.currentTimeMillis()),edge_thumbnail,caption);

                        }

                    }else if(edges1!=null && edges1.length()>0) {

                        for (int i = 0; i < edges1.length(); i++) {

                            JSONObject node=edges1.getJSONObject(i).getJSONObject("node");

                            String edge_display_url=node.getString("display_url");
                            String edge_is_video=node.getString("is_video");

                            String edge_video_url="",edge_thumbnail="multiple";
                            if(edge_is_video.equals("true")){
                                edge_video_url=node.getString("video_url");
                            }

                            Post_Model post_model = new Post_Model("yes",profile_pic_url,edge_video_url, edge_thumbnail, caption, username, full_name, edge_is_video, edge_display_url, URL, String.valueOf(System.currentTimeMillis()));
                            post.add(0,post_model);
                            database.insertContact(username,full_name,edge_display_url,edge_is_video,URL,profile_pic_url,edge_video_url,String.valueOf(System.currentTimeMillis()),edge_thumbnail,caption);

                        }

                    }else{

                        Post_Model post_model=new Post_Model("yes",profile_pic_url,video_url,thumbnail,caption,username,full_name,is_video,display_url,URL,String.valueOf(System.currentTimeMillis()));
                        post.add(0,post_model);
                        database.insertContact(username,full_name,display_url,is_video,URL,profile_pic_url,video_url,String.valueOf(System.currentTimeMillis()),thumbnail,caption);

                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get post, maybe the account is private.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            findViewById(R.id.default_view).setVisibility(View.GONE);
            if (pDialog.isShowing())
                pDialog.dismiss();

            url_text.setText("");
            mAdapter.notifyDataSetChanged();

        }

    }
}


