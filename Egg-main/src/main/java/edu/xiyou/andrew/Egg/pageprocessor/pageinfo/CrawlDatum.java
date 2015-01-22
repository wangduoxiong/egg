package edu.xiyou.andrew.Egg.pageprocessor.pageinfo;

/**
 * 记录爬取种子的状态
 * Created by andrew on 15-1-19.
 */
public class CrawlDatum {
    private String url;
    private long fetchTime; //爬取时间
    private byte status;    //该种子的状态

    /**
     * 未爬取状态
     */
    public static final byte STATUS_UNFETCH = 1;
    /**
     * 已爬取状态
     */
    public static final byte STATUS_FETCHED = 2;
    /**
     * 已注入
     */
    public static final byte STATUS_INJECTED = 4;

    /**
     * 未爬取
     */
    public static final long TIME_UNFETCH = 0;

    public CrawlDatum(String url, byte status) {
        this.url = url;
        this.status = status;
    }

    public CrawlDatum(String url, long fetchTime, byte status) {
        this.url = url;
        this.fetchTime = fetchTime;
        this.status = status;
    }

    public long getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(long fetchTime) {
        this.fetchTime = fetchTime;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }
}
