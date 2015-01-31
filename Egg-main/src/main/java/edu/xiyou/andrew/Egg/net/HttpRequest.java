package edu.xiyou.andrew.Egg.net;

import org.apache.http.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;

/**
 * Created by andrew on 15-1-31.
 */
public class HttpRequest implements Request{
    protected CrawlDatum datum;

    public HttpRequest(CrawlDatum datum){
        this.datum = datum;
    }

    @Override
    public CloseableHttpResponse getResponse() throws Exception {
        return null;
    }

    public CrawlDatum getCrawlDatum(){
        return datum;
    }
}
