package edu.xiyou.andrew.Egg.PageProcessor.pageinfo;


import edu.xiyou.andrew.Egg.utils.CharsetDetector;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrew on 15-1-18.
 */
public class HttpResponse implements Response{
    private Map<String, List<String>> headers;     //response返回的头部信息
    private HttpResponseContent html;    //response返回的网页内容
    private int statusCode;                 //repsonse返回状态
    private long fetchTime;                 //抓取的时间戳
    private String url;                     //url

    public HttpResponse(String url) {
        this.url = url;
    }

    /**
     * 返回Response头部
     * @return HttpResponseHeaders
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * 设置Response头部
     * @param headers
     */
    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public String getCharset(){
        return html.getCharset();
    }

    /**
     * 返回
     * @return httpResponseContent
     */
    public byte[] getContent() {
        return html.getContent();
    }

    public void setContent(byte[] content) throws UnsupportedEncodingException {
        String charset;
        charset = CharsetDetector.guessEncoding(content);
        html = new HttpResponseContent();
        html.setContent(new String(content, charset).getBytes());
        html.setCharset(charset);
        html.setUrl(url);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public long getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(long fetchTime) {
        this.fetchTime = fetchTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType(){
        String contentType = null;
        List<String> contentTypeList = headers.get("Content-Type");

        if (contentType == null){
            return null;
        }
        contentType = contentTypeList.get(0);
        return  contentType;
    }

    public List<String> getHeader(String key){
        if (headers == null){
            return null;
        }

        return headers.get(key);
    }

    public void setHeader(String key, List<String>value){
        if (headers == null){
            headers = new HashMap<String, List<String>>();
        }
        headers.put(key, value);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "\nheaders=" + headers +
                ",\n statusCode=" + statusCode +
                ",\n fetchTime=" + fetchTime +
                ", \nurl='" + url + '\'' +
                ", \nhtml=" + new String(html.getContent()) +
                '}';
    }
}
