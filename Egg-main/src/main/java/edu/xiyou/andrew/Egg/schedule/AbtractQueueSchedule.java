package edu.xiyou.andrew.Egg.schedule;

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

import edu.xiyou.andrew.Egg.parse.CrawlDatum;
import edu.xiyou.andrew.Egg.schedule.persistent.DbUpdater;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrew on 15-3-29.
 */
public class AbtractQueueSchedule implements Schedule{
    protected BlockingQueue<String> queue;
    protected DbUpdater updater;

    @Override
    public CrawlDatum peek() {
        return new CrawlDatum(queue.peek());
    }

    @Override
    public CrawlDatum take() throws InterruptedException {
        return new CrawlDatum(queue.take());
    }

    @Override
    public void put(CrawlDatum datum) throws InterruptedException {
        if (datum == null){
            datum = null;
        }
        if (datum.getFetchTime() == CrawlDatum.UNFETCH_TIME){
            updater.write2Links(datum.getUrl());
        }else {
            updater.write2Visted(datum);
        }
    }

    @Override
    public CrawlDatum poll(long time, TimeUnit unit) throws InterruptedException {
        return new CrawlDatum(queue.poll(time, unit));
    }

    @Override
    public int size(){
        return queue.size();
    }
}
