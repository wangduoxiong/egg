package edu.xiyou.andrew.egg.scheduler;

import com.google.common.collect.Queues;
import edu.xiyou.andrew.egg.model.CrawlDatum;
import edu.xiyou.andrew.egg.scheduler.filter.Filter;
import edu.xiyou.andrew.egg.scheduler.filter.HashSetFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.annotation.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrew on 15-12-16.
 */
@ThreadSafe
public class HashSetScheduler extends SchedulerMonitor implements Scheduler{
    private final static Logger LOG = LoggerFactory.getLogger(HashSetScheduler.class);

    private BlockingQueue<CrawlDatum> queue = Queues.newLinkedBlockingQueue();
    private Filter<String> hashSetFilter = new HashSetFilter<String>();

    @Override
    public synchronized CrawlDatum poll() throws InterruptedException {
        CrawlDatum request = (CrawlDatum) queue.poll(3000, TimeUnit.MILLISECONDS);
        if (request != null){
            takeTaskCount.incrementAndGet();
        }
        LOG.info("method=poll, url={}", request != null ? request.getUrl() : "");
        return request;
    }

    @Override
    public synchronized void offer(List<CrawlDatum> requestList) {
        for (CrawlDatum request : requestList){
            if (StringUtils.isNotEmpty(request.getUrl()) && (!hashSetFilter.contains(request.getUrl()))){
                hashSetFilter.add(request.getUrl());
                queue.offer(request);
                takeTaskCount.getAndIncrement();
                LOG.info("method=offer, url={}", request.getUrl());
            }
        }
    }

    @Override
    public int currentCount() {
        return queue.size();
    }

    @Override
    public void clear() {
        queue.clear();
        hashSetFilter.clear();
    }
}
