package edu.xiyou.andrew.egg.dataprocessor;

import edu.xiyou.andrew.egg.net.Page;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by andrew on 16-1-20.
 */
public class ConsoleProcesser implements DataProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleProcesser.class);

    @Override
    public void process(Page page) {
        if ((page != null) && MapUtils.isNotEmpty(page.getResults())) {
            return;
        }
        Map<String, String> resultMap = page.getResults();
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
