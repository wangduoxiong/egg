package edu.xiyou.andrew.egg.net;


import edu.xiyou.andrew.egg.model.CrawlDatum;
import edu.xiyou.andrew.egg.model.Site;
import org.apache.http.Header;
import org.apache.http.StatusLine;

import java.util.List;
import java.util.Map;
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

/**
 * Request之后的回应，Response存储
 * 得到的回应信息
 */
public interface Response {

    String getUrl();

    /**
     * 获取具体内容
     * @return 得到内容的字节数组
     */
    byte[] getContent();

    /**
     * 获取所有的头部信息
     * @return
     */
    Header[] getHeaders();

    /**
     * 获取对应的头部信息
     * @param name 头部的名字
     * @return 对应的头部信息
     */
    Header getHeader(String name);

    /**
     * 获取回应的状态信息
     * @return
     */
    StatusLine getStatusLine();

    /**
     * 返回处理之后的结果
     * @return
     */
    Map<String, String> getResults();

    /**
     * 查找特定的结果
     * @param key
     * @return
     */
    String getResult(String key);

    /**
     * 得到本页中所有的目标实体
     * @return
     */
    List<CrawlDatum> getTargetRequestList();

    /**
     * 得到所有的目标文件
     * @return
     */
    List<String> getAllLinksList();

    /**
     * 返回对应的站点信息及该站点的爬取设置
     * @return
     */
    Site getSite();

    /**
     * 返回编码集的名称
     * @return
     */
    String getCharSetName();
}
