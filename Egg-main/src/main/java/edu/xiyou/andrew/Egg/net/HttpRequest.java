package edu.xiyou.andrew.Egg.net;

import edu.xiyou.andrew.Egg.parser.Html;
import edu.xiyou.andrew.Egg.parser.Response;
import edu.xiyou.andrew.Egg.utils.Config;
import edu.xiyou.andrew.Egg.net.meta.HttpMeta;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by duoxiongwang on 15-8-17.
 */
public class HttpRequest implements Request{
    private final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private HttpRequestGenerator httpRequestGenerator;

    {
        httpRequestGenerator = new HttpRequestGenerator();
    }

    private synchronized CloseableHttpClient generateRequest(CrawlDatum.Site site){
        logger.info("generatorClient start....");
        if ((site == null) || (StringUtils.isBlank(site.getUrl()))){
            logger.info("site is not conformance to requirements");
            return httpRequestGenerator.generateClient(site);
        }

        return httpRequestGenerator.generateClient(site);
    }

    public HttpRequest setPoolSize(int poolSize){
        httpRequestGenerator.setMaxTotal(poolSize);
        return this;
    }

    private boolean siteIsBlank(CrawlDatum.Site site){
        return (site == null) || StringUtils.isBlank(site.getUrl());
    }

    @Override
    public Response getResponse(CrawlDatum datum) throws Exception {
        if ((datum == null) || siteIsBlank(datum.getSite())){
            return null;
        }
        HttpResponse response = null;
        try {
            CrawlDatum.Site site = datum.getSite();
            CloseableHttpClient httpClient = generateRequest(site);
            HttpUriRequest httpUriRequest = getHttpUriRequest(datum, new HashMap<String, String>());
            response = httpClient.execute(httpUriRequest);
        }catch (Exception e){
            logger.error("op=getResponse, exception " + e.getMessage());
        }
        return handlerResponse(datum, response);
    }

    private Response handlerResponse(CrawlDatum datum,HttpResponse response) {
        Html html = new Html();
        if (response == null){
            return html;
        }
        try {
            html.setUrl(datum.getSite().getUrl());
            html.setContent(IOUtils.toByteArray(response.getEntity().getContent()));
            html.setHeaders(response.getAllHeaders());
            html.setProtocolVersion(response.getProtocolVersion());
            html.setStatusLine(response.getStatusLine());
        } catch (IOException e) {
            html.setContent("".getBytes());
            logger.error("response to html getContent error" + e + "\n url: " + datum.getSite().getUrl());
        }finally {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                logger.error("op=handlerResponse Exception: " + e + "\n url: " + datum.getSite().getUrl());
            }
        }
        return html;
    }

    private HttpUriRequest getHttpUriRequest(CrawlDatum datum, Map<String, String> headers){
        RequestBuilder requestBuilder = selectRequestMethod(datum).setUri(datum.getSite().getUrl());
        if (MapUtils.isEmpty(headers)){
            datum.site.setHeaders(headers);
        }
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().
                setConnectionRequestTimeout(Config.TTIME_OUT).
                setConnectTimeout(Config.TTIME_OUT).
                setSocketTimeout(Config.TTIME_OUT).
                setCookieSpec(CookieSpecs.BEST_MATCH);

        requestBuilder.setConfig(requestConfigBuilder.build());
        return requestBuilder.build();
    }

    private RequestBuilder selectRequestMethod(CrawlDatum datum){
        String method = datum.method;
        if ((method == null) || (method.equalsIgnoreCase(HttpMeta.Method.GET))) {
            return RequestBuilder.get();
        }else if(method.equalsIgnoreCase(HttpMeta.Method.POST)) {
            RequestBuilder builder = RequestBuilder.post();
            if (MapUtils.isNotEmpty(datum.getSite().getWireSign())) {
                NameValuePair[] nameValuePairs = (NameValuePair[]) datum.getSite().getWireSign().get("nameValuePair");
                if (nameValuePairs.length > 0) {
                    builder.addParameters(nameValuePairs);
                }
                return builder;
            }
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
