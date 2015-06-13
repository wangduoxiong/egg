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

import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andrew on 15-6-6.
 */
public class Html implements Response {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String url;
    private byte[] content;
    private Header[] headers;
    private StatusLine statusLine;
    private ProtocolVersion protocolVersion;

    public Html(){

    }

    public Html(String url, String content) {
        this(url, content.getBytes());
    }

    public Html(String url, byte[] content){
        this.url = url;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public byte[] getContent() {
        return content;
    }

    @Override
    public Header[] getAllHeaders() {
        return headers;
    }

    @Override
    public Header getHeader(String name) {
        if (name.length() < 0 || name == null ){
            logger.info("the name of get header is null");
            throw new IllegalArgumentException("the name of get header is null");
        }

        for (Header header : headers){
            if (header.getName().equals(name))
                return header;
        }
        return null;
    }

    @Override
    public StatusLine getStatusLine() {
        return statusLine;
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setProtocolVersion(ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl(String url){
        return url;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

}
