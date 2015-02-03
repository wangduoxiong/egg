/*
 *Copyright (c) 2015 Andrew-Wang.
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package edu.xiyou.andrew.Egg.net.type;

import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.Egg.net.HttpRequest;
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
