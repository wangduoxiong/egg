package edu.xiyou.andrew.Egg.fetcher;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by duoxiongwang on 15-8-18.
 */
public abstract class FetcherMonitor {
    protected AtomicLong fetchCounted ;
    protected AtomicLong pollCount;

    public FetcherMonitor(){
        fetchCounted = new AtomicLong(0);
        pollCount = new AtomicLong(0);
    }

    public AtomicLong getFetchCounted() {
        return fetchCounted;
    }

    public AtomicLong getPollCount(){
        return pollCount;
    }
}
