package edu.xiyou.andrew.egg.utils;

import org.apache.commons.lang3.StringUtils;
import org.mozilla.universalchardet.UniversalDetector;

import java.net.MalformedURLException;
import java.net.URL;
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

    /**
     * canonicalizeUrl
     * <p/>
     * Borrowed from Jsoup.
     *
     * @param url
     * @param refer
     * @return canonicalizeUrl
     */
    public static String canonicalizeUrl(String url, String refer) {
        URL base;
        try {
            try {
                base = new URL(refer);
            } catch (MalformedURLException e) {
                // the base is unsuitable, but the attribute may be abs on its own, so try that
                URL abs = new URL(refer);
                return abs.toExternalForm();
            }
            // workaround: java resolves '//path/file + ?foo' to '//path/?foo', not '//path/file?foo' as desired
            if (url.startsWith("?"))
                url = base.getPath() + url;
            URL abs = new URL(base, url);
            return encodeIllegalCharacterInUrl(abs.toExternalForm());
        } catch (MalformedURLException e) {
            return "";
        }
    }

    /**
     *
     * @param url
     * @return
     */
    public static String encodeIllegalCharacterInUrl(String url) {
        //TODO more charator support
        return url.replace(" ", "%20");
    }
}
