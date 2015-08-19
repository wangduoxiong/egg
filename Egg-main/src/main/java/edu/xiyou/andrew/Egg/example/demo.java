package edu.xiyou.andrew.Egg.example;

import edu.xiyou.andrew.Egg.fetcher.Fetcher;
import edu.xiyou.andrew.Egg.parser.Handler;
import edu.xiyou.andrew.Egg.parser.LinksList;
import edu.xiyou.andrew.Egg.parser.Response;
import edu.xiyou.andrew.Egg.scheduler.BloomScheduler;
import edu.xiyou.andrew.Egg.scheduler.Scheduler;
import edu.xiyou.andrew.Egg.utils.FileUtils;
import edu.xiyou.andrew.Egg.utils.RegexRule;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by duoxiongwang on 15-8-19.
 */
public class demo {
    public static void main(String[] args) {
        Scheduler scheduler1 = new BloomScheduler();
        Fetcher fetcher = new Fetcher(scheduler1, new Handler() {
            @Override
            public void onSuccess(Response response) {
                String path = "/home/duoxiongwang/Documents/project/data/";
                String fileName = path + System.currentTimeMillis();
                try {
                    FileUtils.write2File(new File(fileName), response.getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(Response response) {

            }

            @Override
            public List<String> handleAndGetLinks(Response response) {
                LinksList list = new LinksList();
                RegexRule regexRule = new RegexRule();
                regexRule.addPositive(Arrays.asList("http://blog.csdn.net/\\w+/article/details/\\d+",
                        "http://www.importnew.com/\\d+.html",
                        "http://blog.csdn.net/?&page=\\d+"));
                list.getLinkByA(Jsoup.parse(new String(response.getContent())), regexRule);

                return list;
            }
        }, 0);
        fetcher.init();
        fetcher.before();
        fetcher.setScheduler(scheduler1);
        scheduler1.offer(Arrays.asList("http://www.importnew.com/all-posts", "http://blog.csdn.net"));

        fetcher.fetch();
    }
}
