package edu.xiyou.andrew.Egg.net;

/**
 * Created by andrew on 15-1-31.
 */
public class CrawlDatum {
    /**
     * 未爬取的标识
     */
    public static final byte CRAWLDATUM_UNFETH = 0x01;

    /**
     * 已爬取的标识
     */
    public static final byte CRAWLDATUM_FETHED = 0x02;

    /**
     *  未爬取的时间标识
     */
    public static final byte TIME_UNFETCH = 0x00;

    /**
     * 是否爬取的标识
     */
    protected byte status;

    /**
     * 最后爬取的时间
     */
    protected long fetchTime;

    /**
     * url
     */
    protected String url;

    public CrawlDatum(String url, byte status, byte fetchTime) {
        this.status = status;
        this.fetchTime = fetchTime;
        this.url = url;
    }

    public CrawlDatum(String url){
        this(url, CRAWLDATUM_UNFETH, TIME_UNFETCH);
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
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
}
