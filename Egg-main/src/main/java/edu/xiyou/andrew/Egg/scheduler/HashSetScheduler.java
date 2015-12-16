package edu.xiyou.andrew.Egg.scheduler;

import edu.xiyou.andrew.Egg.scheduler.filter.Filter;
import edu.xiyou.andrew.Egg.scheduler.filter.HashSetFilter;
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

    private BlockingQueue queue = new LinkedBlockingQueue();
    private Filter<String> hashSetFilter = new HashSetFilter<String>();

    @Override
    public synchronized String poll() throws InterruptedException {
        String url = (String) queue.poll(3000, TimeUnit.MILLISECONDS);
        if (url != null){
            takeTaskCount.incrementAndGet();
        }
        LOG.info("poll url: " + url);
        return url;
    }

    @Override
    public synchronized void offer(List<String> urls) {
        for (String url : urls){
            if (StringUtils.isNotEmpty(url) && (!hashSetFilter.contains(url))){
                hashSetFilter.add(url);
                queue.offer(url);
                takeTaskCount.getAndIncrement();
                LOG.info("offer url : ", url);
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
