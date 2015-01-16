package edu.xiyou.andrew.Egg.utils;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * Created by andrew on 15-1-16.
 */
public class CharsetDetector {

    /**
     * 根据内容猜测字符集,若检测失败统一返回utf8
     * @param content 内容
     * @return 字符集
     */
    public  static String guessEncoding(byte[] content){
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
