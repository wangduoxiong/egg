package edu.xiyou.andrew.Egg.net;

import edu.xiyou.andrew.Egg.PageProcessor.pageinfo.*;
import edu.xiyou.andrew.Egg.utils.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
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
public class HttpRequest implements Request{
    private Logger logger =  LoggerFactory.getLogger(getClass());

    private HttpRequestHeaders headers;
    private Proxy proxy;

    private HttpURLConnection getConnectionInstance(URL _URL) throws IOException {
        HttpURLConnection con = null;
        if (proxy == null){
            con = (HttpURLConnection) _URL.openConnection();
        }else {
            con = (HttpURLConnection) _URL.openConnection(proxy);
        }

        if (headers == null){
            return con;
        }

        if (headers.getCookie() != null){
            con.setRequestProperty(HttpHeaderMetadata.COOKIE, headers.getCookie());
        }

        if (headers.getUserAgent() != null){
            con.setRequestProperty(HttpHeaderMetadata.USER_AGENT, headers.getUserAgent());
        }

        if (headers.getCacheControl() != null){
            con.setRequestProperty(HttpHeaderMetadata.CACHE_CONTROL, headers.getCacheControl());
        }

        if (headers.getConnection() != null){
            con.setRequestProperty(HttpHeaderMetadata.CONNECTION, headers.getConnection());
        }

        if (headers.getHost() != null){
            con.setRequestProperty(HttpHeaderMetadata.HOST, headers.getHost());
        }

        return con;
    }

    @Override
    public HttpResponse getResponse(String url) throws Exception {
        HttpResponse response = new HttpResponse(url);
        HttpURLConnection con = null;
        URL _URL = new URL(url);

        for (int i = 0; i < Config.retry; i ++){
            con = getConnectionInstance(_URL);
            con.setInstanceFollowRedirects(false);
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setConnectTimeout(500);
            con.setReadTimeout(1000);

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK || (i == 2)){
                response.setStatusCode(con.getResponseCode());
                response.setHeaders(con.getHeaderFields());
                response.setFetchTime(System.currentTimeMillis());

                InputStream is = con.getInputStream();
                byte[] buf = new byte[4096];
                int read = 0;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((read = is.read(buf)) != -1){
                    baos.write(buf, 0, read);
                }

                is.close();
                baos.close();
                response.setContent(baos.toByteArray());
                return response;
            }

        }
        return null;
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

    public static void main(String[] args) throws Exception {
        HttpRequest request = new HttpRequest();
        HttpResponse response = request.getResponse("http://dict.youdao.com/search?le=eng&q=%20infinite&keyfrom=dict.top");
        //System.out.println(Jsoup.parse(new String(response.getContent(), response.getCharset())));
        Document document = Jsoup.parse(new String(response.getContent(), response.getCharset()));
        System.out.println(LinkFilter.getLinks(document));
//        Elements elements = document.getElementsByTag("link");
//        for (Element element : elements){
//            System.out.println("text: " + element.attr("rel") + "\nurl: " + element.attr("abs:href"));
//        }
    }
}
