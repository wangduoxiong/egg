package edu.xiyou.andrew.Egg.parser;

import java.util.List;

/**
 * 解析器接口，用作解析 {@link edu.xiyou.andrew.Egg.model.Page}.<br>
 * Created by andrew on 16-1-11.
 */
public interface Parser {
    /**
     * 获取所有的{@link edu.xiyou.andrew.Egg.model.Page#rawText} <br>
     * 的链接
     *
     * @return
     */
    Parser getAllLink();

    /**
     * 根据正则匹配出需要的内容
     *
     * @param regexStr
     * @return
     */
    Parser regex(String regexStr);

    /**
     * 根据正则匹配需要的链接
     *
     * @param regexStr 正则表达式
     * @return
     */
    Parser regexLinks(String regexStr);

    /**
     * 根据Xpath匹配锁需要的内容
     *
     * @param xpathStr
     * @return
     */
    Parser xpath(String xpathStr);

    /**
     * 根据css匹配需要的内容
     *
     * @param cssStr
     * @return
     */
    Parser css(String cssStr);

    /**
     * 解析出需要的字段
     *
     * @param parseAble see{@link ParserAble}
     * @return
     */
    Parser parse(ParserAble parseAble);

    /**
     * 获取当前所有的匹配值
     *
     * @return
     */
    List<String> all();

    /**
     * 清除目标列表
     */
    void clear();
}
