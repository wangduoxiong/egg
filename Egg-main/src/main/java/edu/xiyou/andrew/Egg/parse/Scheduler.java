package edu.xiyou.andrew.Egg.parse;

import edu.xiyou.andrew.Egg.net.CrawlDatum;

/**
 * 存入队列的链接和取出链接的接口
 * Created by andrew on 15-2-2.
 */
public interface Scheduler {
    /**
     * 将一个任务存入队列
     * @param datum
     */
    public void push(CrawlDatum datum);

    /**
     * 取出一个任务
     * @return
     */
    public CrawlDatum pull();
}
