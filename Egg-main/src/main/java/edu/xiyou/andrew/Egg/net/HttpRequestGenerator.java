package edu.xiyou.andrew.Egg.net;

import edu.xiyou.andrew.Egg.utils.Config;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;


import java.util.Map;

/**
 * Created by duoxiongwang on 15-8-17.
 */
public class HttpRequestGenerator {

    private static PoolingHttpClientConnectionManager connectionManager;

    static   {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(100);
    }

    public HttpRequestGenerator setMaxTotal(int maxTotal){
        connectionManager.setMaxTotal(maxTotal);
        return this;
    }

    public CloseableHttpClient generateClient(CrawlDatum.Site site){
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);

        if (site == null || StringUtils.isBlank(site.getUrl())){
            return httpClientBuilder.build();
        }

        if (StringUtils.isNotBlank(site.getUserAgent()) ){
            httpClientBuilder.setUserAgent(site.getUserAgent());
        }else {
            httpClientBuilder.setUserAgent("");
        }

        SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(false).
                setTcpNoDelay(true).setSoTimeout(Config.TTIME_OUT).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);

        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(Config.RETRY, true));

        generateCookie(httpClientBuilder, site);
        return httpClientBuilder.build();
    }

    private void generateCookie(HttpClientBuilder httpClientBuilder, CrawlDatum.Site site) {
        if (site == null || MapUtils.isEmpty(site.getCookies()) || StringUtils.isBlank(site.getUrl())){
            return;
        }
        Map<String, String> cookies = site.getCookies();
        CookieStore cookieStore = new BasicCookieStore();
        for (Map.Entry<String, String> entry : cookies.entrySet()){
            BasicClientCookie basicCookieStore = new BasicClientCookie(entry.getKey(), entry.getValue());
            basicCookieStore.setDomain(site.getUrl());
            cookieStore.addCookie(basicCookieStore);
        }
        httpClientBuilder.setDefaultCookieStore(cookieStore);
    }
}
