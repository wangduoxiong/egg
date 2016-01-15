package edu.xiyou.andrew.Egg.parser;

import edu.xiyou.andrew.Egg.model.Page;

import java.util.List;

/**
 * 解析器接口，用作解析 {@link edu.xiyou.andrew.Egg.model.Page}.<br>
 * Created by andrew on 16-1-11.
 */
public interface Parser {
    /**
     * 获取所有的{@link edu.xiyou.andrew.Egg.model.Page#rawText} <br>
     * 的链接
     * @param page
     * @return
     */
    List<String> getAllLink(Page page);


}
