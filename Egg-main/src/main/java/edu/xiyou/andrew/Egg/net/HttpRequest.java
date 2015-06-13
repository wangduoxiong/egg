package edu.xiyou.andrew.Egg.net;

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


import edu.xiyou.andrew.Egg.parser.CrawlDatum;
import edu.xiyou.andrew.Egg.parser.Html;
import edu.xiyou.andrew.Egg.parser.Response;
import edu.xiyou.andrew.Egg.utils.Config;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;

/**
 * Created by andrew on 15-5-20.
 */
public class HttpRequest implements Request{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    {
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(3);
    }

    private HttpClient client = null;
    private ResponseHandler<Response> handler = new ResponseHandler() {
        @Override
        public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            HttpEntity entity = response.getEntity();

            if (entity == null){
                throw new ClientProtocolException("entry is null");
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                if (charset == null){
                    charset = Consts.ISO_8859_1;
                }
                InputStreamReader isr = new InputStreamReader(entity.getContent(), charset);
                BufferedReader br = new BufferedReader(isr);
                StringBuffer sb = new StringBuffer();
                char[] buffer = new char[2048];
                while (br.read(buffer) != -1){
                    sb.append(buffer);
                }
                Html html = new Html();
                html.setContent(sb.toString().getBytes());
                html.setStatusLine(response.getStatusLine());
                html.setHeaders(response.getAllHeaders());
                html.setProtocolVersion(response.getProtocolVersion());
                return html;
            }else {
                logger.info("Response Code is :" + response.getStatusLine().getStatusCode());
            }

            return null;
        }
    };

    public HttpRequest(){
        RequestConfig requestConfig = RequestConfig.custom().
                setConnectionRequestTimeout(Config.TTIME_OUT).
                setSocketTimeout(Config.TTIME_OUT).build();
        client = HttpClients.custom().
                setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36").
                setRetryHandler(new DefaultHttpRequestRetryHandler(Config.RETRY, true)).
                setRedirectStrategy(new LaxRedirectStrategy()).
                setDefaultRequestConfig(requestConfig).
                setConnectionManager(connectionManager).build();
    }

    private boolean checkCrawlDatum(CrawlDatum datum){
        if (datum != null)
            return true;
        return false;
    }


    @Override
    public Response getResponse(CrawlDatum datum) throws IllegalArgumentException {
        if (!checkCrawlDatum(datum)){
            logger.info("datum is null");
            throw new IllegalArgumentException("illegal format");
        }
        String url = datum.getUrl();

        HttpGet httpGet = new HttpGet(url);
        try {
             Response obj = client.execute(httpGet, handler);
            if (obj != null){
                ((Html)obj).setUrl(url);
                return obj;
            }
        } catch (ClientProtocolException e) {
            logger.info("ClientProtocolException  ");
            e.printStackTrace();
        } catch (SocketTimeoutException e){
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.info("IOException   ");
            e.printStackTrace();
        }

        return null;
    }

//    public static void main(String[] args) {
//        HttpRequest request = new HttpRequest();
//        CrawlDatum datum = new CrawlDatum("http://www.importnew.com/all-posts");
//
//        System.out.println(new String(request.getResponse(datum).getContent()));
//    }
}
