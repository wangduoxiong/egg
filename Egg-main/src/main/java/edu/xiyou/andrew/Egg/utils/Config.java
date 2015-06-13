package edu.xiyou.andrew.Egg.utils;

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

/**
 * 设置爬虫的全局桉树参数
 * Created by andrew on 15-5-21.
 */
public class Config {

    public static final String USER_AGENT = "";

    public static final int RETRY = 3;

    public static final int TTIME_OUT = 5000;

    public static final int INTERVAL_JUST_ONE = -1;
    public static int interval = INTERVAL_JUST_ONE;

    public static final int FETCH_COUNT = 1000 * 1000;
    public static final double BLOOMFILTER_ERROR_RATE = 0.001;

    public static int THREAD_COUNT = 16;
}
