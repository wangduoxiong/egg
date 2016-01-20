package edu.xiyou.andrew.egg.net;

import edu.xiyou.andrew.egg.model.CrawlDatum;
import edu.xiyou.andrew.egg.model.Site;

/**
 * 产生请求的工厂类
 * Created by andrew on 16-1-20.
 */
public interface RequestFactory {
    /**
     * 根据{@link CrawlDatum} 和 {@link Site} 产生具体的请求
     *
     * @param datum see {@link edu.xiyou.andrew.egg.model.CrawlDatum}
     * @param site  see{@link edu.xiyou.andrew.egg.model.Site}
     * @return
     */
    Request createRequest(CrawlDatum datum, Site site);

    RequestFactory setThread(int total);
}
