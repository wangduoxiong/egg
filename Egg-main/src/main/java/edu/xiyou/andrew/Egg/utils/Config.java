/*
 *Copyright (c) 2015 Andrew-Wang. 
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package edu.xiyou.andrew.Egg.utils;

/**
 * 爬虫的整体配置
 * Created by andrew on 15-2-3.
 */
public class Config {
    public static final long MAX_ZIZE = 1000 * 1000;        //爬取网页的数量

    public static int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    public static int poolSize = 2 * CPU_COUNT;             //线程池的容量

    public static int retry = 3;

    public static int queueSize = 100;                      //存取取出任务的队列大小

    public static long interval = -1;                       //相同任务爬取的时间间隔
}
