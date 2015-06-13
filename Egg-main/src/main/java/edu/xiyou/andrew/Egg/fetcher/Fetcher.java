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


import edu.xiyou.andrew.Egg.net.HttpRequest;
import edu.xiyou.andrew.Egg.parser.*;
import edu.xiyou.andrew.Egg.scheduler.BloomDepthScheduler;
import edu.xiyou.andrew.Egg.scheduler.BloomScheduler;
import edu.xiyou.andrew.Egg.scheduler.Scheduler;
import edu.xiyou.andrew.Egg.utils.Config;
import edu.xiyou.andrew.Egg.utils.FileUtils;
import edu.xiyou.andrew.Egg.utils.RegexRule;
import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by andrew on 15-6-7.
 */
public class Fetcher {
    private static Logger logger = LoggerFactory.getLogger(Fetcher.class);

    private static int RUNNING = 0;
    private static int STOP = 1;
    private static String CURRENT_THREAD_NAME = Thread.currentThread().getName();
    private static String FETCH_OUTPUT_STRING = CURRENT_THREAD_NAME + " fetch: ";

    protected Scheduler scheduler;
    private ThreadPool threadPool = new ThreadPool(Config.THREAD_COUNT);
    protected Handler handler;
    protected int depth;

    private final AtomicInteger activeThread = new AtomicInteger(0);
    private AtomicInteger waitThread = new AtomicInteger(0);
    private AtomicInteger fetchCount = new AtomicInteger(0);


    private volatile int runState = RUNNING;


    public Fetcher(Scheduler scheduler, Handler handler, int depth){
        this.handler = handler;
        this.scheduler = scheduler;
        this.depth = depth;
    }

    class FetcherThread implements Runnable{

//        private Scheduler scheduler;
        private AtomicInteger fetchCount;
        private HttpRequest request = new HttpRequest();
        private Html html;
        private ReentrantLock threadLock = new ReentrantLock();
        private Condition product = threadLock.newCondition();
        private Condition consume = threadLock.newCondition();

        public FetcherThread(AtomicInteger fetchCount){
            this.fetchCount = fetchCount;
        }

        int runState = RUNNING;

//        private String getTask(){
//            String url = null;
//            threadLock.lock();
//            try {
//                while ((url = scheduler.takeTasks()) == null){
//                    Thread.sleep(500);
//                }
//            } catch (InterruptedException e) {
//                logger.error(e.getMessage());
//            }finally{
//                thread.unlock;
//            }
//            return null;
//        }

        @Override
        public void run() {

            logger.info(CURRENT_THREAD_NAME + " is running \n");

            try {
                activeThread.incrementAndGet();
                while ((runState == RUNNING) && (fetchCount.get() < Config.FETCH_COUNT)) {
                    String url = null;
                    url = scheduler.takeTasks();
                    if (url != null) {

                        logger.info(FETCH_OUTPUT_STRING + url);
                        CrawlDatum datum = new CrawlDatum(url);
                        html = (Html) request.getResponse(datum);
                        datum.setFetchTime(System.currentTimeMillis());
                        if (html == null){
                            continue;
                        }

                        int status = html.getStatusLine().getStatusCode();
                        if (status != HttpStatus.SC_OK) {
                            handler.onFail(html);
                            logger.info(FETCH_OUTPUT_STRING + url + " fail", "StatusCode: " + status);
                            continue;
                        }

                        handler.onSuccess(html);
                        List<String> urlList = handler.handleAndGetLinks(html);
//                        System.out.println(urlList.size());
                        scheduler.putTasks(urlList);
                        fetchCount.incrementAndGet();
                    }
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            } finally {
                activeThread.decrementAndGet();
            }
        }

        public void stop(){
            runState = STOP;
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
        FetcherThread[] threads = new FetcherThread[Config.THREAD_COUNT];

        for (int i =0; i < Config.THREAD_COUNT; i++){
            threads[i] = new FetcherThread(fetchCount);
            threadPool.execute(threads[i]);
        }

        if (runState == STOP){
            for (int i = 0; i < Config.THREAD_COUNT; i++){
                threads[i].stop();
            }
        }

        if (activeThread.get() > 0){
            try {
                Thread.sleep(20 * 60 * 1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void stop(){
        runState = STOP;
    }

    public static void main(String[] args) {
        Scheduler scheduler1 = new BloomScheduler();
        scheduler1.putTasks(Arrays.asList("http://www.importnew.com/all-posts", "http://blog.csdn.net/"));
        Fetcher fetcher = new Fetcher(scheduler1, new Handler() {
            @Override
            public void onSuccess(Response response) {
//                System.out.println(((Html) response).getUrl() + response.getStatusLine());
//                System.out.println(new String(((Html)response).getContent()));
                String path = "/home/andrew/Data/";
                String fileName = path + System.currentTimeMillis();
                try {
                    FileUtils.write2File(new File(fileName), ((Html) response).getContent());
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
                return list;
            }
        }, 0);

//        try {
//            System.out.println(scheduler1.takeTasks());
//        } catch (InterruptedException e) {
//            logger.error(e.getMessage() + "bbbbbbbbbbbbbb");
//        }
        fetcher.fetch();
    }
}
