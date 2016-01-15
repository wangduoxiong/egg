package edu.xiyou.andrew.Egg.model;

import com.google.common.collect.Maps;
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
public class Page extends BaseModel{
    private CrawlDatum request;
    private Header[] headers;
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

    public Header[] getHeaders() {
        return headers;
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

    public List<String> getAllLinksList() {
        return allLinksList;
    }

    public Page setAllLinksList(List<String> allLinksList) {
        this.allLinksList = allLinksList;
        return this;
    }
}
