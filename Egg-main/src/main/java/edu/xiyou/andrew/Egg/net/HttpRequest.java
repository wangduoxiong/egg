package edu.xiyou.andrew.Egg.net;

import edu.xiyou.andrew.Egg.parser.Html;
import edu.xiyou.andrew.Egg.parser.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private HttpUriRequest getHttpUriRequest(){

    }
}
