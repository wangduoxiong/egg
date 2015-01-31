package edu.xiyou.andrew.Egg.net;

import org.apache.http.Header;

import java.io.IOException;
import java.io.InputStream;

/**
 * 请求之后返回的结果
 * Created by andrew on 15-1-31.
 */
public interface Response {
    /**
     * 取出Response里的内容
     * @return
     */
    public InputStream getContent() throws Exception;

    public Header[] getAllHeades();

    public Header[] getHeader(String name);
}
