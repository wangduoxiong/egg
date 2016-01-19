package edu.xiyou.andrew.parser.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 可以自动匹配出正文字段和标题
 * range 是一个阙值，假如匹配到的正文段没有完整匹配到，则可以调大， 假如噪点（非正文）太多，可以调小
 * 具体算法思想：http://wenku.baidu.com/link?url=U5r19bXhfM65kwWYXMmxcyTe8z_70IiCW7E9Pr9IxiMXVCYBhqxjjXSie7ehbnRIga20jAHBTOjcSXsZc_YeR-mqTtD7vDD_ID_jLx3KdNO
 * Created by duoxiongwang on 15-8-21.
 */
public class MainContentFilter {
    private static final String regexStyle = "<style[^>]*?>[\\s\\S]*?<\\/style>";;
    private static final String regexScript = "<script[^>]*?>[\\s\\S<>]*?<\\/script>";
    private static final String regexHtml = "<[^>]+>";

    private int range = 5;

    public String getTitle(String content){
        Matcher matcher = Pattern.compile("<title[^>]*?>[\\s\\S]*?</title>").matcher(content);
        StringBuilder titleWithHtml = new StringBuilder();
        for (int i = 0; i <= matcher.groupCount(); i++){
            titleWithHtml.append(matcher.group(i));
        }
        if (StringUtils.isNotBlank(titleWithHtml.toString())) {
            return Pattern.compile(regexHtml).matcher(titleWithHtml).replaceAll("");
        }
        return "";
    }

    private String getContentWithHtml(File file) throws IOException {
        byte[] contentByte = FileUtils.readFileToByteArray(file);
        return new String(contentByte);
    }

    public String contentDisodge(String content){
        String disodgeStyle = Pattern.compile(regexStyle).matcher(content).replaceAll("");
        String disodgeScript = Pattern.compile(regexScript).matcher(disodgeStyle).replaceAll("");
        return Pattern.compile(regexHtml).matcher(disodgeScript).replaceAll("");
    }


    public String getContent(File file){
        String content = contentDisodge(getContent(new File("csdn.html")));
        String[] lineArr = content.split("\n");
        ArrayList<Integer> lenghtList = Lists.newArrayListWithCapacity(1000);
        for (String line : lineArr){
            lenghtList.add(line.trim().length());
        }

        Map<Integer, Integer> indexMap = Maps.newHashMap();
        int maxLenghtIndexStart = 0;
        int maxLenght = 0, maxIndex = 0;
        int indexListSizeSubFive = lenghtList.size() - range;

        for (int i = range; i < indexListSizeSubFive; i++){
            if (maxLenght < lenghtList.get(i)){
                maxLenght = lenghtList.get(i);
                maxIndex = i;
            }
            int flag = isUseFul(lenghtList, i, range);

            if (flag == 1){
                indexMap.put(maxLenghtIndexStart, i);
            }else if (flag == 2){
                maxLenghtIndexStart = i;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry: indexMap.entrySet()){
            if ((entry.getKey() <= maxIndex) && (entry.getValue() >= maxIndex)){
                for (int i = entry.getKey(); i <= entry.getValue(); i++){
                    if (lineArr[i].trim().length() > 2)
                        builder.append(lineArr[i].trim().replaceAll(";;;", "").replaceAll(";;", "")).append("\n");
                }
            }
        }
        return builder.toString();
    }


    //    int flag = 0;       // 0,前range和后range行全部是空格  1.前range行是空格  2.后range行是空格    3.前后range行都不是空格
    private   int isUseFul(ArrayList<Integer> list,final int index, int range){
        int flag = 0;
        for (int i = 1; i <= range; i++){
            if ((flag == 2) && (list.get(index - i) != 0)){
                return 3;
            }else if((flag == 1) && (list.get(index + i) != 0)){
                return 3;
            }else if (list.get(index + i) != 0 ){
                flag = 2;
            }else if(list.get(index - i) != 0){
                flag = 1;
            }

        }
        return flag;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
}
