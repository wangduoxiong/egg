package scheduler;

import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.CrawlDatum;

/**
 * Created by andrew on 15-1-30.
 */
public interface Scheduler {
    /**
     *
     * @param datum 存入的任务
     */
    public void push(CrawlDatum datum);

    /**
     *
     * @return 爬取任务
     */
    public CrawlDatum poll();
}
