package com.czdxwx.lbs.entity;

import net.tsz.afinal.annotation.sqlite.Table;


@Table(name="news")
public class News {
    private int id;
    private String newsName;
    private String newsText;
    private String content;
    private String date;

    public News() {
    }

    public String getNewsName() {
        return newsName;
    }

    public void setNewsName(String newsName) {
        this.newsName = newsName;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public News(int id, String newsName, String newsText, String content, String date) {
        this.id = id;
        this.newsName = newsName;
        this.newsText = newsText;
        this.content = content;
        this.date = date;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setData(String date) {
        this.date = date;
    }

}
