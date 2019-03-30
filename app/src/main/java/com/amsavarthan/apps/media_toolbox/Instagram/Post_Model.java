package com.amsavarthan.apps.media_toolbox.Instagram;

public class Post_Model {

    private String username;
    private String full_name;
    private String is_video;
    private String display_url;
    private String link;
    private String timestamp;
    private String caption;
    private String video_url;
    private String thumbnail;
    private String profile_pic_url;
    private String dot;

    public Post_Model(String dot,String profile_pic_url, String video_url, String thumbnail, String caption, String username, String full_name, String is_video, String display_url, String link, String timestamp) {
        this.username = username;
        this.full_name = full_name;
        this.is_video = is_video;
        this.display_url = display_url;
        this.link = link;
        this.timestamp = timestamp;
        this.caption=caption;
        this.video_url=video_url;
        this.thumbnail=thumbnail;
        this.profile_pic_url=profile_pic_url;
        this.dot=dot;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getIs_video() {
        return is_video;
    }

    public void setIs_video(String is_video) {
        this.is_video = is_video;
    }

    public String getDisplay_url() {
        return display_url;
    }

    public void setDisplay_url(String display_url) {
        this.display_url = display_url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }

    public String getDot() {
        return dot;
    }

    public void setDot(String dot) {
        this.dot = dot;
    }
}
