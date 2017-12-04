package com.example.mymusicplayer.po;

/**
 * Created by Chasen on 2017/4/9.
 * song类
 */

public class Song {
    /**
     * 歌名
     */
    private String title;
    /**
     * 歌手
     */
    private String artist;
    /**
     * 歌曲路径
     */
    private String path;
    /**
     *歌曲长度
     */
    private int duration;

    /**
     *获取歌名
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置歌名
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取歌手名
     * @return artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * 设置歌手名
     * @param artist
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }
    /**
     * 获取歌曲路径
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置歌曲路径
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取歌曲时长
     * @return duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     *设置歌曲时长
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }
}