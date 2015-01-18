package edu.xiyou.andrew.Egg.PageProcessor.pageinfo;

import java.util.List;

/**
 * Created by andrew on 15-1-18.
 */
public class HttpResponHeaders implements HttpHeaderMetadata{
    private String contentType;
    private String contentEncoding;
    private List<String> setCookies;
    private String vary;
    private String date;
    private String connection;
    private String cacheControl;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public List<String> getSetCookies() {
        return setCookies;
    }

    public void setSetCookies(List<String> setCookies) {
        this.setCookies = setCookies;
    }

    public String getVary() {
        return vary;
    }

    public void setVary(String vary) {
        this.vary = vary;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }
}
