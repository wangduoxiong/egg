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

    public CrawlDatum(String url, byte status, long fetchTime) {
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

    @Override
    public String toString() {
        return "CrawlDatum{" +
                "status=" + status +
                ", fetchTime=" + fetchTime +
                ", url='" + url + '\'' +
                '}';
    }
}
