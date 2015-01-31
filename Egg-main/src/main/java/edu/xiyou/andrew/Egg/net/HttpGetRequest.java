package edu.xiyou.andrew.Egg.net;

import edu.xiyou.andrew.Egg.net.type.Html;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by andrew on 15-1-31.
 */
public class HttpGetRequest extends HttpRequest{

    private static final Logger LOG = LoggerFactory.getLogger(HttpGetRequest.class);
    private CloseableHttpClient client;
    private HttpClientContext context;
    private String cookie;
    private HttpHost proxy;
    private int socketTimeOut = 3000;
    private int connectTimeOut = 3000;
    private HttpClientContext clientContext = new HttpClientContext();


    public HttpGetRequest(CrawlDatum datum) {
        super(datum);
        client = HttpClients.createDefault();
    }

    public CloseableHttpResponse getGetResponse() throws IOException {

        RequestConfig requestConfig = getRequestConfig();

        String url = datum.getUrl();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = client.execute(httpGet, clientContext);
        datum.fetchTime = System.currentTimeMillis();
        return response;

    }

    private RequestConfig getRequestConfig(){
         return RequestConfig.custom().setSocketTimeout(socketTimeOut)
                .setConnectionRequestTimeout(connectTimeOut)
                .setCookieSpec((context != null)?cookie : CookieSpecs.BEST_MATCH)
                .setProxy(proxy).build();
    }

    public static void main(String[] args) throws IOException {
        HttpGetRequest getRequest = new HttpGetRequest(new CrawlDatum("http://www.baidu.com"));
        Html html = new Html(getRequest.getCrawlDatum(), getRequest.getGetResponse());
        System.out.println(new String(html.getContentAsBytes()));
        System.out.println(html.getDatum().getFetchTime());
        System.out.println(html.getCharset());
        System.out.println(html.getDatum().getUrl());
        System.out.println(html.getStatusLine());

    }
}
