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

import edu.xiyou.andrew.egg.model.CrawlDatum;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrew on 15-6-6.
 */
public interface Scheduler {
    /**
     * 获取新的任务,超时则返回null
     *
     * @return
     */
    CrawlDatum poll(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * 获取新任务
     * @return
     * @throws InterruptedException
     */
    CrawlDatum poll() throws InterruptedException;

    /**
     * 添加新加入的任务,{@link edu.xiyou.andrew.egg.model.CrawlDatum}
     *
     * @param requestList
     * @return 插入成功返回 true, 插入失败返回null
     * @throws InterruptedException
     */
    boolean offer(List<CrawlDatum> requestList)throws InterruptedException;

    /**
     * 添加新任务
     * @param requestList
     * @param timeout
     * @param unit
     * @return 插入成功返回true, 插入失败或者超时返回false
     */
    boolean offer(List<CrawlDatum> requestList, long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * 当前调度器中存在的元素数量
     * @return
     */
    int currentCount();

    void clear();
}
