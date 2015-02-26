package edu.xiyou.andrew.Egg.cralwer;

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

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import edu.xiyou.andrew.Egg.fetch.Fetcher;
import edu.xiyou.andrew.Egg.generator.StardardGenerator;
import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.Egg.net.HttpRequest;
import edu.xiyou.andrew.Egg.parse.Handler;
import edu.xiyou.andrew.Egg.parse.QueueScheduler;
import edu.xiyou.andrew.Egg.parse.Scheduler;
import edu.xiyou.andrew.Egg.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Created by andrew on 15-2-4.
 */
public class Crawler{
    protected static Logger logger = LoggerFactory.getLogger(Crawler.class);
    protected byte status;
    protected Fetcher fetcher;
    protected Environment environment;
    protected int depth;
    protected HttpRequest request;
    protected StardardGenerator generator;

    protected Handler handler;
    protected String crawlPath;
    protected List<String> seeds;

    public static byte RUNNING = 0x01;
    public static byte STOPED =  0x02;

    public Crawler(String crawlPath){
        this.crawlPath = crawlPath;
    }

    public List<String> getSeeds() {
        return seeds;
    }

    public void setSeeds(List<String> seeds) {
        this.seeds = seeds;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public void start() {
        File path = new File(crawlPath);

        if (path.exists()){
            FileUtils.deleteDir(path);
        }

        if (!path.getParentFile().exists()){
            path.getParentFile().mkdirs();
        }

        if (!path.exists()){
            path.mkdir();
        }

        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environment = new Environment(path, environmentConfig);
        Scheduler scheduler = new QueueScheduler(environment);
        generator = new StardardGenerator(environment);

        for (String url : seeds){
            scheduler.push(new CrawlDatum(url));
            System.out.println("seed: " + url);
        }
        status = RUNNING;

        for (int i = 0; (i < depth) && (status == RUNNING); i++){
            logger.info("start depth: " + depth);
            fetcher = new Fetcher(environment);
            fetcher.setHandler(handler);
            fetcher.setRequest(request);
            fetcher.fetch(generator);
//            fetcher.test(generator);
        }
    }

    public void stop(){
        status = STOPED;
        fetcher.stop();
    }

}
