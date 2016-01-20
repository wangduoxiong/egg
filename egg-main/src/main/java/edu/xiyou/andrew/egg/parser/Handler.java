package edu.xiyou.andrew.egg.parser;

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

import edu.xiyou.andrew.egg.net.Page;

import java.util.List;

/**
 * 获取网页之后的操作接口
 * Created by andrew on 15-6-7.
 */
public interface Handler {

    /**
     * 当抓取成功所做操作
     * @param page {@link edu.xiyou.andrew.egg.net.Page}
     */
    void onSuccess(Page page);

    /**
     * 抓取失败所做操作
     * @param page {@link edu.xiyou.andrew.egg.net.Page}
     */
    void onFail(Page page);

    /**
     *  获取下一次操作的链接
     * @param page {@link edu.xiyou.andrew.egg.net.Page}
     * @return
     */
    List<String> getNextLinks(Page page);
}
