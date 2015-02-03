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
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by andrew on 15-1-31.
 */
public class HttpResponse implements Response{

    protected CloseableHttpResponse response;
    protected HttpEntity entity;

    public HttpResponse(CloseableHttpResponse response){
        this.response = response;
        entity = response.getEntity();
    }

    @Override
    public InputStream getContent() throws IOException {
        return entity.getContent();
    }

    @Override
    public Header[] getAllHeades() {
        return response.getAllHeaders();
    }

    @Override
    public Header[] getHeader(String name) {
        return response.getHeaders(name);
    }

    public void close(){
        if (response != null){
            try {
                response.close();
            } catch (IOException e) {

            }finally {
                response = null;
            }
        }
    }
}
