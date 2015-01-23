package edu.xiyou.andrew.Egg.fetcher;

import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.Response;

/**
 * Created by andrew on 15-1-23.
 */
public class AbstractFetcher {

    /**
     * 网页下载成功执行的操作,由用户自定义
     * @param response
     */
    protected void onSuccess(Response response){

    }

    /**
     * 网页下载失败执行的操作，由用户自定义
     * @param response
     */
    protected void onError(Response response){

    }
}
