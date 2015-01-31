package edu.xiyou.andrew.Egg.net;

import org.apache.http.*;


/**
 * 进行链接请求的基础接口
 * Created by andrew on 15-1-31.
 */
public interface Request {

    /**
     * @return
     */
    public abstract Object getResponse() throws Exception;
}
