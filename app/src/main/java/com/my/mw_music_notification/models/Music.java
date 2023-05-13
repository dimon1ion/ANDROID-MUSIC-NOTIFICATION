package com.my.mw_music_notification.models;

public class Music {
    private String title;
    private String singer;
    private int imageId;
    private boolean isFavorite;

    public Music(String title, String singer, int imageId) {
        this.title = title;
        this.singer = singer;
        this.imageId = imageId;
        this.isFavorite = false;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
