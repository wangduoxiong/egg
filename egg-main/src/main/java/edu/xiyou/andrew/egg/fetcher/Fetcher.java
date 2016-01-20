package edu.xiyou.andrew.egg.fetcher;

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


import com.google.common.collect.Lists;
import edu.xiyou.andrew.egg.dataprocessor.DataProcessor;
import edu.xiyou.andrew.egg.model.CrawlDatum;
import edu.xiyou.andrew.egg.model.Site;
import edu.xiyou.andrew.egg.net.*;
import edu.xiyou.andrew.egg.parser.Handler;
import edu.xiyou.andrew.egg.parser.HtmlParser;
import edu.xiyou.andrew.egg.parser.Parser;
import edu.xiyou.andrew.egg.scheduler.BloomDepthScheduler;
import edu.xiyou.andrew.egg.scheduler.Scheduler;
import edu.xiyou.andrew.egg.thread.ThreadPool;
import edu.xiyou.andrew.egg.utils.Config;
import edu.xiyou.andrew.egg.parser.RegexRule;
import edu.xiyou.andrew.egg.utils.UrlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 爬虫主体
 * Created by andrew on 15-6-7.
 */
public class Fetcher extends FetcherMonitor {
    private final static Logger LOGGER = LoggerFactory.getLogger(Fetcher.class);

    private static final int STOP = 1;
    private static final int EXIT = 2;
    private static final int RUNNING = 0;
    private static int DEFAULT_THREAD_COUNT = 16;
    private static final String CURRENT_THREAD_NAME = Thread.currentThread().getName();
    private static final String FETCH_OUTPUT_STRING = CURRENT_THREAD_NAME + " fetch: ";

    private Site site;
    private int depth;
    private final Handler handler;
    private Scheduler scheduler;
    private RegexRule regexRule;
    private ThreadPool threadPool;
    private RequestFactory requestFactory;
    private volatile int poolSize = DEFAULT_THREAD_COUNT;
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition newUrlCondition = reentrantLock.newCondition();
    private List<DataProcessor> dataProcessorList = Lists.newArrayList();
    private Condition activeThreadCondition = reentrantLock.newCondition();

    private AtomicInteger activeThread;

    private volatile int runStatus = RUNNING;
    private volatile long emptySleepTime = Config.emptySleepTime;

    public Fetcher(Scheduler scheduler, Handler handler, Site site,
                   List<DataProcessor> dataProcessorList, RegexRule regexRule,
                   RequestFactory requestFactory) {
        super();
        this.site = site;
        this.handler = handler;
        this.scheduler = scheduler;
        this.regexRule = regexRule;
        this.requestFactory = requestFactory;
        this.dataProcessorList = dataProcessorList;

        poolSize = site.getThreadCount();
        threadPool = new ThreadPool(poolSize);
        requestFactory.setThread(poolSize);
    }

    public static Fetcher.Builder custom() {
        return new Builder();
    }

    public static class Builder {
        private Scheduler scheduler;
        private Handler handler;
        private Site site;
        private List<DataProcessor> dataProcessorList;
        private RegexRule regexRule;
        private RequestFactory requestFactory;

        {
            site = new Site().setDomain(UrlUtils.acquireDomain());
            requestFactory = new HttpClientRequestFactory();
        }

        public Builder setScheduler(Scheduler scheduler) {
            this.scheduler = scheduler;
            return this;
        }

        public Builder setHandler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public Builder setSite(Site site) {
            this.site = site;
            return this;
        }

        public Builder setDataProcessorList(List<DataProcessor> dataProcessorList) {
            this.dataProcessorList = dataProcessorList;
            return this;
        }

        public Builder setRegexRule(RegexRule regexRule) {
            this.regexRule = regexRule;
            return this;
        }

        public Fetcher builder() {
            return new Fetcher(scheduler, handler, site, dataProcessorList, regexRule, requestFactory);
        }
    }

    class FecherThread implements Runnable {
        private Request request;
        private CrawlDatum datum;
        private long sleepTime = 5000;
        private ReentrantLock reentrantLock = new ReentrantLock();

        public FecherThread(CrawlDatum datum, Request request) {
            this.request = request;
            this.datum = datum;
        }

        @Override
        public void run() {
            try {
                if ((runStatus != EXIT) || (((ThreadPoolExecutor) threadPool.getService()).getQueue().size() != 0)) {
                    if (runStatus == STOP) {
                        stop();
                        Thread.sleep(5);
                    }
                    Page page;
                    if (datum == null){
                        return;
                    }
                    if (runStatus == RUNNING && StringUtils.isNotBlank(datum.getUrl())) {
                        LOGGER.info("start " + FETCH_OUTPUT_STRING + datum);
                        page = request.getResponse(datum, site);

                        synchronized (handler) {
                            if ((page == null) || (page.getStatusLine() == null) || !site.getAcceptStatusCode().contains(page.getStatusLine().getStatusCode())) {
                                handler.onFail(page);
                                LOGGER.info(FETCH_OUTPUT_STRING + " fetched error");
                            } else {
                                handler.onSuccess(page);
                                fetchCounted.incrementAndGet();
                                List<String> urlList = handler.getNextLinks(page);
                                addSeed(urlList);
                                if (CollectionUtils.isNotEmpty(dataProcessorList)){
                                    for (DataProcessor dataProcessor : dataProcessorList){
                                        dataProcessor.process(page);
                                    }
                                }
                                LOGGER.info(FETCH_OUTPUT_STRING + "fetched sucess");
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error(new StringBuffer().append(Thread.currentThread().getName()).
                        append(" InterruptedException: ").append(e).append("\ndatum:" ).append(datum).toString());
            } catch (Exception e) {
                LOGGER.error(new StringBuffer(Thread.currentThread().getName()).append(" Exception: ").append(e).
                        append("\ndatum:").append(datum).toString());
            } finally {
                pollCount.incrementAndGet();
            }
        }

        private void stop() {
            reentrantLock.lock();
            try {
                while (runStatus == STOP) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                LOGGER.error("op=stop " + e);
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    public void before() {
        if (scheduler instanceof BloomDepthScheduler) {
            ((BloomDepthScheduler) scheduler).merge();
        }
    }

    public void after() {
        while (((ThreadPoolExecutor) threadPool.getService()).getQueue().size() != 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        threadPool.close();
    }

    public void fetch() {
        before();
        init();

        while (!Thread.currentThread().isInterrupted() && (runStatus == RUNNING)) {
            try {
                CrawlDatum datum;
                int nullCount = 0;
                while ((datum = scheduler.poll()) == null) {
                    waitNewUrl();
                    nullCount++;
                    if (nullCount == Config.NULL_COUNT) {
                        runStatus = EXIT;
                        break;
                    }
                }
                LOGGER.info("scheduler current size = " + scheduler.currentCount());

                CrawlDatum finalDatum = datum;
                if (fetchCounted.get() > Config.FETCH_COUNT) {
                    runStatus = EXIT;

                }
                execute(new FecherThread(finalDatum, requestFactory.createRequest(datum, site)));

            } catch (InterruptedException e) {
                LOGGER.error("op=fetch exception: " + e);
            }
        }
    }

    private void execute(final Runnable runnable) {
        reentrantLock.lock();
        try {
            while (threadPool.getActiveThread().get() >= poolSize) {
                activeThreadCondition.await();
            }
        } catch (InterruptedException e) {
            LOGGER.error(Thread.currentThread().getName() + " run error: " + e);
        } finally {
            reentrantLock.unlock();
        }
        threadPool.getActiveThread().incrementAndGet();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    reentrantLock.lock();
                    try {
                        activeThreadCondition.signalAll();
                        threadPool.getActiveThread().decrementAndGet();
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            }
        });
    }

    private void waitNewUrl() {
        reentrantLock.lock();
        try {
            if (runStatus == EXIT) {
                return;
            }
            newUrlCondition.await(emptySleepTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("op=waitNewUrl exception: " + e);
        } finally {
            reentrantLock.unlock();
        }
    }

    public void stop() {
        runStatus = STOP;
    }

    public void init() {
        activeThread = new AtomicInteger(0);
        fetchCounted = new AtomicLong(0);
    }

    public void addSeed(List<String> urlList) {
        if (scheduler == null) {
            scheduler = new BloomDepthScheduler();
        }
        reentrantLock.lock();
        List<CrawlDatum>crawlDatumList = Lists.newArrayListWithCapacity(urlList.size());
        for (String url : urlList) {
            crawlDatumList.add(CrawlDatum.custom().setUrl(url).build());
        }
        scheduler.offer(crawlDatumList);

        try {
            newUrlCondition.signalAll();
        } finally {
            reentrantLock.unlock();
        }
    }

    public boolean isClose() {
        return threadPool.getService().isTerminated();
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
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

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public ThreadPool getThreadPool() {
        return threadPool;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Site getSite() {
        return site;
    }

    public Handler getHandler() {
        return handler;
    }

    public List<DataProcessor> getDataProcessorList() {
        return dataProcessorList;
    }

    public RegexRule getRegexRule() {
        return regexRule;
    }
}
