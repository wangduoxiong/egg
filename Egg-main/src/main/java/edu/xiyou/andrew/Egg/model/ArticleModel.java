package edu.xiyou.andrew.Egg.model;

/**
 * Created by duoxiongwang on 15-8-23.
 */
public class ArticleModel extends BaseModel{
    private static final long serialVersionUID = -3146759931449122731L;
    private String title;
    private String author;
    private String date;
    private String content;
    private String classify;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
