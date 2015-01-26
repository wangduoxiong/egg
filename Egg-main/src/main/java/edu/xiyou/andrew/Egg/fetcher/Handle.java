package edu.xiyou.andrew.Egg.fetcher;

import edu.xiyou.andrew.Egg.generator.Links;
import edu.xiyou.andrew.Egg.net.HttpRequest;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.HttpResponse;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.Link;

import java.util.List;

/**
 * Created by andrew on 15-1-25.
 */
public interface Handle {

    /**
     * 下载成功的处理方式
     * @param response
     */
    public void onSuccess(HttpResponse response);

    /**
     * 下载失败的处理方式
     * @param response
     */
    public void onError(HttpResponse response);

    /**
     * @param response
     * @return
     */
    public Links visitAndGetNextLinks(HttpResponse response);
}
