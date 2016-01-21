package edu.xiyou.andrew.egg.parser;

import edu.xiyou.andrew.egg.net.Page;

import java.util.List;

/**
 * Created by andrew on 16-1-21.
 */
public abstract class AbstractHandler implements Handler{
    private RegexRule regexRule;

    public AbstractHandler(RegexRule regexRule){
        this.regexRule = regexRule;
    }

    @Override
    public List<String> getNextLinks(Page page) {
        return page.getHtmlParser().regexLinks(regexRule).all();
    }
}
