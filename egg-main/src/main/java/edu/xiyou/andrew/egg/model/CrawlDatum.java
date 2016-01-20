package edu.xiyou.andrew.egg.model;

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

import com.google.common.collect.Maps;
import edu.xiyou.andrew.egg.utils.HttpMeta;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 *  一个请求的基本单位，
 * Created by andrew on 15-5-20.
 */
public class CrawlDatum extends BaseModel implements Cloneable{
    private String url;                                                         //链接
    private String method;                                                  //连接的方式
    private Map<String, Object> extra;                                  //存放请求的额外信息
    private static String CYCLE_TIMES = "CYCLE_TIMES";      //存在 extra中，重复爬取的次数
    private static String PROPERTY = "PROPERTY";               //优先级
    private boolean storeHtml = true;                               //是否将网页源码保存

    public CrawlDatum(String url, String method, Map<String, Object> extra, boolean storeHtml) {
        this.url = url;
        this.method = method;
        this.extra = extra;
        this.storeHtml = storeHtml;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
       this.extra = extra;
    }

    public CrawlDatum addExtra(String key, Object value){
        if (MapUtils.isEmpty(extra)){
            extra = Maps.newHashMap();
        }
        if (StringUtils.isNotBlank(key)){
            extra.put(key, value);
        }
        return this;
    }

    public boolean isStoreHtml() {
        return storeHtml;
    }

    public void setStoreHtml(boolean storeHtml) {
        this.storeHtml = storeHtml;
    }

    @Override
    public CrawlDatum clone() throws CloneNotSupportedException {
        return (CrawlDatum)super.clone();
    }
    public static CrawlDatum.Builder custom(){
        return new Builder();
    }

    public static class Builder{
        private String url;
        private String method;
        private Map<String, Object> extra;
        private boolean storeHtml;

        {
            method = HttpMeta.Method.GET;
            extra = Maps.newHashMap();
            storeHtml = true;
        }

        public String getUrl() {
            return url;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getMethod() {
            return method;
        }

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public Map<String, Object> getExtra() {
            return extra;
        }

        public Builder setExtra(Map<String, Object> extra) {
            if (MapUtils.isEmpty((this.extra))){
                extra = Maps.newHashMap();
            }
            this.extra.putAll(extra);
            return this;
        }

        public boolean isStoreHtml() {
            return storeHtml;
        }

        public Builder setStoreHtml(boolean storeHtml) {
            this.storeHtml = storeHtml;
            return this;
        }

        public CrawlDatum build(){
            return new CrawlDatum(url, method, extra, storeHtml);
        }
    }
}
