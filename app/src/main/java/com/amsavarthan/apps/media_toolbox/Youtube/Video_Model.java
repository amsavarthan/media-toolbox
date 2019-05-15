package com.amsavarthan.apps.media_toolbox.Youtube;

public class Video_Model {

    private String video_url,thumbnail_max_url,thumbnail_url,title,dot;

    public Video_Model(String dot,String video_url, String thumbnail_max_url, String thumbnail_url, String title) {
        this.video_url = video_url;
        this.dot=dot;
        this.thumbnail_max_url = thumbnail_max_url;
        this.thumbnail_url = thumbnail_url;
        this.title = title;
    }


    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail_max_url() {
        return thumbnail_max_url;
    }

    public void setThumbnail_max_url(String thumbnail_max_url) {
        this.thumbnail_max_url = thumbnail_max_url;
    }

    public String getDot() {
        return dot;
    }

    public void setDot(String dot) {
        this.dot = dot;
    }
}
