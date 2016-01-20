package edu.xiyou.andrew.egg.scheduler;

/*
 * Copyright (c) 2015 Andrew-Wang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.collect.Queues;
import edu.xiyou.andrew.egg.model.CrawlDatum;
import edu.xiyou.andrew.egg.scheduler.filter.BloomFilter;
import edu.xiyou.andrew.egg.utils.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.annotation.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrew on 15-6-7.
 */
public class BloomScheduler extends SchedulerMonitor implements Scheduler {
    private final static Logger LOGGER = LoggerFactory.getLogger(BloomScheduler.class);

    private BlockingQueue<CrawlDatum> queue = Queues.newLinkedBlockingQueue();
    private BloomFilter<String> bloomFilter = new BloomFilter<String>(Config.BLOOMFILTER_ERROR_RATE, Config.BLOOMFILTER_RATE);

    @Override
    public CrawlDatum poll() throws InterruptedException {
        CrawlDatum datum = queue.poll(3000, TimeUnit.MILLISECONDS);
        if (datum.getUrl() != null) {
            takeTaskCount.incrementAndGet();
        }
        LOGGER.info("method=poll, url={}", datum.getUrl());
        return datum;
    }

    @Override
    public synchronized void offer(List<CrawlDatum> requestList) {
        for (CrawlDatum request : requestList){
            if (StringUtils.isNotEmpty(request.getUrl()) && (!bloomFilter.contains(request.getUrl()))){
                bloomFilter.add(request.getUrl());
                queue.offer(request);
                takeTaskCount.getAndIncrement();
                LOGGER.info("method=offer, url={}", request.getUrl());
            }
        }
    }

    @Override
    public int currentCount() {
        return queue.size();
    }

    @Override
    public void clear() {
        bloomFilter.clear();
        queue.clear();
    }
}
