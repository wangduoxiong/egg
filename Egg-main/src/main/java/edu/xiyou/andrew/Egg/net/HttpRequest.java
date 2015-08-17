package edu.xiyou.andrew.Egg.net;

import edu.xiyou.andrew.Egg.parser.Html;
import edu.xiyou.andrew.Egg.parser.Response;
import edu.xiyou.andrew.Egg.utils.HttpMeta;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.NavigableMap;

/**
 * Created by duoxiongwang on 15-8-17.
 */
public class HttpRequest implements Request{
    private final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private HttpRequestGenerator httpRequestGenerator;

    {
        httpRequestGenerator = new HttpRequestGenerator();
    }

    private CloseableHttpClient generateRequest(CrawlDatum.Site site){
        logger.info("generatorClient start....");
        if ((site == null) || (StringUtils.isBlank(site.getUrl()))){
            logger.info("site is not conformance to requirements");
            return httpRequestGenerator.generateClient(site);
        }

        return httpRequestGenerator.generateClient(site);
    }

    public void setMaxTotal(int maxTotal){
        httpRequestGenerator.setMaxTotal(maxTotal);
    }

    private boolean siteIsBlank(CrawlDatum.Site site){
        return (site == null) || StringUtils.isBlank(site.getUrl());
    }

    private boolean siteIsNotBlank(CrawlDatum.Site site){
        return !siteIsBlank(site);
    }

    @Override
    public Response getResponse(CrawlDatum datum) throws Exception {
        if ((datum == null) || siteIsBlank(datum.getSite())){
            return null;
        }
        CrawlDatum.Site site = datum.getSite();
        Html html = null;

        CloseableHttpClient httpClient = httpRequestGenerator.generateClient(datum.getSite());
        return null;
    }

    private HttpUriRequest getHttpUriRequest(CrawlDatum datum){


        return null;
    }

    private RequestBuilder selectRequestMethod(CrawlDatum datum){
        String method = datum.method;
        if ((method == null) || (method.equalsIgnoreCase(HttpMeta.Method.GET))) {
            return RequestBuilder.get();
        }else if(method.equalsIgnoreCase(HttpMeta.Method.POST)){
            RequestBuilder builder = RequestBuilder.post();
            if (MapUtils.isNotEmpty(datum.getSite().getWireSign())){
                NameValuePair[] nameValuePairs = (NameValuePair[]) datum.getSite().getWireSign().get("nameValuePair");
                if (nameValuePairs.length > 0){
                    builder.addParameters(nameValuePairs);
                }
                return builder;
            }else if (method.equalsIgnoreCase(HttpMeta.Method.HEAD)){
                return RequestBuilder.head();
            }else if (method.equalsIgnoreCase(HttpMeta.Method.DELETE)){
                return RequestBuilder.delete();
            }else if (method.equalsIgnoreCase(HttpMeta.Method.TRACE)){
                return RequestBuilder.trace();
            }
            throw new IllegalArgumentException("illegel Format method: " + method);
        }
    }
}
