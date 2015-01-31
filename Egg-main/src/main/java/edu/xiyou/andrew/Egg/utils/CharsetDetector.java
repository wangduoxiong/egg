package edu.xiyou.andrew.Egg.utils;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * 猜测字符集
 * Created by andrew on 15-1-31.
 */
public class CharsetDetector {
    public static String guessEncoding(byte[] content) {
        final String DEFAULT_ENCODING = "UTF-8";
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(content, 0, content.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        detector.reset();
        if (encoding == null){
            encoding = DEFAULT_ENCODING;
        }
        return encoding;
    }
}
