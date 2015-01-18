package edu.xiyou.andrew.Egg.PageProcessor.pageinfo;

/**
 * 网页的信息
 * Created by andrew on 15-1-18.
 */
public class Link {
    //链接的锚
    private String anchor;
    //
    private String url;

    public Link(String anchor, String url) {
        this.anchor = anchor;
        this.url = url;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
