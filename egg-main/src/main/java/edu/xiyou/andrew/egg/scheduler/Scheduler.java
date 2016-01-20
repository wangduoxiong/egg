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

/**
 * Created by andrew on 15-6-6.
 */
public interface Scheduler {
    /**
     * 获取新的任务,
     * @return
     */
    public CrawlDatum poll() throws InterruptedException;

    /**
     * 添加新加入的任务,{@link edu.xiyou.andrew.egg.model.CrawlDatum}
     * @param requestList
     */
    public void offer(List<CrawlDatum> requestList);

    public int currentCount();

    void clear();
}
