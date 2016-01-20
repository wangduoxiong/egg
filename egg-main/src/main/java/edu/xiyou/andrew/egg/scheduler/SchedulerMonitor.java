package edu.xiyou.andrew.egg.scheduler;

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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 队列的监控的
 * Created by andrew on 15-6-7.
 */
public abstract class SchedulerMonitor {
    protected AtomicInteger takeTaskCount = new AtomicInteger(0);
    protected AtomicInteger putTaskCount = new AtomicInteger(0);

    /**
     * 获取已经虫队列中取出请求的数量
     * @return
     */
    public int getRequestedCount(){
        return takeTaskCount.get();
    }

    /**
     * 获取所有存取待队列中的请求数量
     * @return
     */
    public int getTotalRequestCount(){
        return putTaskCount.get();
    }
}
