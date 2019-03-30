package com.amsavarthan.apps.media_toolbox.Instagram;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amsavarthan.apps.media_toolbox.R;
import com.amsavarthan.apps.media_toolbox.Utils.HttpHandler;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class DP_View extends AppCompatActivity {

    private static final String TAG = "DP_View";
    private ProgressDialog pDialog;
    private String retrieved_profile_link;
    private CircleImageView profile_pic;
    private TextInputEditText editText;
    private static String URL;

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
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_dp__view);

        try {
            getSupportActionBar().setSubtitle(getResources().getString(R.string.sub_title_insta));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }catch (Exception e){
            e.printStackTrace();
        }

        profile_pic=findViewById(R.id.profile_pic);
        editText=findViewById(R.id.username);

    }

    public void getProfilePicture(View view) {

        String username=editText.getText().toString();

        if(!TextUtils.isEmpty(username)){
            URL="https://www.instagram.com/"+username+"/?__a=1";
            Toast.makeText(this, URL, Toast.LENGTH_LONG).show();
            new GetData().execute();
        }else{
            Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show();
        }

    }


    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DP_View.this);
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

                    JSONObject graphql = jsonObj.getJSONObject("graphql");
                    JSONObject user = graphql.getJSONObject("user");

                    retrieved_profile_link= user.getString("profile_pic_url_hd");

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
                                "Couldn't get profile picture, Please check your internet connection!",
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
            if (pDialog.isShowing())
                pDialog.dismiss();

            Glide.with(DP_View.this)
                    .load(retrieved_profile_link)
                    .placeholder(R.mipmap.default_dp)
                    .centerCrop()
                    .crossFade()
                    .into(profile_pic);

            profile_pic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(DP_View.this, "Saved to SD Card", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

    }

}
