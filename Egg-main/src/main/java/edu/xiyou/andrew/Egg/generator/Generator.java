package edu.xiyou.andrew.Egg.generator;

import edu.xiyou.andrew.Egg.net.CrawlDatum;

/**
 * 种子生成器的接口
 * Created by andrew on 15-2-1.
 */
public interface Generator {
    /**
     * 生成下一个种子
     * @return
     */
    public CrawlDatum next();
}
