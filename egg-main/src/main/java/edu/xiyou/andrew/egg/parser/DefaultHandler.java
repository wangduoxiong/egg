package edu.xiyou.andrew.egg.parser;

import edu.xiyou.andrew.egg.dataprocessor.ConsoleProcesser;
import edu.xiyou.andrew.egg.net.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by andrew on 16-1-20.
 */
public class DefaultHandler implements Handler{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHandler.class);
    private RegexRule regexRule;

    public DefaultHandler(RegexRule regexRule){
        this.regexRule = regexRule;
    }

    @Override
    public void onSuccess(Page page) {
        ConsoleProcesser consoleProcesser = new ConsoleProcesser();
        consoleProcesser.process(page);
    }

    @Override
    public void onFail(Page page) {
        LOGGER.warn(page.getRequest().toString() + "fetch error");
    }

    @Override
    public List<String> getNextLinks(Page page) {
        return page.getHtmlParser().regexLinks(regexRule).all();
    }

}
