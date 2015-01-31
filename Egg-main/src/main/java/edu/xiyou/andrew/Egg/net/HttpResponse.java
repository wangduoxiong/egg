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
