/*
 *Copyright (c) 2015 Andrew-Wang.
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package edu.xiyou.andrew.Egg.parse;

import com.sleepycat.je.Environment;
import edu.xiyou.andrew.Egg.generator.Generator;
import edu.xiyou.andrew.Egg.generator.StardardGenerator;
import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.Egg.parse.filter.BloomFilter;
import edu.xiyou.andrew.Egg.persistence.BerkeleyWrite;
import edu.xiyou.andrew.Egg.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 任务的抓取队列
 * 当任务push时，直接存入数据库
 * Created by andrew on 15-2-2.
 */
public class QueueScheduler implements Scheduler{
    private static int queueSize = Config.queueSize;
    private static long maxSize = Config.MAX_ZIZE;
    protected static AtomicLong pushNum = new AtomicLong(0);
    protected static AtomicLong pullNum = new AtomicLong(0);
    private BlockingQueue<CrawlDatum> queue = new ArrayBlockingQueue<CrawlDatum>(queueSize);
    private static BloomFilter<String> bloomFilter = new BloomFilter<String>(7, (int)maxSize);
    private BerkeleyWrite write;
    private Generator generator;
    private Environment environment;

    protected final Lock lock = new ReentrantLock();
    protected Condition empty = lock.newCondition();
    protected Condition full = lock.newCondition();

    private static Logger logger = LoggerFactory.getLogger(QueueScheduler.class);

    public QueueScheduler(Environment environment){
        this.environment = environment;
        write = new BerkeleyWrite(environment);
        write.init();
        generator = new StardardGenerator(environment);
    }

    @Override
    public void push(CrawlDatum datum) {
        if (datum == null){
            return;
        }
        if (pushNum.get() > maxSize){
            logger.info("push crawldatum too more");
            return;
        }
        if (!bloomFilter.contains(datum.getUrl())){
            bloomFilter.add(datum.getUrl());
            try {
                write.writeLink(datum);
            }catch (Exception e){
                logger.info("Exception: " + e);
                return;
            }
            pushNum.incrementAndGet();
        }
    }

    {
        Thread putThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        putToQueue();
                    } catch (InterruptedException e) {
                        logger.info("InterruptedException" + e);
                    }
                }
            }
        });
        putThread.start();
    }

    private void putToQueue() throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() >= queueSize){
                full.await();
            }
            queue.add(generator.next());
            empty.signal();
        }finally {
            lock.unlock();
        }
    }

    private CrawlDatum takeFromQueue() throws InterruptedException {
        CrawlDatum datum = null;
        lock.lock();
        try {
            while (queue.isEmpty()){
                empty.await();
            }
            datum = queue.take();
            pullNum.incrementAndGet();
            full.signal();
        }finally {
            lock.unlock();
        }
        return datum;
    }

    @Override
    public CrawlDatum pull() {
        CrawlDatum datum = null;
        lock.lock();
        try {
            while (queue.isEmpty()){
                try {
                    empty.await();
                } catch (InterruptedException e) {
                    logger.info("InterruptedException" + e);
                }
            }
            datum = queue.take();
            full.signal();
        } catch (InterruptedException e) {
            logger.info("InterruptedException" + e);
        }finally {
            lock.unlock();
        }
        return datum;
    }
}
