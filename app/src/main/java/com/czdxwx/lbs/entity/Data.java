package com.czdxwx.lbs.entity;

import net.tsz.afinal.annotation.sqlite.Table;

@Table(name="data")
public class Data {
    private int id;
    private String tweetName;
    private String tweetText;
    private String content;
    private String date;

    public Data(String tweetName, String tweetText, String content, String date) {
        this.tweetName = tweetName;
        this.tweetText = tweetText;
        this.content = content;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Data() {
    }

    public String getTweetName() {
        return tweetName;
    }

    public void setTweetName(String tweetName) {
        this.tweetName = tweetName;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
