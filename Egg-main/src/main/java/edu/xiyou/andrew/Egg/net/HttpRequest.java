package edu.xiyou.andrew.Egg.net;

import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.*;
import edu.xiyou.andrew.Egg.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by andrew on 15-1-18.
 */
public class HttpRequest implements Request {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private HttpRequestHeaders headers;
    private Proxy proxy;

    public HttpRequest(){

    }

    public HttpRequest(HttpRequestHeaders headers) {
        this.headers = headers;
    }

    private HttpURLConnection getConnectionInstance(URL _URL) throws IOException {
        HttpURLConnection con = null;
        if (proxy == null) {
            con = (HttpURLConnection) _URL.openConnection();
        } else {
            con = (HttpURLConnection) _URL.openConnection(proxy);
        }

        if (headers == null) {
            return con;
        }

        if (headers.getCookie() != null) {
            con.setRequestProperty(HttpHeaderMetadata.COOKIE, headers.getCookie());
        }

        if (headers.getUserAgent() != null) {
            con.setRequestProperty(HttpHeaderMetadata.USER_AGENT, headers.getUserAgent());
        }

        if (headers.getCacheControl() != null) {
            con.setRequestProperty(HttpHeaderMetadata.CACHE_CONTROL, headers.getCacheControl());
        }

        if (headers.getConnection() != null) {
            con.setRequestProperty(HttpHeaderMetadata.CONNECTION, headers.getConnection());
        }

        if (headers.getHost() != null) {
            con.setRequestProperty(HttpHeaderMetadata.HOST, headers.getHost());
        }

        return con;
    }

    @Override
    public HttpResponse getResponse(String url) throws Exception {
        HttpResponse response = new HttpResponse(url);
        HttpURLConnection con = null;
        URL _URL = new URL(url);

        con = getConnectionInstance(_URL);
        con.setInstanceFollowRedirects(false);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setConnectTimeout(500);
        con.setReadTimeout(1000);

        response.setStatusCode(con.getResponseCode());
        response.setHeaders(con.getHeaderFields());
        response.setFetchTime(System.currentTimeMillis());
        logger.info(HttpRequest.class.getName(), "url :" + url + "  StatusCode: " + con.getResponseCode());

        InputStream is = con.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            byte[] buf = new byte[4096];
            int read = 0;
            while ((read = is.read(buf)) != -1) {
                baos.write(buf, 0, read);
            }
            response.setContent(baos.toByteArray());
        }catch (IOException e){
            response.setContent(null);
            logger.info(HttpRequest.class.getName(), e);
        }finally {
            is.close();
            baos.close();
        }
        return response;
    }

    public HttpRequestHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpRequestHeaders headers) {
        this.headers = headers;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

}
