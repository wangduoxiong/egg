package edu.xiyou.andrew.Egg.cralwer;

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

import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.Egg.net.HttpRequest;
import edu.xiyou.andrew.Egg.net.HttpResponse;
import edu.xiyou.andrew.Egg.net.Response;
import edu.xiyou.andrew.Egg.parse.Handler;
import edu.xiyou.andrew.Egg.parse.LinkFilter;
import edu.xiyou.andrew.Egg.utils.RegexRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew on 15-2-4.
 */
public class CrawlerDemo implements Handler{
    private RegexRule regexRule;
    private LinkFilter linkFilter;
    private Handler handler = this;

    public RegexRule getRegexRule() {
        return regexRule;
    }

    public void setRegexRule(RegexRule regexRule) {
        this.regexRule = regexRule;
    }

    public LinkFilter getLinkFilter() {
        return linkFilter;
    }

    public void setLinkFilter(LinkFilter linkFilter) {
        this.linkFilter = linkFilter;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onFailed(Response response) {

    }

    @Override
    public void onSuccess(Response response) {
        System.out.println("url:" + ((HttpResponse)response).getDatum().getUrl() +
                " ,statusCode" + response.getStatusLine().getStatusCode());
    }

    @Override
    public List<CrawlDatum> handleAndGetLinks(Response response) {
        return null;
    }

    public static void main(String[] args) {
        CrawlerDemo demo = new CrawlerDemo();
        List<String> seeds = new ArrayList<String>(4);
        seeds.add("http://www.csdn.net");
        seeds.add("http://www.baidu.com");
        seeds.add("http://www.2345.com");
        seeds.add("http://www.sina.com");
        Crawler crawler = new Crawler("/home/andrew/EggTest/");
        crawler.setHandler(demo.getHandler());
        crawler.setRequest(new HttpRequest());
        crawler.setDepth(1);
        crawler.setSeeds(seeds);
        crawler.start();
    }
}
