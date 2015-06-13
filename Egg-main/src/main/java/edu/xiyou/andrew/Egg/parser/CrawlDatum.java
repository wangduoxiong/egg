package edu.xiyou.andrew.Egg.parser;

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
 * Created by andrew on 15-5-20.
 */
public class CrawlDatum {
    public static final long UNFETCH_TIME = 0;  //未抓取的时间值

    long fetchTime;         //  最后一次抓取的时间
    String url;

    public CrawlDatum(String url) {
        this(url, UNFETCH_TIME);
    }

    public CrawlDatum(String url, long fetchTime) {
        this.fetchTime = fetchTime;
        this.url = url;
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

    public void setUrl(String url) {
        this.url = url;
    }
}
