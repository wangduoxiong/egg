package edu.xiyou.andrew.Egg.fetcher;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by duoxiongwang on 15-8-18.
 */
public abstract class FetcherMonitor {
    protected AtomicLong fetchCounted ;

    public FetcherMonitor(){
        fetchCounted = new AtomicLong(0);
    }

    public AtomicLong getFetchCounted() {
        return fetchCounted;
    }

}
