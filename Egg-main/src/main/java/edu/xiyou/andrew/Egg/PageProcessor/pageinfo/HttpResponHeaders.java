package edu.xiyou.andrew.Egg.PageProcessor.pageinfo;

import java.util.List;
import java.util.Map;

/**
 * Created by andrew on 15-1-18.
 */
public class HttpResponHeaders{
    private Map<String, List<String>> headears;

    public HttpResponHeaders(Map<String, List<String>> headears) {
        this.headears = headears;
    }

    public Map<String, List<String>> getHeadears() {
        return headears;
    }

    public void setHeadears(Map<String, List<String>> headears) {
        this.headears = headears;
    }

    public List<String> getValue(String key){
        return headears.get(key);
    }

    public void setValue(String key, List<String> value){
        if (key == null){
            return;
        }

        headears.put(key, value);
    }
}
