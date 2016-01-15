package edu.xiyou.andrew.Egg.net;


import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
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

    public String getUrl();

    /**
     * 获取具体内容
     * @return 得到内容的字节数组
     */
    public byte[] getContent();

    /**
     * 获取所有的头部信息
     * @return
     */
    public Header[] getAllHeaders();

    /**
     * 获取对应的头部信息
     * @param name 头部的名字
     * @return 对应的头部信息
     */
    public Header getHeader(String name);

    /**
     * 获取回应的状态信息
     * @return
     */
    public StatusLine getStatusLine();

    /**
     * 获取ProtocolVersion
     * @return
     */
    public ProtocolVersion getProtocolVersion();


}
