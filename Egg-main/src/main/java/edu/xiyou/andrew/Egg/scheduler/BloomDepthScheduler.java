package edu.xiyou.andrew.Egg.scheduler;

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


import edu.xiyou.andrew.Egg.utils.BloomFilter;
import edu.xiyou.andrew.Egg.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by andrew on 15-6-7.
 */
public class BloomDepthScheduler extends SchedulerMonitor implements Scheduler{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private BloomFilter<String> bloomFilter = new BloomFilter<String>(Config.BLOOMFILTER_ERROR_RATE, Config.FETCH_COUNT);
    private BlockingQueue<String> currentQueue = new LinkedBlockingQueue<String>();
    private BlockingQueue<String> nextQueue = new LinkedBlockingQueue<String>();

    @Override
    public String poll() {
        takeTaskCount.incrementAndGet();
        try {
            return currentQueue.take();
        } catch (InterruptedException e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    @Override
    public void offer(List<String> urls) {
        synchronized (this) {
            for (String url : urls) {
                if ((url != null) && !bloomFilter.contains(url)) {
                    putTaskCount.incrementAndGet();
                    nextQueue.offer(url);
                }
            }
        }
    }

    public void merge(){
        synchronized (this) {
            logger.info("----> SchedulerQueue merge start <----");
            currentQueue.clear();
            currentQueue.addAll(nextQueue);
            nextQueue.clear();
            logger.info("----> SchedulerQueue merge end <----");
        }
    }
}
