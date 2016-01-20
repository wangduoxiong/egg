package edu.xiyou.andrew.egg.utils;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * Created by zhuoxiong on 2016/1/15.
 */
public class UrlUtils {

    public static String acquireDomain(String url){
        String domain = null;
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
