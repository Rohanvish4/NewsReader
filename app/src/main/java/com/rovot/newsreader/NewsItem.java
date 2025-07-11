package com.rovot.newsreader;

public class NewsItem {
    private String title;
    private String description;
    private String date;
    private String imgLink;
    private String link;

    public NewsItem(String title, String description, String date, String imgLink, String link) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.imgLink = imgLink;
        this.link = link;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    @Override
    public String toString() {
        return "NewsItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", imgLink='" + imgLink + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
