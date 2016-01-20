package edu.xiyou.andrew.egg.parser;

import edu.xiyou.andrew.egg.net.Page;
import edu.xiyou.andrew.egg.utils.RegexRule;

import java.util.List;

/**
 * 解析器接口，用作解析 {@link Page}.<br>
 * Created by andrew on 16-1-11.
 */
public interface Parser {
    /**
     * 获取所有的{@link Page#rawText} <br>
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
     * @param regex 正则表达式,{@link edu.xiyou.andrew.egg.utils.RegexRule}
     * @return
     */
    Parser regexLinks(RegexRule regex);

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
     * 获取目标列表的首元素
     * @return
     */
    String get();

    /**
     * 清除目标列表
     */
    void clear();

    Parser setPage(Page page);
}
