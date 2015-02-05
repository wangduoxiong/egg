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
import edu.xiyou.andrew.Egg.fetch.ThreadPool;
import edu.xiyou.andrew.Egg.generator.Generator;
import edu.xiyou.andrew.Egg.generator.StardardGenerator;
import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.Egg.parse.filter.BloomFilter;
import edu.xiyou.andrew.Egg.persistence.BerkeleyDBUpdate;
import edu.xiyou.andrew.Egg.persistence.BerkeleyWrite;
import edu.xiyou.andrew.Egg.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeMap;
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
public class QueueScheduler implements Scheduler {
    private static int queueSize = Config.queueSize;
    private static long maxSize = Config.MAX_ZIZE;
    protected static AtomicLong pushNum = new AtomicLong(0);
    protected static AtomicLong pullNum = new AtomicLong(0);
    private BlockingQueue<CrawlDatum> queue = new ArrayBlockingQueue<CrawlDatum>(queueSize);
    private static BloomFilter<String> bloomFilter = new BloomFilter<String>(0.001, (int) maxSize);
    private Generator generator;
    private Environment environment;
    private BerkeleyDBUpdate update;

    protected final Lock lock = new ReentrantLock();
    protected Condition empty = lock.newCondition();
    protected Condition full = lock.newCondition();
    protected final Lock DbLock = new ReentrantLock();
    protected Condition DbEmpty = DbLock.newCondition();

    private static Logger logger = LoggerFactory.getLogger(QueueScheduler.class);

    public QueueScheduler(Environment environment) {
        this.environment = environment;
        update = new BerkeleyDBUpdate(environment);
        update.getWrite().init();
        generator = new StardardGenerator(environment);
    }

    @Override
    public void push(CrawlDatum datum) {
        if (datum == null) {
            return;
        }
        if (pushNum.get() > maxSize) {
            logger.info("push crawldatum too more");
            return;
        }
        if (!bloomFilter.contains(datum.getUrl())) {
            bloomFilter.add(datum.getUrl());
            try {
                System.out.println("push: " + datum);
                DbLock.lock();
                try {
                    update.getWrite().writeLink(datum);
                    DbEmpty.signalAll();
                }finally {
                    DbLock.unlock();
                }
            } catch (Exception e) {
                logger.info("Exception: " + e);
                return;
            }
            pushNum.incrementAndGet();
        }
    }

    private void threadStart() {
        Thread putThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++)
                {
                    putToQueue();
                }
            }
        });
        putThread.start();
    }


    public void putToQueue(){
        lock.lock();
        try {
            CrawlDatum datum = null;
            datum = generator.next();
            if (datum != null){
                queue.add(datum);
                System.out.println("------------------------------haved     add");
            }
            System.out.println("put  put put");
        }finally {
            lock.unlock();
        }
    }

//    private void putToQueue() throws InterruptedException {
//        lock.lock();
//        CrawlDatum datum = null;
//        try {
//            while (queue.size() >= queueSize){
//                full.await();
//            }
//            datum = generator.next();
//            System.out.println("putToQueue datum: " + datum);
//            DbLock.lock();
//            try {
//                if (datum != null) {
//                    queue.add(datum);
//                    empty.signal();
//                    System.out.println("------------------------------haved     add");
//                } else {
//                    //DbEmpty.await();
//                    //Thread.sleep(300);
//                }
//            }finally {
//                DbLock.unlock();
//            }
//        }finally {
//            lock.unlock();
//        }
//    }

    @Override
//    public CrawlDatum pull() {
//        CrawlDatum datum = null;
//        lock.lock();
//
//        System.out.println("queue size is : " + queue.size());
//        try {
//            while (queue.isEmpty()){
//                //empty.await();
//                Thread.sleep(50);
//            }
//            datum = queue.remove();
//            pullNum.incrementAndGet();
//            full.signal();
//        } catch (Exception e) {
//            logger.info("InterruptedException" + e);
//        }finally {
//            lock.unlock();
//        }
//        return datum;
//    }

    public CrawlDatum pull(){
        CrawlDatum datum = null;

        if ((queue.size() < 10)){
            if (((StardardGenerator)generator).count() - StardardGenerator.getTatol() >= 10) {
                threadStart();
            }else if ((queue.size() == 0) && (((StardardGenerator)generator).count() - StardardGenerator.getTatol() > 0)){
                threadStart();
            }
        }
        while (queue.isEmpty()){
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {

            }
        }
        datum = queue.remove();
        pullNum.incrementAndGet();
        return datum;
    }

    public static AtomicLong getPushNum() {
        return pushNum;
    }

    public static AtomicLong getPullNum() {
        return pullNum;
    }

    public BerkeleyWrite getWrite() {
        return update.getWrite();
    }

    public BerkeleyDBUpdate getUpdate() {
        return update;
    }

    public Generator getGenerator() {
        return generator;
    }
}
