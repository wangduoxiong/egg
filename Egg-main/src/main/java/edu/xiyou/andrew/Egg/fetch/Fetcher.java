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
import edu.xiyou.andrew.Egg.net.*;
import edu.xiyou.andrew.Egg.parse.Handler;
import edu.xiyou.andrew.Egg.parse.QueueScheduler;
import edu.xiyou.andrew.Egg.utils.Config;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected boolean running = true;
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

    public class FetcherThread implements Runnable{
        @Override
        public void run() {
            List<CrawlDatum> list;
            CrawlDatum datum = null;
            boolean hasMore = true;
            long feed = maxSize - fetched.get();
            threadLive.incrementAndGet();

            System.out.println("FetcherThread  running: " + running);
            while (running) {
                datum = scheduler.pull();

                System.out.println("FetcherThread " + datum);
                hasMore = (datum != null);
                try {
                    while ((feed > 0) && hasMore) {
                        request.setDatum(datum);
                        try {
                            lastRequestTime.set(System.currentTimeMillis());
                            response = request.getResponse();
                            int status = response.getStatusLine().getStatusCode();
                            for (int i = 0; (status != HttpStatus.SC_OK) && (i < Config.retry); i++) {
                                logger.info("retry: " + (i + 1) + "  , url: " + datum.getUrl());
                                response = request.getResponse();
                                status = response.getStatusLine().getStatusCode();
                            }

                            if (status != HttpStatus.SC_OK) {
                                handler.onFailed(response);
                                logger.info("fetch " + datum.getUrl() + "failed");
                                continue;
                            }
                            handler.onSuccess(response);

                            scheduler.getWrite().writeVisited(datum);
                            scheduler.getWrite().sync();
                            list = handler.handleAndGetLinks(response);

                            for (CrawlDatum elemnet : list) {
                                scheduler.push(datum);
                            }
                            scheduler.getWrite().sync();
                            feed--;
                            fetched.incrementAndGet();
                        } catch (Exception e) {
                            logger.info("Exception: " + e, new Exception());
                        } finally {
                        }
                        datum = scheduler.pull();
                        hasMore = (datum != null);
                        if (hasMore)
                        System.out.println(datum);
                    }
                }finally {
                    threadLive.decrementAndGet();
                }
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

    public void test(){
        before();
        boolean hasmore = true;
        CrawlDatum datum = null;
        while (hasmore){
            datum = scheduler.pull();
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

    public void fetch(){
        before();
        fetched = new AtomicLong(0);
        threadLive = new AtomicInteger(0);

//        FetcherThread thread = new FetcherThread();
//        new Thread(thread).start();
        FetcherThread[] fetcherThread = new FetcherThread[Config.threads];
        for (FetcherThread thread : fetcherThread){
            thread = new FetcherThread();
            //pool.execute(thread);
            new Thread(thread).start();
        }
//       pool.getService().shutdown();

        while (threadLive.get() > 0 && running) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }

            if (System.currentTimeMillis() - lastRequestTime.get() > Config.interval){
                logger.info("Aborting with " + threadLive + " hung threads.");
                break;
            }

        }

        //running = false;
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
