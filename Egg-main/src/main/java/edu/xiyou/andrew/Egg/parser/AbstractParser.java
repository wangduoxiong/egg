package edu.xiyou.andrew.Egg.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.xiyou.andrew.Egg.model.Page;
import edu.xiyou.andrew.Egg.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhuoxiong on 2016/1/15.
 */
public abstract class AbstractParser implements Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractParser.class);

    private Page page;
    private List<String> targetList = Lists.newArrayList();

    private final static Pattern HREF_PATTERN = Pattern.compile("(<a[^>]*href=)['\"]([^'\">]*)['\"]", Pattern.CASE_INSENSITIVE);

    /**
     * 判断page是否为null，为null返回true, 否则false
     *
     * @return 为null返回true, 否则false
     */
    private boolean pageNullReturn() {
        if (page == null) {
            LOGGER.debug("Page is null, cann't parse");
            return true;
        }

        if (page.getRawText() == null || ((page.getRawText()).length == 0)) {
            LOGGER.debug("Page RawText is null, can't parse, url={}", page.getRequest() == null ? "" : page.getRequest().getUrl());
            return true;
        }
        return false;
    }

    @Override
    public Parser getAllLink(){
        return getAllLink(true);
    }

    /**
     * 获取所有链接
     * @param unique 是否过过滤本页重复的链接 为true将过滤重复,反之为false
     * @return parser
     */
    public Parser getAllLink(boolean unique) {
        if (pageNullReturn()) {
            return this;
        }

        String charSetName = page.getSite().getCharset();
        if (StringUtils.isBlank(charSetName)) {
            page.getSite().setCharset(UrlUtils.guessEncoding(page.getRawText()));
            charSetName = page.getSite().getCharset();
        }
        Matcher matcher;
        try {
            matcher = HREF_PATTERN.matcher(new String(page.getRawText(), charSetName));
        } catch (UnsupportedEncodingException e) {
            LOGGER.debug("method=getAllLink, Exception={}", e);
            matcher = HREF_PATTERN.matcher(new String(page.getRawText()));
        }
        Collection<String> urlCollection = null;
        if (matcher.find()){
            if (unique){
                urlCollection = Sets.newHashSet();
                urlCollection.add(matcher.group(2));
            }else {
                urlCollection = Lists.newArrayList();
                urlCollection.add(matcher.group(2));
            }
        }
        if (targetList.size() > 0) {
            targetList.clear();
        }
        if (urlCollection != null) {
            targetList.addAll(urlCollection);
        }
        return this;
    }

    @Override
    public Parser regex(String regexStr) {
        if (StringUtils.isBlank(regexStr)) {
            return this;
        }
        return this;
    }

    @Override
    public Parser xpath(String xpathStr) {
        return null;
    }

    @Override
    public Parser css(String cssStr) {
        return null;
    }

    @Override
    public Parser parse(ParserAble parseAble) {
        return null;
    }

    public Page getPage() {
        return page;
    }

    public AbstractParser setPage(Page page) {
        this.page = page;
        return this;
    }

    public List<String> getTargetList() {
        return targetList;
    }

    public AbstractParser setTargetList(List<String> targetList) {
        this.targetList = targetList;
        return this;
    }
}
