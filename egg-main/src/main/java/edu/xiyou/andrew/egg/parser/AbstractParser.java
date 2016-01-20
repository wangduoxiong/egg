package edu.xiyou.andrew.egg.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.xiyou.andrew.egg.net.Page;
import edu.xiyou.andrew.egg.utils.RegexRule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
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
        this.targetListIfNotEmptyClear();
        if (pageNullReturn()) {
            return this;
        }

        String charSetName =page.getCharSetName();
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
        this.targetListIfNotEmptyClear();
        if (urlCollection != null) {
            targetList.addAll(urlCollection);
        }
        return this;
    }

    @Override
    public Parser regex(String regexStr) {
        this.targetListIfNotEmptyClear();
        if (checkCurrentCondition(regexStr)){
            return this;
        }
        Pattern pattern = Pattern.compile(regexStr);
        Matcher matcher;
        try {
            matcher = pattern.matcher(new String(page.getRawText(), page.getCharSetName()));
        } catch (UnsupportedEncodingException e) {
            LOGGER.debug("method=regex, Exception={}", e);
            matcher = pattern.matcher(new String(page.getRawText()));
        }
        while (matcher.find()){
            if (StringUtils.isNotEmpty(matcher.group())){
                targetList.add(matcher.group());
            }
        }
        return this;
    }

    @Override
    public Parser regexLinks(RegexRule regex){
        this.targetListIfNotEmptyClear();
        if (checkCurrentCondition(regex) || (regex.getPositive().size() <= 0)){
            return this;
        }
        List<String> tmpList;
        tmpList = getAllLink().all();
        targetList.clear();
        if ((null == tmpList) || (tmpList.size() == 0)){
            return this;
        }
        for (String href : tmpList){
            if (StringUtils.isBlank(href)){
                continue;
            }
            if (regex.satisfy(href)){
                targetList.add(href);
            }
        }
        return this;
    }

    @Override
    public Parser xpath(String xpathStr) {
        this.targetListIfNotEmptyClear();
        if (checkCurrentCondition(xpathStr)){
            return this;
        }
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode tagNode;
        try {
             tagNode = htmlCleaner.clean(new String(page.getRawText(), page.getCharSetName()));
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("method=xpath, Excpetion={}", e);
            tagNode = htmlCleaner.clean(new String(page.getRawText()));
        }
        Object[] resultArray = new Object[0];
        try {
             resultArray = tagNode.evaluateXPath(xpathStr);
        } catch (XPatherException e) {
            LOGGER.error("method=xpath, Exception={}, xpath={}", e, xpathStr);

        }
        targetList.addAll(Arrays.<String>asList((String[]) resultArray));
        return this;
    }

    @Override
    public Parser css(String cssStr) {
        checkCurrentCondition(cssStr);
        targetListIfNotEmptyClear();
        Document document;
        try {
             document = Jsoup.parse(new String(page.getRawText(), page.getCharSetName()));
        } catch (UnsupportedEncodingException e) {
            document = Jsoup.parse(new String(page.getRawText()));
        }
        Elements elements = document.select(cssStr);
        for (Element element : elements){
            targetList.add(element.text());
        }
        return this;
    }

    @Override
    public Parser parse(ParserAble parseAble) {
        checkCurrentCondition(parseAble);
        targetListIfNotEmptyClear();
        targetList.addAll(parseAble.parse());
        return this;
    }

    @Override
    public List<String> all(){
        return targetList;
    }

    public Page getPage() {
        return page;
    }

    @Override
    public Parser setPage(Page page) {
        this.page = page;
        return this;
    }

    @Override
    public void clear(){
        targetList.clear();
    }

    @Override
    public String get(){
        if (CollectionUtils.isEmpty(targetList)){
            return "";
        }
        return targetList.get(0);
    }

    public List<String> getTargetList() {
        return targetList;
    }

    public AbstractParser setTargetList(List<String> targetList) {
        this.targetList = targetList;
        return this;
    }

    private void targetListIfNotEmptyClear(){
        if (targetList.size() > 0) {
            targetList.clear();
        }
    }
    private <E>boolean checkCurrentCondition(E element){
        if (pageNullReturn())
            return true;
        if (element instanceof String){
            return StringUtils.isBlank((CharSequence) element);
        }
        return element == null;
    }
}
