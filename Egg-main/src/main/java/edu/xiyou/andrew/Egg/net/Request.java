package edu.xiyou.andrew.Egg.net;

import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.Response;

/**
 * Created by andrew on 15-1-18.
 */
public interface Request {
    /**
     * 获取response
     * @param url url地址
     * @return response实例
     * @throws Exception
     */
    public Response getResponse(String url) throws Exception;
}
