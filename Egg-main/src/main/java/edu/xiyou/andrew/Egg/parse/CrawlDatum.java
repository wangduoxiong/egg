package edu.xiyou.andrew.Egg.parse;

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

/**
 * 爬取任务的结构体
 * Created by andrew on 15-3-28.
 */
public class CrawlDatum {
    private String url;             //任务的url
    private long fetchTime;         //任务最后的爬取时间

    public final static long UNFETCH_TIME = 0;

    public CrawlDatum(String url, long fetchTime) {
        this.url = url;
        this.fetchTime = fetchTime;
    }

    public CrawlDatum(String url){
        this(url, CrawlDatum.UNFETCH_TIME);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(long fetchTime) {
        this.fetchTime = fetchTime;
    }
}
