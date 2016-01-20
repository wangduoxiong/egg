package edu.xiyou.andrew.egg.net;

import com.google.common.collect.Maps;
import edu.xiyou.andrew.egg.model.CrawlDatum;
import edu.xiyou.andrew.egg.model.Site;
import edu.xiyou.andrew.egg.utils.UrlUtils;
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
public class Page implements Response {
    private static final String DEFAULT_CHARSET = "UTF-8";

    private Site site;
    private CrawlDatum request;
    private Header[] headers;
    private String charSetName;
    private StatusLine statusLine;
    private byte[] rawText;
    private Map<String, String> results = Maps.newHashMap();
    private List<String> allLinksList;
    private List<CrawlDatum>targetRequestList;          //下次请求的链接实体
    private boolean needSave = true;                        //是否保存本页

    public CrawlDatum getRequest() {
        return request;
    }

    public Page setRequest(CrawlDatum request) {
        this.request = request;
        return this;
    }

    @Override
    public String getUrl() {
        if ((request == null) || StringUtils.isBlank(request.getUrl())){
            return null;
        }
        return request.getUrl();
    }

    @Override
    public byte[] getContent() {
        return rawText;
    }

    @Override
    public Header[] getHeaders() {
        return headers;
    }

    @Override
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

    @Override
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

    @Override
    public Map<String, String> getResults() {
        return results;
    }

    @Override
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

    @Override
    public List<CrawlDatum> getTargetRequestList() {
        return targetRequestList;
    }

    public Page setTargetRequestList(List<CrawlDatum> targetRequestList) {
        this.targetRequestList = targetRequestList;
        return this;
    }

    public boolean isNeedSave() {
        return needSave;
    }

    public Page setNeedSave(boolean needSave) {
        this.needSave = needSave;
        return this;
    }

    @Override
    public List<String> getAllLinksList() {
        return allLinksList;
    }

    public Page setAllLinksList(List<String> allLinksList) {
        this.allLinksList = allLinksList;
        return this;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public Page setSite(Site site) {
        this.site = site;
        return this;
    }

    @Override
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
