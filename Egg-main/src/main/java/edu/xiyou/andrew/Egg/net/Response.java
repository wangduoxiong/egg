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
package edu.xiyou.andrew.Egg.net;

import org.apache.http.Header;
import org.apache.http.StatusLine;

import java.io.InputStream;

/**
 * 请求之后返回的结果
 * Created by andrew on 15-1-31.
 */
public interface Response {
    /**
     * 取出Response里的内容
     * @return
     */
    public InputStream getContent() throws Exception;

    public Header[] getAllHeades();

    public Header[] getHeader(String name);

    public StatusLine getStatusLine();
}
