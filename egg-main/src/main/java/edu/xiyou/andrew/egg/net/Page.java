package edu.xiyou.andrew.egg.net;

import com.google.common.collect.Maps;
import edu.xiyou.andrew.egg.model.CrawlDatum;
import edu.xiyou.andrew.egg.model.Site;
import edu.xiyou.andrew.egg.parser.HtmlParser;
import edu.xiyou.andrew.egg.parser.Parser;
import edu.xiyou.andrew.egg.utils.UrlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.StatusLine;

import java.util.List;
import java.util.Map;

/**
 *  获取网页内容后，将结果用本类保存
 * Created by andrew on 16-1-10.
 */
public class Page{
    private static final String DEFAULT_CHARSET = "UTF-8";

    private Site site;
    private CrawlDatum request;
    private Header[] headers;
    private String charSetName;
    private StatusLine statusLine;
    private byte[] rawText;
    private HtmlParser htmlParser = new HtmlParser(this);
    private Map<String, String> results = Maps.newHashMap();
    private List<String> allLinksList;
    private boolean needSave = true;                        //是否保存本页

    public CrawlDatum getRequest() {
        return request;
    }

    public Page setRequest(CrawlDatum request) {
        this.request = request;
        return this;
    }

    public String getUrl() {
        if ((request == null) || StringUtils.isBlank(request.getUrl())){
            return null;
        }
        return request.getUrl();
    }

    public HtmlParser getHtmlParser() {
        return htmlParser;
    }

    public Page setHtmlParser(HtmlParser htmlParser) {
        this.htmlParser = htmlParser;
        return this;
    }

    public byte[] getContent() {
        return rawText;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public Header getHeader(String name) {

        for (Header header : headers) {
            if (header.getName().equals(name));
        }
        return null;
    }

    public Page setHeaders(Header[] headers) {
        this.headers = headers;
        return this;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Page setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
        return this;
    }

    public byte[] getRawText() {
        return rawText;
    }

    public Page setRawText(byte[] rawText) {
        this.rawText = rawText;
        return this;
    }

    public Map<String, String> getResults() {
        return results;
    }

    public String getResult(String key){
        if (StringUtils.isBlank(key)){
            return "";
        }
        return results.get(key);
    }

    public Page setResults(Map<String, String> results) {
        if (MapUtils.isNotEmpty(results)){
            this.results.putAll(results);
        }
        return this;
    }

    public Page addResults(String key, String value){
        if (StringUtils.isNotBlank(key)){
            results.put(key, value);
        }
        return this;
    }

    public boolean isNeedSave() {
        return needSave;
    }

    public Page setNeedSave(boolean needSave) {
        this.needSave = needSave;
        return this;
    }

    public List<String> getAllLinksList() {
        if (CollectionUtils.isEmpty(allLinksList)){
            allLinksList = htmlParser.getAllLink().all();
        }
        return allLinksList;
    }

    public Page setAllLinksList(List<String> allLinksList) {
        this.allLinksList = allLinksList;
        return this;
    }

    public Site getSite() {
        return site;
    }

    public Page setSite(Site site) {
        this.site = site;
        return this;
    }

    public String getCharSetName() {
        if (StringUtils.isBlank(charSetName)){
            charSetName = this.getSite().getCharset();
            if (StringUtils.isBlank(charSetName)) {
                if (this.getRawText() == null){
                    charSetName = DEFAULT_CHARSET;
                }else {
                    this.getSite().setCharset(UrlUtils.guessEncoding(this.getRawText()));
                }
                charSetName = this.getSite().getCharset();
            }
        }
        return charSetName;
    }

    public Page setCharSetName(String charSetName) {
        this.charSetName = charSetName;
        return this;
    }
}
