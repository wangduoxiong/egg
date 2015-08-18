package edu.xiyou.andrew.Egg.fetcher;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by duoxiongwang on 15-8-18.
 */
public abstract class FetcherMonitor {
    protected AtomicInteger fetchCounted = new AtomicInteger(0);

    public AtomicInteger getFetchCounted() {
        return fetchCounted;
    }

    public void setFetchCounted(AtomicInteger fetchCounted) {
        this.fetchCounted = fetchCounted;
    }
}
