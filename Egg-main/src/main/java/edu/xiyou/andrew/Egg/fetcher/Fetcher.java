package edu.xiyou.andrew.Egg.fetcher;

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


import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.Egg.net.HttpRequest;
import edu.xiyou.andrew.Egg.net.Request;
import edu.xiyou.andrew.Egg.parser.Handler;
import edu.xiyou.andrew.Egg.parser.Html;
import edu.xiyou.andrew.Egg.parser.LinksList;
import edu.xiyou.andrew.Egg.parser.Response;
import edu.xiyou.andrew.Egg.scheduler.BloomDepthScheduler;
import edu.xiyou.andrew.Egg.scheduler.BloomScheduler;
import edu.xiyou.andrew.Egg.scheduler.Scheduler;
import edu.xiyou.andrew.Egg.thread.ThreadPool;
import edu.xiyou.andrew.Egg.utils.Config;
import edu.xiyou.andrew.Egg.utils.FileUtils;
import edu.xiyou.andrew.Egg.utils.RegexRule;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by andrew on 15-6-7.
 */
public class Fetcher {
    private static Logger logger = LoggerFactory.getLogger(Fetcher.class);

    protected static final int RUNNING = 0;
    protected static final int STOP = 1;
    protected static final int EXIT = 2;
    protected static final String CURRENT_THREAD_NAME = Thread.currentThread().getName();
    protected static final String FETCH_OUTPUT_STRING = CURRENT_THREAD_NAME + " fetch: ";

    protected Scheduler scheduler;
    protected ThreadPool threadPool = new ThreadPool(Config.THREAD_COUNT);
    protected Handler handler;
    protected int depth;
    protected Request request;
    protected volatile int poolSize = Config.THREAD_COUNT;
    protected int ThreadCount = Config.THREAD_COUNT;
    protected ReentrantLock reentrantLock = new ReentrantLock();
    protected Condition newUrlCondition = reentrantLock.newCondition();
    protected Condition activeThreadCondition = reentrantLock.newCondition();

    protected AtomicInteger activeThread ;
    protected AtomicInteger fetchCount;

    protected volatile long emptySleepTime = Config.emptySleepTime;
    protected volatile int runStatus = RUNNING;

    public Fetcher(Scheduler scheduler, Handler handler) {
        this.scheduler = scheduler;
        this.handler = handler;
    }

    public Fetcher(Scheduler scheduler, Handler handler, int depth){
        this.handler = handler;
        this.scheduler = scheduler;
        this.depth = depth;
    }

    class FecherThread extends FetcherMonitor implements Runnable{
        private Request request;
        private long sleepTime = 5000;
        private ReentrantLock reentrantLock = new ReentrantLock();
        private String url;

        public FecherThread(String url, Request request) {
            this.url = url;
            this.request = request;
        }

        @Override
        public void run() {
            try {
                if (runStatus != EXIT) {
                    if (runStatus == STOP) {
                        stop();
                        Thread.sleep(5);
                    }
                    Html html;
                    if (runStatus == RUNNING) {
                        if (runStatus == RUNNING && StringUtils.isNotBlank(url)){
                            CrawlDatum datum = getCrawDatum(url);
                            html = (Html) request.getResponse(datum);
                            datum.setFetchTime(System.currentTimeMillis());

                            if ((html == null) || (html.getStatusLine() == null) || html.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                                handler.onFail(html);
                                logger.info(FETCH_OUTPUT_STRING + " fetched error");
                            }else {
                                handler.onSuccess(html);
                                List<String> urlList = handler.handleAndGetLinks(html);
                                addSeed(urlList);
//                                logger.info(FETCH_OUTPUT_STRING + "fetched sucess");
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                logger.error(Thread.currentThread().getName() + " InterruptedException: " +  e);
            } catch (Exception e) {
                logger.error(Thread.currentThread().getName() + " Exception: " + e);
            }
        }

        private void stop(){
            reentrantLock.lock();
            try {
                while (runStatus == STOP){
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                logger.error("op=stop " + e);
            }finally {
                reentrantLock.unlock();
            }
        }

        private CrawlDatum getCrawDatum(String url){
            CrawlDatum datum = new CrawlDatum(url);
            datum.getSite().setUserAgent(Config.USER_AGENT);
            return datum;
        }
    }

    public void before(){
        if (scheduler instanceof BloomDepthScheduler){
            ((BloomDepthScheduler) scheduler).merge();
        }
    }

    public void after(){
        threadPool.close();
    }

    public void fetch(){
        before();
        init();

        while (!Thread.currentThread().isInterrupted() && (runStatus == RUNNING)) {
            reentrantLock.lock();
            try {
                while (threadPool.getActiveThread().get() >= poolSize){
                    activeThreadCondition.await();
                }
            } catch (InterruptedException e) {
            }
            finally {
                reentrantLock.unlock();
            }
            try {
                String url;
                while ((url = scheduler.poll()) == null) {
                    waitNewUrl();
                }
                threadPool.execute(new FecherThread(url, request));

            } catch (InterruptedException e) {
                logger.error("op=fetch exception: " + e);
            }
            reentrantLock.lock();
            try {
                activeThreadCondition.signalAll();
            }finally {
                reentrantLock.unlock();
            }
        }
    }

    private void waitNewUrl() {
        reentrantLock.lock();
        try {
            if (runStatus == EXIT){
                return;
            }
            newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("op=waitNewUrl exception: " + e);
        }finally {
            reentrantLock.unlock();
        }
    }

    public void stop(){
        runStatus = STOP;
    }

    public void init(){
        activeThread = new AtomicInteger(0);
        fetchCount = new AtomicInteger(0);
        request = new HttpRequest().setPoolSize(poolSize);
    }

    public void addSeed(List<String> urlList){
        if (scheduler == null){
            scheduler = new BloomDepthScheduler();
        }
        scheduler.offer(urlList);

        reentrantLock.lock();
        try {
            newUrlCondition.signalAll();
        }finally {
            reentrantLock.unlock();
        }
    }

    public static void main(String[] args) {
        Scheduler scheduler1 = new BloomScheduler();
        Fetcher fetcher = new Fetcher(scheduler1, new Handler() {
            @Override
            public void onSuccess(Response response) {
                String path = "/home/duoxiongwang/Documents/project/data/";
                String fileName = path + System.currentTimeMillis();
                try {
                    FileUtils.write2File(new File(fileName), response.getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Response response) {

            }

            @Override
            public List<String> handleAndGetLinks(Response response) {
                LinksList list = new LinksList();
                RegexRule regexRule = new RegexRule();
                regexRule.addPositive(Arrays.asList("http://blog.csdn.net/\\w+/article/details/\\d+",
                        "http://www.importnew.com/\\d+.html",
                        "http://blog.csdn.net/?&page=\\d+"));
                list.getLinkByA(Jsoup.parse(new String(response.getContent())), regexRule);

                logger.info("\n\n\n" + list + "\n\n\n");
                return list;
            }
        }, 0);
        fetcher.init();
        fetcher.before();
        fetcher.setScheduler(scheduler1);
        scheduler1.offer(Arrays.asList("http://www.importnew.com/all-posts", "http://blog.csdn.net"));

        fetcher.fetch();
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public int getThreadCount() {
        return ThreadCount;
    }

    public void setThreadCount(int threadCount) {
        ThreadCount = threadCount;
    }

    public int getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(int runStatus) {
        this.runStatus = runStatus;
    }

    public AtomicInteger getActiveThread() {
        return activeThread;
    }

    public AtomicInteger getFetchCount() {
        return fetchCount;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
