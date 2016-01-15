package edu.xiyou.andrew.Egg.utils;

/*
 * Copyright (c) 2015 Andrew-Wang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *  正则匹配的工具类
 * Created by andrew on 15-6-7.
 */

@Deprecated
public class RegexRule {
    private List<String> positive;
    private List<String> negative;

    public RegexRule(List<String> positive, List<String> negative){
        this.positive = positive;
        this.negative = negative;
    }

    public RegexRule(){
        positive = new ArrayList<String>();
        negative = new ArrayList<String>();
    }

    /**
     * 正则集合：
     *     1.负正则： 首字母是 '-'
     *     2.正正则： 首字母不是'+'
     * @param list
     */
    public RegexRule(List<String> list){
        for (String rule : list){
            addRule(rule);
        }
    }

    /**
     * 添加规则
     * @param rule
     */
    private void addRule(String rule) {
        if (rule == null){
            return;
        }

        char pn = rule.charAt(0);
        if (pn == '+'){
            positive.add(rule.substring(1));
        }else if (pn == '-'){
            negative.add(rule.substring(1));
        }else {
            positive.add(rule);
        }
    }

    public boolean isEmpty(){
        return positive.isEmpty();
    }

    /**
     * 添加positive规则
     * @param rule
     */
    public void addPositive(String rule){
        positive.add(rule);
    }

    public void addPositive(List<String> list){
        positive.addAll(list);
    }

    /**
     * 添加负正则
     * @param rule
     */
    public void addNegative(String rule){
        negative.add(rule);
    }

    /**
     * 添加负正则
     * @param list
     */
    public void addNegative(List<String> list){
        negative.addAll(list);
    }

    /**
     * 对str进行正则， 符合条件返回true,不符合返回false
     * @param url 待验证的字符串
     * @return
     */
    public boolean satisfy(String url){
        for (String rule : negative){
            if (Pattern.matches(rule, url)) {
                return false;
            }
        }

        for (String rule : positive){
            if (Pattern.matches(rule, url)){
                return true;
            }
        }
        return false;
    }

    public List<String> getPositive(){
        return positive;
    }

    public List<String> getNegative(){
        return negative;
    }
}
