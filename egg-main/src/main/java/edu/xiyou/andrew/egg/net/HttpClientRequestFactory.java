package edu.xiyou.andrew.egg.net;

import edu.xiyou.andrew.egg.model.CrawlDatum;
import edu.xiyou.andrew.egg.model.Site;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 由httpclient产生具体的请求
 * Created by andrew on 16-1-20.
 */
public class HttpClientRequestFactory implements RequestFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientRequestFactory.class);

    private static final PoolingHttpClientConnectionManager connectionManager;

    static {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setDefaultMaxPerRoute(100);
    }

    public HttpClientRequestFactory() {
        super();
    }

    @Override
    public synchronized Request createRequest(CrawlDatum datum, Site site) {
        LOGGER.info("create a request");
        return new HttpClientRequest(getClient(site));
    }

    private CloseableHttpClient getClient(Site site) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
        if ((site != null) && StringUtils.isNotBlank(site.getUserAgent())) {
            httpClientBuilder.setUserAgent(site.getUserAgent());
        } else {
            httpClientBuilder.setUserAgent("eggSpider");
        }

        if ((site != null) && CollectionUtils.isNotEmpty(site.getHeaderList())) {
            httpClientBuilder.setDefaultHeaders(site.getHeaderList());
        }
        httpClientBuilder.setDefaultSocketConfig(SocketConfig.custom().setSoKeepAlive(true).build());
        if (site != null) {
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(site.getRetry(), true));
        }
        if (site != null){
            httpClientBuilder.setRedirectStrategy(new RedirectStrategy() {
                @Override
                public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                    //待完善
                    return false;
                }

                @Override
                public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
                    return null;
                }
            });
        }

        if ((site != null) && MapUtils.isNotEmpty(site.getCookies())) {
            this.setCookie(httpClientBuilder, site);
        }
        return httpClientBuilder.build();
    }

    private void setCookie(HttpClientBuilder httpClientBuilder, Site site) {
        CookieStore cookieStore = new BasicCookieStore();
        for (Map.Entry<String, String> entry : site.getCookies().entrySet()) {
            BasicClientCookie cookie = new BasicClientCookie(entry.getKey(), entry.getValue());
            cookie.setDomain(site.getDomain());
            cookieStore.addCookie(cookie);
        }
        httpClientBuilder.setDefaultCookieStore(cookieStore);
    }

    @Override
    public RequestFactory setThread(int total) {
        connectionManager.setMaxTotal(total);
        return this;
    }
}
