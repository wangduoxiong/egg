package edu.xiyou.andrew.egg.parser;

import edu.xiyou.andrew.egg.net.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by andrew on 16-1-20.
 */
public class DefaultHandler extends AbstractHandler implements Handler{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHandler.class);

    public DefaultHandler(RegexRule regexRule){
        super(regexRule);
    }

    @Override
    public void onSuccess(Page page) {
        Map<String, String> map = page.getResults();
        map.put("url", page.getUrl());
    }

    @Override
    public void onFail(Page page) {
        LOGGER.warn(page.getRequest().toString() + "fetch error");
    }

}
