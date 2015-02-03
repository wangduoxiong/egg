/*
 *Copyright (c) 2015 Andrew-Wang.
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package edu.xiyou.andrew.Egg.net;

import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by andrew on 15-1-31.
 */
public class HttpRequest implements Request{
    protected CrawlDatum datum;
    private static final Logger LOG = LoggerFactory.getLogger(HttpRequest.class);
    private CloseableHttpClient client;
    private String cookie;
    private HttpHost proxy;
    private int socketTimeOut = 3000;
    private int connectTimeOut = 3000;
    private int redirect = 3;
    private HttpClientContext clientContext = new HttpClientContext();


    public HttpRequest(CrawlDatum datum) {
        this.datum = datum;
        client = HttpClients.createDefault();
    }

    public HttpRequest() {
    }

    @Override
    public HttpResponse getResponse() throws IOException {
        HttpResponse  result = new HttpResponse(datum);
        RequestConfig requestConfig = getRequestConfig();

        String url = datum.getUrl();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = client.execute(httpGet);
        try {
            int statusCode = response.getStatusLine().getStatusCode();

            for (int i = 0; (i < redirect) && ((statusCode == HttpStatus.SC_MOVED_TEMPORARILY)
                    || (statusCode == HttpStatus.SC_MOVED_PERMANENTLY)
                    || (statusCode == HttpStatus.SC_SEE_OTHER)
                    || (statusCode == HttpStatus.SC_TEMPORARY_REDIRECT)); i++) {
                Header header = response.getFirstHeader("location");
                if (header != null) {
                    String newUrl = header.getValue();
                    if ((newUrl != null) && (newUrl.length() >= 7) && (!newUrl.equals(""))) {
                        HttpGet get = new HttpGet(newUrl);
                        get.setConfig(requestConfig);

                        response = client.execute(get);
                        statusCode = response.getStatusLine().getStatusCode();
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            if (response != null) {
                result.setEntity(response.getEntity());
                result.setHeaders(response.getAllHeaders());
                result.setStatusLine(response.getStatusLine());
                result.setVersion(response.getProtocolVersion());
            }
            datum.setFetchTime(System.currentTimeMillis());
            datum.setStatus(CrawlDatum.CRAWLDATUM_FETHED);
            return result;
        }finally {
            response.close();
        }

    }

    public HttpResponse getPostResponse(List<NameValuePair> nvps) throws IOException{
        // 将要POST的数据封包
//        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//        nvps.add(new BasicNameValuePair("username", "vip"));
//        nvps.add(new BasicNameValuePair("password", "123456"));
        HttpResponse result = new HttpResponse(datum);
        RequestConfig requestConfig = getRequestConfig();
        String url = datum.getUrl();
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        post.setConfig(requestConfig);

        CloseableHttpResponse response = client.execute(post);
        try {
            datum.setFetchTime(System.currentTimeMillis());
            datum.setStatus(CrawlDatum.CRAWLDATUM_FETHED);

            if (response != null) {
                result.setStatusLine(response.getStatusLine());
                result.setHeaders(response.getAllHeaders());
                result.setEntity(response.getEntity());
                result.setVersion(response.getProtocolVersion());
            }
            return result;
        }finally {
            response.close();
        }
    }

    public HttpResponse getPostResponseWithContext(List<NameValuePair>nvps) throws IOException {
        HttpResponse result = new HttpResponse(datum);
        RequestConfig requestConfig = getRequestConfig();
        String url = datum.getUrl();
        HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        post.setConfig(requestConfig);

        CloseableHttpResponse response = client.execute(post, clientContext);
        try {
            datum.setFetchTime(System.currentTimeMillis());
            datum.setStatus(CrawlDatum.CRAWLDATUM_FETHED);

            if (response != null) {
                result.setStatusLine(response.getStatusLine());
                result.setVersion(response.getProtocolVersion());
                result.setHeaders(response.getAllHeaders());
                result.setEntity(response.getEntity());
            }
            return result;
        }finally {
            response.close();
        }
    }

    private RequestConfig getRequestConfig(){
        return RequestConfig.custom().setSocketTimeout(socketTimeOut)
                .setConnectionRequestTimeout(connectTimeOut)
                .setCookieSpec((cookie != null)?cookie : CookieSpecs.BEST_MATCH)
                .setProxy(proxy).build();
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public void setProxy(HttpHost proxy) {
        this.proxy = proxy;
    }

    public int getSocketTimeOut() {
        return socketTimeOut;
    }

    public void setSocketTimeOut(int socketTimeOut) {
        this.socketTimeOut = socketTimeOut;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public int getRedirect() {
        return redirect;
    }

    public void setRedirect(int redirect) {
        this.redirect = redirect;
    }

    public HttpClientContext getClientContext() {
        return clientContext;
    }

    public void setClientContext(HttpClientContext clientContext) {
        this.clientContext = clientContext;
    }

    public CrawlDatum getDatum() {
        return datum;
    }

    public void setDatum(CrawlDatum datum) {
        this.datum = datum;
    }
}
