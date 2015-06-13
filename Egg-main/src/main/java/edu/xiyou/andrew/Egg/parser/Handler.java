package edu.xiyou.andrew.Egg.parser;

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

import java.util.List;

/**
 * Created by andrew on 15-6-7.
 */
public interface Handler {

    /**
     * 当抓取成功所做操作
     * @param response
     */
    public void onSuccess(Response response);

    /**
     * 抓取失败所做操作
     * @param response
     */
    public void onFail(Response response);

    /**
     * 得到下一次处理的链接
     * @param response
     * @return
     */
    public List<String> handleAndGetLinks(Response response);
}
