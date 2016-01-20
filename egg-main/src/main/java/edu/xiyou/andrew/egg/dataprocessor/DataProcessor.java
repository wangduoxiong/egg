package edu.xiyou.andrew.egg.dataprocessor;

import edu.xiyou.andrew.egg.net.Page;

/**
 * 数据处理的接口
 * Created by andrew on 16-1-20.
 */
public interface DataProcessor {
    /**
     * 处理数据的方法
     * @param page
     */
    void process(Page page);
}
