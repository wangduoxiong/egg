package edu.xiyou.andrew.Egg.generator;

import edu.xiyou.andrew.Egg.utils.RegexRule;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by andrew on 15-1-26.
 */
public class Links extends ArrayList<String>{
    public void addAllLinks(Document document){
        Elements elements = document.select("a[href]");

        for (Element element : elements){
            String href = element.attr("abs:href");
            if (href.length() > 6)
                this.add(href);
        }
    }

    public void addAllLinks(Document document, String cssSelector){
        Elements elements = document.select(cssSelector).select("a[href]");

        for (Element element : elements){
            String href = element.attr("abs:href");
            if (href.length() > 6){
                this.add(href);
            }
        }
    }

    public void addAllLinks(Document document, RegexRule regexRule){
        Elements elements = document.select("a[href]");

        for (Element element : elements){
            String href = element.attr("abs:href");
            if (regexRule.satisfy(href)){
                this.add(href);
            }
        }
    }

    public void addAllLinks(Document document, String cssSelector, RegexRule regexRule){
        Elements elements = document.select(cssSelector).select("a[href]");

        for (Element element : elements){
            String href = element.attr("abs:href");
            if (regexRule.satisfy(href)){
                this.add(href);
            }
        }
    }
}
