package com.amsavarthan.apps.media_toolbox.Youtube;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.apps.media_toolbox.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Video_Model> videos;
    private Activity activity;

    private BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {

            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            list.remove(referenceId);
            if (list.isEmpty()) {
               /* NotificationManager notificationManager = (NotificationManager) ctxt.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setupChannels(notificationManager);
                }
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        context, "com.amsavarthan.media_toolbox");

                android.app.Notification notification;
                notification = mBuilder
                        .setAutoCancel(true)
                        .setContentTitle("Video thumbnail")
                        .setSmallIcon(R.drawable.ic_file_download_24dp)
                        .setContentText("Download successful")
                        .build();

                notificationManager.notify(0, notification);*/
                //Toast.makeText(ctxt, "Downloaded successfully", Toast.LENGTH_LONG).show();
            }
        }

    };

    public RecyclerAdapter(List<Video_Model> video,Activity activity) {
        this.videos=video;
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        context=parent.getContext();
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapter.ViewHolder holder, int i) {

        final Video_Model video=videos.get(i);

        if(video.getDot().equals("no")){
            holder.dot.setVisibility(View.GONE);
        }else{
            holder.dot.setVisibility(View.VISIBLE);
        }

        holder.title.setText(video.getTitle());

        Picasso.get()
                .load(video.getThumbnail_url())
                .error(R.color.colorAccent)
                .into(holder.thumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progress.setVisibility(GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                        holder.progress.setVisibility(GONE);
                        Toast.makeText(context, "Error loading thumbnail", Toast.LENGTH_SHORT).show();
                        Log.e("Image load error",e.getLocalizedMessage());

                    }
                });

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(video.getVideo_url()));
                context.startActivity(i);
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(context)
                        .title("Download")
                        .icon(context.getResources().getDrawable(R.drawable.ic_file_download_24dp))
                        .content("Which you want to download?")
                        .canceledOnTouchOutside(true)
                        .cancelable(true)
                        .positiveText("Video")
                        .negativeText("Thumbnail")
                        .neutralText("Both")
                        .positiveColor(context.getResources().getColor(R.color.black))
                        .neutralColor(context.getResources().getColor(R.color.black))
                        .negativeColor(context.getResources().getColor(R.color.black))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                download(video.getVideo_url(),null,"video");
                            }
                        })
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                download(video.getVideo_url(),video.getThumbnail_max_url(),"both");
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                                download(null,video.getThumbnail_max_url(),"thumbnail");
                            }
                        })
                        .show();

            }
        });

        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager=(ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData=ClipData.newPlainText("Youtube video URL",video.getVideo_url());
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, "URL copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void downloadVideo(String video_url, String thumb_url, boolean withThumb) {

        if(withThumb){

            Toast.makeText(context, "Thumbnail download started..", Toast.LENGTH_SHORT).show();
            downloadThumbnail(thumb_url,"thumbnail");

            context.startActivity(new Intent(context,DownloadActivity.class)
            .putExtra("url",video_url));

        }else{

            context.startActivity(new Intent(context,DownloadActivity.class)
                    .putExtra("url",video_url));

        }

    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private DownloadManager downloadManager;
    private long refid;
    ArrayList<Long> list = new ArrayList<>();

    private void downloadThumbnail(final String url,String type) {

        String fileName="IMG_" + System.currentTimeMillis();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(true);
        request.setTitle(fileName);
        request.setDescription("Downloading "+type+"...");
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/MediaDownloader/Youtube Downloads/"+fileName + ".jpg");
        refid = downloadManager.enqueue(request);
        list.add(refid);

    }

    private void download(final String video_url,final String thumb_url, final String type) {

        Dexter.withActivity(activity)
                .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(isOnline()) {

                            switch (type){

                                case "video":
                                    downloadVideo(video_url,null,false);
                                    return;
                                case "thumbnail":
                                    Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show();
                                    downloadThumbnail(thumb_url,type);
                                    return;
                                case "both":
                                    downloadVideo(video_url,thumb_url,true);

                            }

                        }else{
                            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            DialogOnDeniedPermissionListener.Builder
                                    .withContext(context)
                                    .withTitle("Storage permission")
                                    .withMessage("Storage permission is needed for downloading images.")
                                    .withButtonText(android.R.string.ok)
                                    .build();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

       ImageView thumbnail;
       Button save,copy;
       ImageView play;
       ProgressBar progress;
       TextView title;
       ImageView dot;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail=itemView.findViewById(R.id.thumbnail);
            save=itemView.findViewById(R.id.save);
            copy=itemView.findViewById(R.id.copy);
            play=itemView.findViewById(R.id.play);
            title=itemView.findViewById(R.id.title);
            progress=itemView.findViewById(R.id.progress);
            dot=itemView.findViewById(R.id.dot);

            context.registerReceiver(onComplete,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager) {
        CharSequence adminChannelName = "Downloads";
        String adminChannelDescription = "Used to show the progress of downloads";
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("com.amsavarthan.media_toolbox", adminChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

}
