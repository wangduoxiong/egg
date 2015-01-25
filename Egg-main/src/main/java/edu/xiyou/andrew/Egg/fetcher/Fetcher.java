package edu.xiyou.andrew.Egg.fetcher;

import edu.xiyou.andrew.Egg.generator.DBUpdater;
import edu.xiyou.andrew.Egg.generator.StandardGenerator;
import edu.xiyou.andrew.Egg.net.HttpRequest;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.CrawlDatum;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.HttpResponse;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.Link;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.Response;
import edu.xiyou.andrew.Egg.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by andrew on 15-1-23.
 */
public class Fetcher{
    public static final Logger LOG = LoggerFactory.getLogger(Fetcher.class);

    public DBUpdater dbUpdater = null;
    public HttpRequest request = null;
    public Handle handle = null;

    private int retry = Config.retry;
    private AtomicInteger acitveThreads;
    private AtomicInteger spinWaiting;
    private AtomicLong lastRequestStart;
    private QueueFeeder feeder;
    private FetchQueue fetchQueue;


    public static final int FETCH_SUCESS = 1;
    public static final int FETCH_FAILED = 2;

    private int threads = Config.threadNum;
    private boolean isContentStored = false;

    public static class FetchItem{
        public CrawlDatum datum;

        public FetchItem(CrawlDatum datum){
            this.datum = datum;
        }
    }

    public static class FetchQueue{
        public AtomicInteger totalSize = new AtomicInteger(0);

        public final List<FetchItem> queue = Collections.synchronizedList(new LinkedList<FetchItem>());

        public void clear(){
            queue.clear();
        }

        public int getSize(){
            return queue.size();
        }

        public synchronized void addFetchItem(FetchItem item){
            if (item == null){
                return;
            }

            queue.add(item);

            totalSize.incrementAndGet();
        }

        public synchronized FetchItem getFetchItem(){
            if (queue == null){
                return null;
            }
            return queue.remove(0);
        }

        public synchronized void dump(){
            for (int i = 0; i < queue.size(); i++){
                FetchItem it = queue.get(i);
                LOG.info(" " + i + ". " + it.datum.getUrl());
            }
        }
    }

    public static class QueueFeeder extends Thread{
        public FetchQueue queue;
        public StandardGenerator generator;

        public int size;

        public QueueFeeder(FetchQueue queue, StandardGenerator generator, int size){
            this.queue = queue;
            this.generator = generator;
            this.size = size;
        }

        public boolean running = true;

        public void stopFeeder(){
            running = false;

            while (this.isAlive()){
                try {
                    Thread.sleep(1000);
                    LOG.info("stopping feeder ...... ");
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

                while (feed > 0 && hasMore && running){
                    CrawlDatum datum = generator.nextDatum();
                    hasMore = (datum != null);

                    if (hasMore){
                        queue.addFetchItem(new FetchItem(datum));
                        feed--;
                    }
                }
                generator.close();
            }
        }
    }

    boolean running;

    private class FetcherThread extends Thread{

        @Override
        public void run() {
            acitveThreads.incrementAndGet();
            FetchItem item = null;

            try {
                while (running){
                    try {
                        item = fetchQueue.getFetchItem();
                        if (item == null){
                            if (feeder.isAlive() || fetchQueue.getSize() > 0){
                                spinWaiting.incrementAndGet();

                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {

                                }

                                spinWaiting.decrementAndGet();
                                continue;
                            }else {
                                return;
                            }
                        }

                        lastRequestStart.set(System.currentTimeMillis());

                        String url = item.datum.getUrl();
                        HttpResponse response = null;

                        for (int retryCount = 0; retryCount <= retry; retryCount++){
                            if (retryCount > 0){
                                LOG.info("retry :" + retryCount + "th" + url);
                            }

                            try {
                                response = request.getResponse(url);
                                break;
                            } catch (IOException e) {
                                String logMessage = "fetch of " + url + "failed once with " + e.getMessage();
                                if (retryCount > 0){
                                    logMessage += "   retry";
                                }
                                LOG.info(logMessage);
                            }
                        }
                        CrawlDatum crawlDatum = null;
                        if (response != null){
                            LOG.info("fetch " + url);
                            crawlDatum = new CrawlDatum(url, CrawlDatum.STATUS_FETCHED, lastRequestStart.get());
                        }else {
                            LOG.info("failed " + url);
                            crawlDatum = new CrawlDatum(url, CrawlDatum.STATUS_UNFETCH, CrawlDatum.TIME_UNFETCH);
                        }

                        try {
                            dbUpdater.getSegmentWrite().writeFetch(crawlDatum);
                            if (response == null){
                                continue;
                            }

                            List<String> nextLinks = null;

                            try {
                                nextLinks = handle.visitAndGetNextLinks(response);
                            }catch (Exception e){
                                LOG.info("Exception :", e);
                            }

                            if (nextLinks != null && !nextLinks.isEmpty()){
                                dbUpdater.getSegmentWrite().writeLinks(nextLinks);
                            }

                        } catch (Exception e) {
                            LOG.info("Exception :" + e);
                        }
                    }catch (Exception e){
                        LOG.info("Exception :" + e);
                    }
                }
            }catch (Exception e){
                LOG.info("Exception :" + e);
            }finally {
                acitveThreads.decrementAndGet();
            }
        }
    }

    private void before() throws Exception{
        try {
            if (dbUpdater.islocked()) {
                dbUpdater.merge();
                dbUpdater.unlocked();
            }
        }catch (Exception e){
            LOG.info("Exception :" + e);
        }
        dbUpdater.locked();
        running = true;
    }

    public void fetchAll(StandardGenerator generator) throws Exception{
        before();

        lastRequestStart = new AtomicLong(System.currentTimeMillis());

        acitveThreads = new AtomicInteger(0);
        spinWaiting = new AtomicInteger(0);
        fetchQueue = new FetchQueue();
        feeder = new QueueFeeder(fetchQueue, generator, 1000);
        feeder.start();

        FetcherThread[] fetcherThreads = new FetcherThread[threads];
        for (int i = 0; i < threads; i++){
            fetcherThreads[i] = new FetcherThread();
            fetcherThreads[i].start();
        }

        do {
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){

            }

            LOG.info("-activeThread=" + acitveThreads.get()
            + ", spinWaiting=" + spinWaiting.get()
            + ", fetchQueue.size=" + fetchQueue.getSize());

            if (!feeder.isAlive() && fetchQueue.getSize() < 5){
                fetchQueue.dump();
            }

            if ((System.currentTimeMillis() - lastRequestStart.get()) > Config.interval ){
                LOG.info("Aborting with " + acitveThreads + " hung threads.");
                break;
            }
        }while (acitveThreads.get() > 0 && running);

        running = false;
        long waitThreadEndStartTime = System.currentTimeMillis();
        if (acitveThreads.get() > 0){
            LOG.info("wait for activeThreads to end");
        }

        while (acitveThreads.get() > 0){
            LOG.info("-activeThreads= " + acitveThreads.get());
            try {
                Thread.sleep(500);
            }catch (Exception e){

            }

            if (System.currentTimeMillis() - waitThreadEndStartTime > Config.WAIT_THREAD_END_TIME){
                LOG.info("kill threads");
                for (int i = 0; i < fetcherThreads.length; i++){
                    if (fetcherThreads[i].isAlive()){
                        try {
                            fetcherThreads[i].stop();
                            LOG.info("kill thread " + i);
                        }catch (Exception e){
                            LOG.info("Exception ", e);
                        }

                    }
                }
            }
            break;
        }
    }

    public void stop(){
        running = false;
    }

    private void after() throws Exception{
        dbUpdater.close();
        dbUpdater.merge();
        dbUpdater.close();
    }

    public int getThreads(){
        return threads;
    }

    public void setThreads(int threads){
        this.threads = threads;
    }

    public int getRetry(){
        return retry;
    }

    public void setRetry(int retry){
        this.retry = retry;
    }


    public boolean isContentStored(){
        return isContentStored;
    }

    public void setContentStored(boolean isContentStored){
        this.isContentStored = isContentStored;
    }

    public void setDbUpdater(DBUpdater dbUpdater){
        this.dbUpdater = dbUpdater;
    }

    public DBUpdater getDbUpdater(){
        return dbUpdater;
    }

    public void setHttpRequest(HttpRequest request){
        this.request = request;
    }

    public HttpRequest getHttpRequest(){
        return request;
    }

    public Handle getHandle(){
        return handle;
    }

    public void setHandle(Handle handle){
        this.handle = handle;
    }
}
