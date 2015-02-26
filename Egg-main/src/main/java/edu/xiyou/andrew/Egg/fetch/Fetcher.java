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
package edu.xiyou.andrew.Egg.fetch;


import com.sleepycat.je.Environment;
import edu.xiyou.andrew.Egg.generator.StardardGenerator;
import edu.xiyou.andrew.Egg.net.*;
import edu.xiyou.andrew.Egg.parse.Handler;
import edu.xiyou.andrew.Egg.parse.QueueScheduler;
import edu.xiyou.andrew.Egg.utils.Config;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by andrew on 15-2-3.
 */
public class Fetcher {
    protected HttpRequest request;
    protected HttpResponse response;
    protected QueueScheduler scheduler;
    protected Handler handler;
    protected ThreadPool pool;
    protected Environment environment;
    protected AtomicInteger threadLive;
    protected AtomicInteger spinWaiting;                //处于阻塞状态的线程数量
    protected boolean running = true;
    protected FetchQueue fetchQueue;
    protected QueueFeeder feeder;
    protected AtomicLong fetched;                       //已经爬取了的页面
    protected long maxSize = Config.MAX_ZIZE;           //最多爬取页面的数量
    protected AtomicLong lastRequestTime;

    private static final Logger logger = LoggerFactory.getLogger(Fetcher.class);

    public Fetcher(Environment environment){
        this.environment = environment;
        pool = new ThreadPool(Config.poolSize);
        scheduler = new QueueScheduler(environment);
        request = new HttpRequest();
        lastRequestTime = new AtomicLong(0);
    }

    public static class FetchQueue{
        public AtomicLong totalSize = new AtomicLong(0);
        public final List<CrawlDatum> queue = Collections.synchronizedList(new LinkedList<CrawlDatum>());

        public void clear(){
            queue.clear();
        }

        public int getSize(){
            return queue.size();
        }

        public synchronized void addFetchItem(CrawlDatum datum){
            if (datum == null){
                return;
            }
            queue.add(datum);
            totalSize.incrementAndGet();
        }

        public synchronized CrawlDatum getFetchItem(){
            while (queue.isEmpty()){
                return null;
            }
            return queue.remove(0);
        }

        public void dump(){
            for (int i = 0; i < queue.size(); i++){
                CrawlDatum datum = queue.get(i);
                logger.info("  " + i + ". " + datum.getUrl());
            }
        }
    }

    public static class QueueFeeder extends Thread{
        public FetchQueue queue;
        public StardardGenerator generator;
        public int size;

        public QueueFeeder(FetchQueue queue, StardardGenerator generator, int size){
            this.queue = queue;
            this.generator = generator;
            this.size = size;
        }

        boolean running = true;

        public void stopFeeder(){
            running = false;
            while (this.isAlive()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
        }

        @Override
        public void run() {
            boolean hasMore = true;
            running = true;
            while (hasMore && running){
                int feed = size - queue.getSize();
                if (feed <= 0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                    continue;
                }
                while ((feed > 0) && hasMore && running){
                    CrawlDatum datum = generator.next();
                    hasMore = (datum != null);

                    if (hasMore){
                        queue.addFetchItem(datum);
                        feed--;
                    }
                }
            }
            generator.close();
        }
    }

    public class FetcherThread extends Thread {
        @Override
        public void run() {
            List<CrawlDatum> list = new LinkedList<CrawlDatum>();
            CrawlDatum datum = null;
            threadLive.incrementAndGet();

            System.out.println("FetcherThread  running: " + running);
            try {

                while (running) {
                    try {
                        datum = fetchQueue.getFetchItem();
                        if (datum == null) {
                            if (feeder.isAlive() || (fetchQueue.getSize() > 0)) {
                                spinWaiting.incrementAndGet();

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {

                                }
                                spinWaiting.decrementAndGet();
                                continue;
                            } else {
                                return;
                            }
                        }
                        lastRequestTime.set(System.currentTimeMillis());

                        request.setDatum(datum);
                        response = request.getResponse();
                        int status = response.getStatusLine().getStatusCode();
                        for (int i = 0; (status != HttpStatus.SC_OK) && (i < Config.retry); i++) {
                            logger.info("retry: " + (i + 1) + "  , url: " + datum.getUrl());
                            response = request.getResponse();
                            status = response.getStatusLine().getStatusCode();
                        }

                        scheduler.getWrite().writeVisited(datum);
                        scheduler.getWrite().sync();

                        if (status != HttpStatus.SC_OK) {
                            handler.onFailed(response);
                            logger.info("fetch " + datum.getUrl() + "failed");
                            continue;
                        }

                        handler.onSuccess(response);
//                        list = handler.handleAndGetLinks(response);
//                        for (CrawlDatum elemnet : list) {
//                            scheduler.push(datum);
//                        }
//                        scheduler.getWrite().sync();
                    } catch (IOException e) {
                        logger.info("IOException " + e, new Exception());
                    } catch (Exception e) {
                        logger.info("Exception " + e, new Exception());
                    }
                }
            } finally {
                threadLive.decrementAndGet();
            }
        }
    }

    private void before(){
        try {
            logger.info("function before start...");
            scheduler.getUpdate().merge();
            scheduler.getWrite().init();
            running = true;
            logger.info("function before end.....");
        }catch (Exception e){
            logger.info("Exception: " + e, new Exception());
        }
    }

    public void test(StardardGenerator generator){
        before();
        fetchQueue = new FetchQueue();
        feeder = new QueueFeeder(fetchQueue, generator, 1000);
        try {
            feeder.start();
            feeder.join();
        } catch (InterruptedException e) {
        }
        boolean hasmore = true;
        CrawlDatum datum = null;
        while (hasmore){
            datum = fetchQueue.getFetchItem();
            //datum = generator.next();
            hasmore = (datum != null);
            if (hasmore){
                System.out.println(datum);
            }
        }
    }

    public void after(){
        scheduler.getWrite().sync();
        scheduler.getWrite().close();
    }

    public void fetch(StardardGenerator generator){
        before();
        fetched = new AtomicLong(0);
        spinWaiting = new AtomicInteger(0);
        fetchQueue = new FetchQueue();
        threadLive = new AtomicInteger(0);
        feeder = new QueueFeeder(fetchQueue, generator, 1000);
        feeder.start();
        try {
            feeder.join();
        } catch (InterruptedException e) {
        }

//        FetcherThread thread = new FetcherThread();
//        new Thread(thread).start();
        FetcherThread[] fetcherThreads = new FetcherThread[Config.threads];
        for (int i = 0; i < fetcherThreads.length; i++){
            fetcherThreads[i] = new FetcherThread();
            //pool.execute(thread);
            fetcherThreads[i].start();
        }
//       pool.getService().shutdown();


        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            logger.info("-activeThreads=" + threadLive.get()
                    + ", spinWaiting=" + spinWaiting.get() + ", fetchQueue.size="
                    + fetchQueue.getSize());
            if (!feeder.isAlive() && fetchQueue.getSize() > 5){
                fetchQueue.dump();
            }

            if ((System.currentTimeMillis() - lastRequestTime.get()) > Config.WAIT_THREAD_END_TIME){
                logger.info("Aborting with " + threadLive + " hung threads.");
                break;
            }
        }while ((threadLive.get() > 0) && running);
        running = false;
        long waiThreadEndStartTime = System.currentTimeMillis();
        if (threadLive.get() > 0){
            logger.info("wait for activeThreads to end");
        }


        while (threadLive.get() > 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }

            if (System.currentTimeMillis() - lastRequestTime.get() > Config.WAIT_THREAD_END_TIME){
                logger.info("kill threads");
                for (int i = 0; i < fetcherThreads.length; i++){
                    if (fetcherThreads[i].isAlive()){
                        try {
                            fetcherThreads[i].stop();
                            logger.info("kill thread " + i);
                        }catch (Exception e){
                            logger.info("Exception " + e, new Exception());
                        }
                    }
                }
                break;
            }

        }
        logger.info("clear all active threads");
        feeder.stop();
        fetchQueue.clear();
        after();
    }

    public void stop(){
        running = false;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public QueueScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(QueueScheduler scheduler) {
        this.scheduler = scheduler;
    }
}
