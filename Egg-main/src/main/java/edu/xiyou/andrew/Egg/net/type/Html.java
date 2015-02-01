package edu.xiyou.andrew.Egg.net.type;

import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.Egg.net.HttpGetRequest;
import edu.xiyou.andrew.Egg.net.HttpResponse;
import edu.xiyou.andrew.Egg.net.Response;
import edu.xiyou.andrew.Egg.utils.CharsetDetector;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by andrew on 15-1-31.
 */
public final class Html extends HttpResponse{
    protected byte[] content;
    protected String charset;
    protected StatusLine statusLine;
    protected CrawlDatum datum;
    private static final Logger LOG = LoggerFactory.getLogger(Html.class);

    public Html(CrawlDatum datum, CloseableHttpResponse response){
        super(response);
        this.response = response;
        this.datum = datum;
        try {
            HttpEntity entity = response.getEntity();

            content = EntityUtils.toByteArray(entity);
        } catch (IOException e) {
            content = null;
        }
        statusLine = response.getStatusLine();
        charset = CharsetDetector.guessEncoding(content);
    }

    protected byte[] getByteFromInputStream(InputStream inputStream){
        if (inputStream == null){
            return null;
        }
        byte[] buf = new byte[4096];
        int read = -1;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while ((read = inputStream.read(buf)) != -1){
                baos.write(buf, 0, read);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            LOG.info("IOException: " + e);
            return null;
        }finally {
            try {
                baos.close();
                inputStream.close();
            } catch (IOException e) {
            }

        }
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContentAsBytes(){
        return content;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public CrawlDatum getDatum() {
        return datum;
    }

    public CloseableHttpResponse getResponse() {
        return response;
    }

    public void setResponse(CloseableHttpResponse response) {
        this.response = response;
    }

    @Override
    public InputStream getContent() throws IOException {
        return response.getEntity().getContent();
    }

    @Override
    public Header[] getAllHeades() {
        return response.getAllHeaders();
    }

    @Override
    public Header[] getHeader(String name) {
        return response.getHeaders(name);
    }


}
