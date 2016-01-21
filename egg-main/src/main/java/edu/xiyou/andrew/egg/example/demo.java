package edu.xiyou.andrew.egg.example;

import com.google.common.collect.Lists;
import edu.xiyou.andrew.egg.dataprocessor.ConsoleProcesser;
import edu.xiyou.andrew.egg.fetcher.Fetcher;
import edu.xiyou.andrew.egg.model.CrawlDatum;
import edu.xiyou.andrew.egg.parser.DefaultHandler;
import edu.xiyou.andrew.egg.parser.Handler;
import edu.xiyou.andrew.egg.parser.LinksList;
import edu.xiyou.andrew.egg.net.Response;
import edu.xiyou.andrew.egg.scheduler.BloomScheduler;
import edu.xiyou.andrew.egg.scheduler.HashSetScheduler;
import edu.xiyou.andrew.egg.scheduler.Scheduler;
import edu.xiyou.andrew.egg.parser.RegexRule;
import edu.xiyou.andrew.egg.utils.TimeUtils;
import org.jsoup.Jsoup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by duoxiongwang on 15-8-19.
 */
public class demo {
    public static void main(String[] args) throws InterruptedException {
        Long startTime = System.currentTimeMillis();

        Scheduler hashScheduler = new HashSetScheduler();
        hashScheduler.offer(Arrays.asList(CrawlDatum.custom().setUrl("http://baike.baidu.com/").build()));
        Fetcher fetcher =Fetcher.custom().
                setDataProcessorList(Lists.newArrayList(new ConsoleProcesser())).
                setHandler(new DefaultHandler(new RegexRule(Lists.newArrayList("http://baike.baidu.com/\\S+"))))
                .setScheduler(hashScheduler).builder();

        fetcher.fetch();
        fetcher.after();
        Long spendTime = 0L;
        while (!fetcher.isClose()) {
            Thread.sleep(10);
        }
        spendTime = System.currentTimeMillis() - startTime;

        System.out.println("spend time about " + TimeUtils.currentTime2String(spendTime));
        System.out.printf("spendTime: " + spendTime);
    }


}