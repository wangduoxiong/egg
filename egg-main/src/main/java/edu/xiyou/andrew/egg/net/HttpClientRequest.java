package edu.xiyou.andrew.egg.net;

import com.google.common.collect.ImmutableMap;
import edu.xiyou.andrew.egg.model.CrawlDatum;
import edu.xiyou.andrew.egg.model.Site;
import edu.xiyou.andrew.egg.parser.Html;
import edu.xiyou.andrew.egg.utils.Config;
import edu.xiyou.andrew.egg.utils.HttpMeta;
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
public class HttpClientRequest implements Request {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientRequest.class);

    private final ImmutableMap<String, RequestBuilder> methodSelectMap = ImmutableMap.<String, RequestBuilder>builder()
            .put(HttpMeta.Method.GET, RequestBuilder.get())
            .put(HttpMeta.Method.POST, RequestBuilder.post())
            .put(HttpMeta.Method.PUT, RequestBuilder.put())
            .put(HttpMeta.Method.DELETE, RequestBuilder.delete())
            .put(HttpMeta.Method.HEAD, RequestBuilder.head())
            .put(HttpMeta.Method.TRACE, RequestBuilder.trace()).build();

    private CloseableHttpClient httpClient;

    protected HttpClientRequest(CloseableHttpClient client) {
        this.httpClient = client;
    }

    @Override
    public Page getResponse(CrawlDatum datum, Site site) throws Exception {
        if ((datum == null) || StringUtils.isBlank(datum.getUrl())) {
            return new Page();
        }
        HttpUriRequest httpUriRequest = getHttpUriRequest(datum, site);
        HttpResponse response = httpClient.execute(httpUriRequest);
        return handlerResponse(datum, response);
    }

    private Page handlerResponse(CrawlDatum datum, HttpResponse response) {
        Page page = new Page();
        if (response == null) {
            return page;
        }
        try {
            page.setRequest(datum);
            page.setRawText(IOUtils.toByteArray(response.getEntity().getContent()));
            page.setHeaders(response.getAllHeaders());
            page.setStatusLine(response.getStatusLine());
        } catch (IOException e) {
            page.setRawText("".getBytes());
            LOGGER.error("method=handlerResponse, Exception={}, url={}", e, datum.getUrl());
        } finally {
            try {
                EntityUtils.consume(response.getEntity());
            } catch (IOException e) {
                LOGGER.error("method=handlerResponse, Exception={}, url={}", e, datum.getUrl());
            }
        }
        return page;
    }

    private HttpUriRequest getHttpUriRequest(CrawlDatum datum, Site site) {
        RequestBuilder requestBuilder = selectRequestMethod(datum).setUri(datum.getUrl());

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().
                setConnectionRequestTimeout(site.getTimeout()).
                setConnectTimeout(site.getTimeout()).
                setSocketTimeout(site.getTimeout()).
                setCookieSpec(CookieSpecs.BEST_MATCH);

        requestBuilder.setConfig(requestConfigBuilder.build());
        return requestBuilder.build();
    }

    private RequestBuilder selectRequestMethod(CrawlDatum datum) {
        RequestBuilder builder = methodSelectMap.get(datum.getMethod());
        if (builder == null) {
            LOGGER.error("method=selectRequestMethod, ill httpmethod={}", datum.getMethod());
            builder = RequestBuilder.get();
        }

        if (HttpMeta.Method.POST.equals(datum.getMethod())) {
            if (MapUtils.isNotEmpty(datum.getExtra())) {
                NameValuePair[] nameValuePairs = (NameValuePair[]) datum.getExtra().get("nameValuePair");
                if (nameValuePairs.length > 0) {
                    builder.addParameters(nameValuePairs);
                }
            }
        }
        return builder;
    }

}
