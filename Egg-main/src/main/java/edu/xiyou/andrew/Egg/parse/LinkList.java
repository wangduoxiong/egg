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

import edu.xiyou.andrew.Egg.utils.RegexRule;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;

/**
 * 从每个网页的Document对象中筛选出所需的链接
 * Created by andrew on 15-3-29.
 */
public class LinkList extends LinkedList<String>{
    public void getLinkByA(Document document, RegexRule regexRule){
        Elements elements = document.select("a[href]");
        for (Element element : elements){
            String url = element.attr("abs:href");
            if ((url.length() > 6) && (regexRule.satisfy(url))){
                add(url);
            }
        }
    }

    public void getLinkByA(Document document, String cssQuery){
        Elements elements = document.select("a[href]");

        if (cssQuery != null && cssQuery.length() > 0){
            elements  = document.select(cssQuery);
        }

        for (Element element : elements){
            String url = element.attr("abs:href");
            if (url.length() > 6) {
                add(url);
            }
        }
    }

    public void getLinkByA(Document document, String cssQuery, RegexRule regexRule){
        Elements elements = document.select("a[href]").select(cssQuery);
        for (Element element : elements){
            String url = element.attr("abs:href");
            if ((url.length() > 6) && (regexRule.satisfy(url))){
                add(url);
            }
        }
    }

    public void getLinkByImg(Document document, String cssQuery){
        Elements elements = document.select("img[src]");

        if (cssQuery != null && cssQuery.length() > 0){
            elements  = document.select(cssQuery);
        }

        for (Element element : elements){
            String url = element.attr("abs:src");
            if (url.length() > 6) {
                add(url);
            }
        }
    }

//    public static void main(String[] args) throws IOException {
//        Document document = Jsoup.connect("http://www.baidu.com").get();
//        System.out.println(document);
//        LinkList list = new LinkList();
//        list.getLinkByA(document, "");
//        System.out.println(list.size());
//    }
}
