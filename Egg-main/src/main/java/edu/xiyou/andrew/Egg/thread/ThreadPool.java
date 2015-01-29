package edu.xiyou.andrew.Egg.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程池
 * Created by andrew on 15-1-29.
 */
public class ThreadPool {
    private int threadNum;
    private AtomicInteger threadLive;
    private Lock lock;
    private Condition condition;

    private ExecutorService service = null;

    {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public ThreadPool(int threadNum, ExecutorService service){
        this.threadNum = threadNum;
        this.service = service;
    }

    public ThreadPool(int threadNum){
        this.threadNum = threadNum;
        service = Executors.newFixedThreadPool(threadNum);
    }

    public int getThreadLive() {
        return threadLive.get();
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public ExecutorService getService() {
        return service;
    }

    public void setService(ExecutorService service) {
        this.service = service;
    }

    public void setThreadLive(AtomicInteger threadLive) {
        this.threadLive = threadLive;
    }

    public void execute(final Runnable runnable){
        if (threadLive.get() > threadNum){
            try{
                lock.lock();
                while (threadLive.get() > threadNum){
                    condition.await();
                }
            } catch (InterruptedException e) {

            }finally {
                lock.unlock();
            }
        }
        threadLive.incrementAndGet();

        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                }finally {
                    try {
                        lock.lock();
                        threadLive.decrementAndGet();
                        condition.signal();
                    }finally {
                        lock.unlock();
                    }
                }
            }
        });
    }

    public void shutdown(){
        if (service != null) {
            service.shutdown();
        }
    }

    public boolean isShutdown(){
        if (service != null){
            return true;
        }
        return service.isShutdown();
    }
}
