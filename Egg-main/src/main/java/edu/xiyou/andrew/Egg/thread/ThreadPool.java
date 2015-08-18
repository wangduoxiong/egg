package edu.xiyou.andrew.Egg.thread;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by andrew on 15-6-7.
 */
public class ThreadPool {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int DEFAULT_POOL_SIZE = 8;
    private final int MAX_POOL_SIZE = 64;

    private volatile int poolSize = DEFAULT_POOL_SIZE;
    private AtomicInteger activeThread = new AtomicInteger(0);
    private ExecutorService service = null;

    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition condition = reentrantLock.newCondition();

    public ThreadPool(int poolSize, ExecutorService service){
        if (checkPoolSize(poolSize)){
            this.poolSize = poolSize;
        }
        this.service = service;
    }

    public ThreadPool(int poolSize){
        if (checkPoolSize(poolSize)) {
            this.poolSize = poolSize;
        }
        service = Executors.newFixedThreadPool(this.poolSize);
    }

    public synchronized void execute(final Runnable runnable){
        if (activeThread.get() >= poolSize){
            reentrantLock.lock();
            try {
                while (activeThread.get() < poolSize){
                    condition.await();
                }
            } catch (InterruptedException e) {
            }finally {
                reentrantLock.unlock();
            }
        }
        activeThread.getAndDecrement();
        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                }finally {
                    reentrantLock.lock();
                    try {
                        activeThread.decrementAndGet();
                        condition.signalAll();
                    }finally {
                        reentrantLock.unlock();
                    }

                }
            }
        });

    }

    private boolean checkPoolSize(int poolSize){
        if ((poolSize > DEFAULT_POOL_SIZE) && (poolSize < MAX_POOL_SIZE)){
            return true;
        }
        return false;
    }

    public ExecutorService getService(){
        return service;
    }

    public void close(){
        service.shutdown();
    }

    public AtomicInteger getActiveThread() {
        return activeThread;
    }


    //    static class Task implements Runnable{
//
//        @Override
//        public void run() {
//            System.out.println(Thread.currentThread().getName() + "----->" + activeThread.getAndIncrement());
//        }
//    }
//    public static void main(String[] args) {
//        ThreadPool threadPool = new ThreadPool(5);
//        for (int i = 0; i < 200; i++){
//            threadPool.execute(new Task());
//        }
//    }
}
