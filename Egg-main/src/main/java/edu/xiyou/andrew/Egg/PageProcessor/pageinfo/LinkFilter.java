package edu.xiyou.andrew.Egg.PageProcessor.pageinfo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

/**
 * 通过传入网页,过滤出各种链接Link
 * Created by andrew on 15-1-18.
 */
public class LinkFilter {
    public static List<Link> getLinksByA(Document document){
        List<Link> links = new LinkedList<Link>();
        Elements elements = document.select("a[href]");

        for (Element element : elements){
            String anchor = element.text();
            String url = element.attr("abs:href");
            if (url.length() > 6){
                links.add(new Link(anchor, url));
            }
        }
        return links;
    }

    public static List<Link> getLinksByCss(Document document){
        List<Link> links = new LinkedList<Link>();
        Elements elements = document.select("link[href]");

        for (Element element : elements){
            String anchor = element.attr("rel");
            String url = element.attr("abs:href");
            if (url.length() > 6){
                links.add(new Link(anchor, url));
            }
        }
        return links;
    }

    public static List<Link> getLinksByJs(Document document) {
        List<Link> links = new LinkedList<Link>();
        Elements elements = document.select("script[src]");

        for (Element element : elements) {
            String anchor = element.text();
            String url = element.attr("abs:src");
            if (url.length() > 6){
                links.add(new Link(anchor, url));
            }
        }

        return links;
    }


    public static List<Link> getLinksByImg(Document document){
        List<Link> links = new LinkedList<Link>();
        Elements elements = document.select("img[src]");

        for (Element element : elements){
            String anchor = element.attr("alt");
            String url = element.attr("abs:src");
            if (url.length() > 6){
                links.add(new Link(anchor, url));
            }
        }

        return links;
    }

    public static List<Link> getLinks(Document document){
        List<Link> links = new LinkedList<Link>();
        links.addAll(getLinksByA(document));
        links.addAll(getLinksByCss(document));
        links.addAll(getLinksByImg(document));
        links.addAll(getLinksByJs(document));

        return links;
    }
}
