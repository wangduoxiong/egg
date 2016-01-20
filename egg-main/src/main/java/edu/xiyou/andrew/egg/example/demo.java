package edu.xiyou.andrew.egg.example;

import edu.xiyou.andrew.egg.fetcher.Fetcher;
import edu.xiyou.andrew.egg.parser.Handler;
import edu.xiyou.andrew.egg.parser.LinksList;
import edu.xiyou.andrew.egg.net.Response;
import edu.xiyou.andrew.egg.scheduler.BloomScheduler;
import edu.xiyou.andrew.egg.scheduler.HashSetScheduler;
import edu.xiyou.andrew.egg.scheduler.Scheduler;
import edu.xiyou.andrew.egg.parser.RegexRule;
import org.jsoup.Jsoup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by duoxiongwang on 15-8-19.
 */
public class demo {
    public static void main(String[] args) throws InterruptedException {
        Scheduler scheduler = new HashSetScheduler();
        Long startTime = System.currentTimeMillis();

        Fetcher fetcher =
//        Config.cookies.put("key", "value");
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