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

import org.apache.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by andrew on 15-1-31.
 */
public class HttpResponse implements Response{
    protected CrawlDatum datum;
    protected Header[] headers;
    protected StatusLine statusLine;
    protected HttpEntity entity;
    protected ProtocolVersion version;

    public HttpResponse(CrawlDatum datum) {
        this.datum = datum;
    }

    @Override
    public InputStream getContent() throws IOException {
        return entity.getContent();
    }

    @Override
    public Header[] getAllHeades() {
        return headers;
    }

    @Override
    public Header[] getHeader(String name) {
        List<Header> list = new LinkedList<Header>();
        for (Header header : headers){
            if (header.equals(name)){
                list.add(header);
            }
        }
        return (Header[])list.toArray();
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    @Override
    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public HttpEntity getEntity() {
        return entity;
    }

    public void setEntity(HttpEntity entity) {
        this.entity = entity;
    }

    public ProtocolVersion getVersion() {
        return version;
    }

    public void setVersion(ProtocolVersion version) {
        this.version = version;
    }

    public CrawlDatum getDatum() {
        return datum;
    }
}
