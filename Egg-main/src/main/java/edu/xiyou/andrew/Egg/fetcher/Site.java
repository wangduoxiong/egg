package edu.xiyou.andrew.Egg.fetcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.Egg.net.Request;
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
public class Site {
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

    private long fetchCount = 1000;                         //在此站点抓取fetchCount数量个网页，-1不指定数目，直至抓取完毕为止

    private Set<Integer> DEFAULT_ACCEPT_STATUS_CODE = Sets.newHashSet(200);
    private Set<Integer> acceptStatusCode = DEFAULT_ACCEPT_STATUS_CODE;

    private List<CrawlDatum> startDatumList;        //开始爬取的种子
}
