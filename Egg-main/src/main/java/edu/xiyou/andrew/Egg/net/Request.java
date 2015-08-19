package edu.xiyou.andrew.Egg.net;

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
 * Created by andrew on 15-5-20.
 */


import edu.xiyou.andrew.Egg.parser.Response;

/**
 * 进行获取任务的接口，定义了一个获取方法，通过
 * 该方法可以得到相应的回应。回应的接口Response。
 */
public interface Request {

    /**
     * 通过种子获取内容
     * @param datum 种子
     * @return 回应的具体内容
     */
    Response getResponse(CrawlDatum datum) throws Exception;
}
