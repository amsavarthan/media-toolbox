package com.amsavarthan.apps.media_toolbox.Instagram;

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
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amsavarthan.apps.media_toolbox.View.HifyImageView;
import com.amsavarthan.apps.media_toolbox.R;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Post_Model> post;
    ArrayList<Long> list = new ArrayList<>();

    public BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {

            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            list.remove(referenceId);
            if (list.isEmpty()) {
                NotificationManager notificationManager = (NotificationManager) ctxt.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setupChannels(notificationManager);
                }
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        context, "com.amsavarthan.media_toolbox");

                android.app.Notification notification;
                notification = mBuilder
                        .setAutoCancel(true)
                        .setContentTitle("Instagram post")
                        .setSmallIcon(R.drawable.ic_file_download_24dp)
                        .setContentText("Download successful")
                        .build();

                notificationManager.notify(0, notification);
                Toast.makeText(ctxt, "Downloaded successfully", Toast.LENGTH_LONG).show();
            }
        }

    };
    private DownloadManager downloadManager;
    private long refid;
    private Activity activity;

    private void downloadImage(final String ImageURI,final ViewHolder holder) {

        new MaterialDialog.Builder(context)
                .title("Download "+holder.choice)
                .content("Do you want to download this "+holder.choice+"?")
                .positiveText("YES")
                .negativeText("NO")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        String fileName,extension;
                        if(holder.choice.equals("Image")) {
                            fileName="IMG_" + System.currentTimeMillis();
                            extension=".jpg";
                        }else{
                            fileName="VDO_" + System.currentTimeMillis();
                            extension=".mp4";
                        }

                        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show();
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ImageURI));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                        request.setAllowedOverRoaming(true);
                        request.setTitle(fileName);
                        request.setDescription("Downloading "+holder.choice+"...");
                        request.setVisibleInDownloadsUi(true);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/MediaDownloader/Instagram Posts/" + fileName+extension);

                        refid = downloadManager.enqueue(request);
                        list.add(refid);


                    }
                }).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        }).show();

    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        try {
            if (post.get(holder.getAdapterPosition()).getIs_video().equals("true")) {
                if (holder.videoView.isPlaying()) {
                    holder.videoView.stopPlayback();
                }
                holder.videoView.setVisibility(View.GONE);
                holder.video_holder.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.VISIBLE);
                holder.bottom.setVisibility(View.VISIBLE);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void save(final String url, final ViewHolder holder) {

        Dexter.withActivity(activity)
                .withPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(isOnline()) {
                            downloadImage(url,holder);
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

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public RecyclerAdapter(List<Post_Model> post,Activity activity) {
        this.post=post;
        this.activity=activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        context=parent.getContext();
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapter.ViewHolder holder, int i) {

        if(post.get(i).getDot().equals("no")){
            holder.dot.setVisibility(View.GONE);
        }else{
            holder.dot.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .load(post.get(i).getProfile_pic_url())
                .centerCrop()
                .into(holder.circleImageView);

        holder.username.setText(String.format("@%s", post.get(i).getUsername()));
        holder.fullname.setText(post.get(i).getFull_name());

        if(post.get(i).getIs_video().equals("false")) {
            holder.video_holder.setVisibility(View.GONE);

            Glide.with(context)
                    .load(post.get(i).getDisplay_url())
                    .centerCrop()
                    .into(holder.image);
            holder.choice="Image";
            holder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save(post.get(holder.getAdapterPosition()).getDisplay_url(),holder);
                }
            });

        }else{

            holder.video_holder.setVisibility(View.VISIBLE);
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(post.get(holder.getAdapterPosition()).getVideo_url()), "video/mp4");
                    context.startActivity(intent);

                }
            });

            if(!post.get(i).getThumbnail().equals("multiple")) {

                Glide.with(context)
                        .load(post.get(i).getThumbnail())
                        .centerCrop()
                        .into(holder.image);

            }else{

                Glide.with(context)
                        .load(post.get(i).getDisplay_url())
                        .centerCrop()
                        .into(holder.image);

            }
            holder.choice="Video";
            holder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save(post.get(holder.getAdapterPosition()).getVideo_url(),holder);
                }
            });
        }

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager clipboardManager=(ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData=ClipData.newPlainText("Instagram post caption",post.get(holder.getAdapterPosition()).getCaption());
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, "Caption copied to clipboard \"" + post.get(holder.getAdapterPosition()).getCaption() + "\"", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        return post.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView username,fullname;
        HifyImageView image;
        CircleImageView circleImageView,dot;
        Button share,save;
        String choice;
        FrameLayout video_holder;
        ImageView play;
        VideoView videoView;
        MediaController mediaController;
        LinearLayout bottom;
        CardView card;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.username);
            fullname=itemView.findViewById(R.id.name);
            image=itemView.findViewById(R.id.image);
            share=itemView.findViewById(R.id.share);
            save=itemView.findViewById(R.id.save);
            card=itemView.findViewById(R.id.card);
            video_holder=itemView.findViewById(R.id.video_frame);
            circleImageView=itemView.findViewById(R.id.owner_pic);
            play=itemView.findViewById(R.id.play);
            videoView=itemView.findViewById(R.id.videoview);
            bottom=itemView.findViewById(R.id.bottom);
            dot=itemView.findViewById(R.id.dot);

            context.registerReceiver(onComplete,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            mediaController=new MediaController(context);


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
