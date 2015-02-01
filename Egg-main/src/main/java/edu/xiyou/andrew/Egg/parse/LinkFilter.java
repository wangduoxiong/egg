package edu.xiyou.andrew.Egg.parse;

import edu.xiyou.andrew.Egg.utils.RegexRule;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by andrew on 15-2-1.
 */
public class LinkFilter implements Parse{
    public static List<String> getAllLinksByA(Document document){
        List<String> list = new LinkedList<String>();
        Elements elements = document.select("a[href]");
        String url = null;
        for (Element element : elements){
            url = element.attr("abs:href");
            if (url.length() > 7){
                list.add(url);
            }
        }
        return list;
    }

    public static List<String> getLinksByA(Document document, String cssSelector){
        List<String> list = new LinkedList<String>();
        Elements elements = document.select("a[href]").select(cssSelector);
        String url = null;
        for (Element element : elements){
            url = element.attr("abs:href");
            if (url.length() > 7){
                list.add(url);
            }
        }
        return list;
    }

    public static List<String> getLinksByA(Document document, RegexRule regexRule){
        List<String> list = new LinkedList<String>();
        Elements elements = document.select("a[href]");
        String url = null;
        for (Element element : elements){
            url = element.attr("abs:href");
            if ((url.length() > 7) && (regexRule.satisfy(url))){
                list.add(url);
            }
        }
        return list;
    }

    public static List<String> getLinksByA(Document document, String cssSelector, RegexRule regexRule){
        List<String> list = new LinkedList<String>();
        Elements elements = document.select("a[href]").select(cssSelector);
        String url = null;
        for (Element element : elements){
            url = element.attr("abs:href");
            if ((url.length() > 7) && regexRule.satisfy(url)){
                list.add(url);
            }
        }
        return list;
    }

    public static List<String> getAllLinksByImg(Document document){
        List<String> list = new LinkedList<String>();
        Elements elements = document.select("img[src]");
        String url = null;
        for (Element element : elements){
            url = element.attr("abs:src");
            if (url.length() > 7){
                list.add(url);
            }
        }
        return list;
    }

    public static List<String> getLinksByImg(Document document, RegexRule regexRule){
        List<String> list = new LinkedList<String>();
        Elements elements = document.select("img[src]");
        String url = null;

        for (Element element : elements){
            url = element.attr("abs:src");
            if ((url.length() > 7) && regexRule.satisfy(url)){
                list.add(url);
            }
        }
        return list;
    }

    public static List<String> getLinksByCss(Document document){
        List<String> links = new LinkedList<String>();
        Elements elements = document.select("link[href]");

        for (Element element : elements){
            String url = element.attr("abs:href");
            if (url.length() > 7){
                links.add(url);
            }
        }
        return links;
    }

    public static List<String> getLinksByJs(Document document) {
        List<String> links = new LinkedList<String>();
        Elements elements = document.select("script[src]");

        for (Element element : elements) {
            String url = element.attr("abs:src");
            if (url.length() > 6){
                links.add(url);
            }
        }
        return links;
    }
}
