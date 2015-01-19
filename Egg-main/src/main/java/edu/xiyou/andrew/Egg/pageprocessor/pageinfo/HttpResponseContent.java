package edu.xiyou.andrew.Egg.pageprocessor.pageinfo;

/**
 * Created by andrew on 15-1-18.
 */
public class HttpResponseContent {
    private String charset;
    private byte[] content;
    private String url;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
