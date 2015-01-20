package edu.xiyou.andrew.Egg.generator;

import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.CrawlDatum;

/**
 * 种子生成器的接口
 * Created by andrew on 15-1-19.
 */
public interface Generator {
    /**
     * 生成种子的接口
     * @return
     */
    public CrawlDatum nextDatum();
}
