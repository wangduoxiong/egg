package edu.xiyou.andrew.egg.utils;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.util.regex.Pattern;

/**
 * Created by zhuoxiong on 2016/1/15.
 */
public class UrlUtils {

    private static Pattern patternForProtocal = Pattern.compile("[\\w]+://");

    public static String removeProtocol(String url) {
        return patternForProtocal.matcher(url).replaceAll("");
    }

    public static String acquireDomain(String url){
        String domain = removeProtocol(url);
        int i = StringUtils.indexOf(domain, "/", 1);
        if (i > 0) {
            domain = StringUtils.substring(domain, 0, i);
        }
        return domain;
    }
    private static final String DEFAULT_CHARSET = "UTF-8";
    public static String guessEncoding(byte[] content) {
        String charSet = DEFAULT_CHARSET;

        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(content, 0, content.length);
        if (detector.getDetectedCharset() != null){
            charSet = detector.getDetectedCharset();
        }

        return charSet;
    }

}
