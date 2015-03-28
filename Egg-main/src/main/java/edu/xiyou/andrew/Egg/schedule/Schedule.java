package edu.xiyou.andrew.Egg.schedule;

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

import edu.xiyou.andrew.Egg.parse.CrawlDatum;

/**
 * 任务的取出和加入队列的接口
 * Created by andrew on 15-3-28.
 */
public interface Schedule {
    /**
     * 将任务加入数据库队列
     * @param datum 加入的任务
     * @throws Exception
     */
    public void push(CrawlDatum datum) throws Exception;

    /**
     * 从队列中取出并删除任务
     * @return 取出的任务
     * @throws Exception
     */
    public CrawlDatum pull() throws Exception;

    /**
     * 从队列中取出任务
     * @return 取出的任务
     * @throws Exception
     */
    public CrawlDatum peek() throws Exception;

    /**
     * 从队列中取出并删除任务
     * @return 取出的任务
     */
    public CrawlDatum take();

    /**
     * 将任务加入数据库队列
     * @param datum
     */
    public void put(CrawlDatum datum);
}
