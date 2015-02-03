package edu.xiyou.andrew.Egg.fetch;

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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by andrew on 15-2-3.
 */
public class ThreadPool {
    private int poolSize;
    private AtomicInteger threadLive;
    private Lock lock;
    private Condition condition;

    private ExecutorService service = null;
    private static Logger logger = LoggerFactory.getLogger(ThreadPool.class);

    {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public ThreadPool(int poolSize, ExecutorService service){
        this.poolSize = poolSize;
        this.service = service;
    }

    public ThreadPool(int poolSize){
        this.poolSize = poolSize;
        service = Executors.newFixedThreadPool(poolSize);
    }

    public void execute(final Runnable runnable){
        if (threadLive.get() > poolSize){
            try {
                lock.lock();
                while (threadLive.get() > poolSize){
                    condition.await();
                }
            } catch (InterruptedException e) {
                logger.info("InterruptedException: " + e);
            }finally {
                lock.unlock();
            }
        }
        threadLive.incrementAndGet();

        service.submit(new Runnable() {
            @Override
            public void run() {
                runnable.run();
                try {
                    lock.lock();
                    threadLive.decrementAndGet();
                    condition.signal();
                }finally {
                    lock.unlock();
                }
            }
        });
    }

    public ExecutorService getService() {
        return service;
    }

    public void setService(ExecutorService service) {
        this.service = service;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public AtomicInteger getThreadLive() {
        return threadLive;
    }
}