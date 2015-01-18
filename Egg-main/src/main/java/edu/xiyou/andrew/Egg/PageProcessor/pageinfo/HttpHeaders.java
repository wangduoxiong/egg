package edu.xiyou.andrew.Egg.PageProcessor.pageinfo;

/**
 * Http header 信息
 * Created by andrew on 15-1-18.
 */
public class HttpHeaders {

    public static final String COOKIE = "Cookie";

    public static final String CONNECTION = "Connection";

    public static final String HOST = "Host";

    public static final String USER_AGENT = "User-Agent";

    public static final String REFERER = "Referer";

    public static final String CACHE_CONTROL = "Cache-Control";

    private String cookie;
    private String userAgent;
    private String connection;
    private String host;
    private String cacheControl;

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }
}
