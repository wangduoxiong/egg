package edu.xiyou.andrew.egg.model;

import com.google.common.collect.Sets;
import org.apache.http.Header;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 每个爬虫对应一个的Site,一个Site代表爬虫一个站点的
 * 设置。
 * Created by andrew on 16-1-6.
 *
 * @since 0.2.00
 */
public class Site extends BaseModel{
    private String domain;
    private String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/43.0.2357.130 Chrome/43.0.2357.130 Safari/537.36";
    private Map<String, String> cookies;
    private List<Header> headerList;
    private String charset;

    private boolean isCycle = false;                         //是否重复爬取
    private int cycleCount = 0;                                 //重复爬取的次数,-1为无限次爬取，当isCycle为false，此参数无效
    private int cycleInterval = 24 * 60 * 60 * 1000;    //当重复爬取时，每次爬取的间隔时间,单位毫秒

    private int retry = 3;                                          //当爬取失败时，重试次数
    private int retryTime = 3000;                               //当爬取失败时，再爬取时，间隔时间

    private int timeout = 3000;                                 //

    private long fetchCount = 1000;                         //在此站点抓取fetchCount数量个网页，-1不指定数目，直至抓取完毕为止

    private int threadCount = 16;

    private Set<Integer> DEFAULT_ACCEPT_STATUS_CODE = Sets.newHashSet(200);
    private Set<Integer> acceptStatusCode = DEFAULT_ACCEPT_STATUS_CODE;

    private List<CrawlDatum> startDatumList;        //开始爬取的种子

    public Set<Integer> getAcceptStatusCode() {
        return acceptStatusCode;
    }

    public Site setAcceptStatusCode(Set<Integer> acceptStatusCode) {
        this.acceptStatusCode = acceptStatusCode;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public Site setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Site setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
        return this;
    }

    public int getCycleCount() {
        return cycleCount;
    }

    public Site setCycleCount(int cycleCount) {
        this.cycleCount = cycleCount;
        return this;
    }

    public int getCycleInterval() {
        return cycleInterval;
    }

    public Site setCycleInterval(int cycleInterval) {
        this.cycleInterval = cycleInterval;
        return this;
    }


    public String getDomain() {
        return domain;
    }

    public Site setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public long getFetchCount() {
        return fetchCount;
    }

    public Site setFetchCount(long fetchCount) {
        this.fetchCount = fetchCount;
        return this;
    }

    public List<Header> getHeaderList() {
        return headerList;
    }

    public Site setHeaderList(List<Header> headerList) {
        this.headerList = headerList;
        return this;
    }

    public boolean isCycle() {
        return isCycle;
    }

    public Site setIsCycle(boolean isCycle) {
        this.isCycle = isCycle;
        return this;
    }

    public int getRetry() {
        return retry;
    }

    public Site setRetry(int retry) {
        this.retry = retry;
        return this;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public Site setRetryTime(int retryTime) {
        this.retryTime = retryTime;
        return this;
    }

    public List<CrawlDatum> getStartDatumList() {
        return startDatumList;
    }

    public Site setStartDatumList(List<CrawlDatum> startDatumList) {
        this.startDatumList = startDatumList;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Site setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public Site setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public Site setThreadCount(int threadCount) {
        this.threadCount = threadCount;
        return this;
    }
}
