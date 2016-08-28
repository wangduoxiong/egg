package edu.xiyou.andrew.egg.scheduler;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import edu.xiyou.andrew.egg.model.CrawlDatum;
import edu.xiyou.andrew.egg.scheduler.filter.Filter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 默认的scheduler
 * 内部由两个组件构成
 * 一个是Queue 一个是Filter
 * 默认情况下Queue为 ArrayBlockingQueue,大小为100_0000;
 * 默认filter默认为null, 不对传入的值进行过滤
 * Created by andrew on 15-12-16.
 */
public class DefaultScheduler extends SchedulerMonitor implements Scheduler{
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultScheduler.class);

    private Queue<CrawlDatum> queue;
    private Filter<String> filter;

    public DefaultScheduler(){
        this(null, null);
    }

    public DefaultScheduler(Queue<CrawlDatum> queue){
        this(queue, null);
    }

    /**
     * 当传入queue为null时, queue默认为ArrayBlockingQueue
     * filter 为null,则不对值进行过滤
     * @param queue 队列
     * @param filter 过滤器
     */
    public DefaultScheduler(Queue<CrawlDatum> queue, Filter<String> filter){
        super();
        if (queue == null){
            this.queue = Queues.newArrayBlockingQueue(100_0000);
        }else {
            this.queue = queue;
        }
        this.filter = filter;
    }

    @Override
    public synchronized CrawlDatum poll() throws InterruptedException {
        CrawlDatum request = queue.poll();
        if (request != null){
            takeTaskCount.incrementAndGet();
        }
        LOGGER.info("method=poll, url={}", request != null ? request.getUrl() : "");
        return request;
    }


    public synchronized CrawlDatum poll(long time, TimeUnit timeUnit) throws InterruptedException {
        BlockingQueue<CrawlDatum> blockingQueue = checkAndReturnBlockingQueue(queue);

        CrawlDatum request = blockingQueue.poll(time, timeUnit);
        if (request != null){
            takeTaskCount.incrementAndGet();
        }
        LOGGER.info("method=poll, url={}", request != null ? request.getUrl() : "");
        return request;
    }

    @Override
    public synchronized boolean offer(List<CrawlDatum> requestList) {
        Preconditions.checkNotNull(requestList);
        boolean result = false;
        for (CrawlDatum request : requestList){
            if (StringUtils.isNotEmpty(request.getUrl()) && (!filter.contains(request.getUrl()))){
                filter.add(request.getUrl());
                result = queue.offer(request);
                takeTaskCount.getAndIncrement();
                LOGGER.info("method=offer, url={}", request.getUrl());
            }
        }
        return result;
    }

    @Override
    public boolean offer(List<CrawlDatum> requestList, long timeout, TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNull(requestList);
        BlockingQueue<CrawlDatum> blockingQueue = checkAndReturnBlockingQueue(queue);
        boolean result = false;

        for (CrawlDatum request : requestList){
            if (StringUtils.isNotEmpty(request.getUrl()) && (!filter.contains(request.getUrl()))){
                filter.add(request.getUrl());
                result = blockingQueue.offer(request, timeout, unit);
                takeTaskCount.getAndIncrement();
                LOGGER.info("method=offer, url={}", request.getUrl());
            }
        }
        return result;
    }

    private BlockingQueue<CrawlDatum> checkAndReturnBlockingQueue(Queue<CrawlDatum> queue){
        BlockingQueue<CrawlDatum> blockingQueue;
        if (queue instanceof BlockingQueue){
            blockingQueue = (BlockingQueue<CrawlDatum>) queue;
        }else {
            throw new UnsupportedOperationException("传入的队列不支持这种操作");
        }
        return blockingQueue;
    }

    @Override
    public int currentCount() {
        return queue.size();
    }

    private boolean contain(String url){
        return (filter != null) && filter.contains(url);
    }

    @Override
    public void clear() {
        queue.clear();
        filter.clear();
    }
}
