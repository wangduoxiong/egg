package edu.xiyou.andrew.Egg.net;

/*
 * Copyright (c) 2015 Andrew-Wang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.http.HttpHost;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by andrew on 15-5-20.
 */
public class CrawlDatum implements Serializable{
    public static final long UNFETCH_TIME = 0;  //未抓取的时间值

    long fetchTime;         //  最后一次抓取的时间
    Site site;

    public CrawlDatum(Site site) {
        this(site, UNFETCH_TIME);
    }

    public CrawlDatum(Site site, long fetchTime) {
        this.site = site;
        this.fetchTime = fetchTime;
    }

    public long getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(long fetchTime) {
        this.fetchTime = fetchTime;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    static class Site implements Comparable,Serializable{
        private String url;
        private String userAgent;
        private Map<String, String> cookies;

        private Map<String, String> headers;
        private String host;

        private HttpHost httpHost;

        public Site(String url, String userAgent) {
            this(url, userAgent, null, null, null);
        }

        public Site(String url, String userAgent, Map<String, String> cookies, Map<String, String> headers, String host) {
            this.url = url;
            this.userAgent = userAgent;
            this.cookies = cookies;
            this.headers = headers;
            this.host = host;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public Map<String, String> getCookies() {
            return cookies;
        }

        public void setCookies(Map<String, String> cookies) {
            this.cookies = cookies;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public HttpHost getHttpHost() {
            return httpHost;
        }

        public void setHttpHost(HttpHost httpHost) {
            this.httpHost = httpHost;
        }

        @Override
        public int compareTo(Object o) {
            return 0;
        }
    }

}
