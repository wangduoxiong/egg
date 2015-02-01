package edu.xiyou.andrew.Egg.utils;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.regex.Pattern;

/**
 * 正则工具类
 * 正正则； str 只需满足一条正则即可
 * 负正则： str 满足一条则直接返回false
 * Created by andrew on 15-2-1.
 */
public class RegexRule {
    private List<String> positive = null;
    private List<String> negative = null;

    /**
     * 初始化正则
     * @param positive 正正则列表
     * @param negative 负正则列表
     */
    public RegexRule(List<String> positive, List<String> negative){
        this.positive = positive;
        this.negative = negative;
    }

    /**
     *
     */
    public RegexRule(){
        positive = new ArrayList<String>();
        negative = new ArrayList<String>();
    }

    /**
     * 判断是否为空
     * @return
     */
    public boolean isEmpty(){
        return positive.isEmpty();
    }

    /**
     * 正则集合：
     *     1.负正则： 首字母是 '-'
     *     2.正正则： 首字母不是'+'
     * @param rules
     */
    public RegexRule(List<String> rules){
        for (String rule : rules){
            addRule(rule);
        }
    }

    /**
     * 添加正则
     * @param rule
     */
    public void addRule(String rule) {
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

    /**
     * 添加规则
     * @param rules
     */
    public void addRule(List<String> rules){
        for (String rule : rules){
            addRule(rule);
        }
    }

    /**
     * 添加positive规则
     * @param rule
     */
    public void addPositive(String rule){
        positive.add(rule);
    }

    /**
     *
     * @param rules
     */
    public void addPositive(List<String> rules){
        for (String rule : rules){
            addPositive(rule);
        }
    }

    /**
     * 添加负正则
     * @param rule
     */
    public void addNegative(String rule){
        negative.add(rule);
    }

    /**
     *
     * @param rules
     */
    public void addNegative(List<String> rules){
        for (String rule : rules){
            negative.add(rule);
        }
    }

    /**
     * 对str进行正则， 符合条件返回true,不符合返回false
     * @param str 待验证的字符串
     * @return
     */
    public boolean satisfy(String str){

        for (String rule : negative){
            if (Pattern.matches(rule, str))
                return false;
        }

        for (String rule : positive){
            if (Pattern.matches(rule, str))
                return true;
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

