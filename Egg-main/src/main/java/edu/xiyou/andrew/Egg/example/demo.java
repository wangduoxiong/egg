package edu.xiyou.andrew.Egg.example;

import edu.xiyou.andrew.Egg.fetcher.Fetcher;
import edu.xiyou.andrew.Egg.parser.Handler;
import edu.xiyou.andrew.Egg.parser.LinksList;
import edu.xiyou.andrew.Egg.net.Response;
import edu.xiyou.andrew.Egg.scheduler.BloomScheduler;
import edu.xiyou.andrew.Egg.scheduler.Scheduler;
import edu.xiyou.andrew.Egg.utils.RegexRule;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by duoxiongwang on 15-8-19.
 */
public class demo {
    public static void main(String[] args) throws InterruptedException {
        Scheduler scheduler1 = new BloomScheduler();
        Long startTime = System.currentTimeMillis();

        /**
         * Handler 接口是处理爬取网页后的页面的接口
         */
        Fetcher fetcher = new Fetcher(scheduler1, new Handler() {
            /**
             * 抓取成功是的操作
             * @param response
             */
            @Override
            public void onSuccess(Response response) {
                String path = "/home/andrew/Data/baike/data/";
                String fileName = path + System.nanoTime();
                try {
                    FileUtils.write2File(new File(fileName), response.getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /**
             * 失败的操作
             * @param response
             */
            @Override
            public void onFail(Response response) {

            }

            /**
             * 获取下一次爬取页面的操作
             * @param response
             * @return
             */
            @Override
            public List<String> handleAndGetLinks(Response response) {
                LinksList list = new LinksList();
                RegexRule regexRule = new RegexRule();
                regexRule.addPositive(Collections.singletonList("http://baike.baidu.com/\\S+"));
                regexRule.addNegative("http://baike.baidu.com/redirect/\\S+");
                list.getLinkByA(Jsoup.parse(new String(response.getContent())), regexRule);
                return list;
            }
        }, 0);
        fetcher.init();
        fetcher.before();
        fetcher.setScheduler(scheduler1);
        scheduler1.offer(Arrays.asList("http://baike.baidu.com/"));

        fetcher.fetch();
        fetcher.after();
        Long spendTime = 0L;
        while (!fetcher.isClose()) {
            Thread.sleep(10);
        }
        spendTime = System.currentTimeMillis() - startTime;

        System.out.println("spend time about " + currentTime2String(spendTime));
        System.out.printf("spendTime: " + spendTime);
    }

    public static String currentTime2String(long currentTime) {
        if (currentTime < 0) {
            throw new IllegalArgumentException("time must bigger 0");
        }
        if (currentTime == 0) {
            return "00:00:00:000";
        }
        int hours = (int) (currentTime / (60 * 60 * 1000));
        int minutes = (int)(currentTime - hours * 60 * 60 * 1000) / 60000;
        int second = (int)(currentTime - hours*60*60*1000 - minutes*60*1000)/1000;
        int mills = (int)(currentTime%1000);

        return new StringBuilder().append(hours).append(":").append(minutes).append(":").append(second).append(":").append(mills).toString();
    }
}