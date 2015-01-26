package edu.xiyou.andrew.Egg.crawler;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import edu.xiyou.andrew.Egg.fetcher.Fetcher;
import edu.xiyou.andrew.Egg.fetcher.Handle;
import edu.xiyou.andrew.Egg.generator.DBUpdater;
import edu.xiyou.andrew.Egg.generator.Inject;
import edu.xiyou.andrew.Egg.generator.StandardGenerator;
import edu.xiyou.andrew.Egg.net.HttpRequest;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.CrawlDatum;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.HttpHeaderMetadata;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.HttpRequestHeaders;
import edu.xiyou.andrew.Egg.utils.Config;
import edu.xiyou.andrew.Egg.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by andrew on 15-1-25.
 */
public abstract class Crawler implements Handle{

    public static final Logger LOG = LoggerFactory.getLogger(Crawler.class);

    protected int status;
    protected int retry = Config.retry;
    protected boolean resumable = false;
    protected int threads = Config.threadNum;
    protected ArrayList<String> seeds = new ArrayList<String>();
    protected ArrayList<String> forcedSeeds = new ArrayList<String>();
    protected Fetcher fetcher;

    protected Handle handle = this;
    protected HttpRequest request;
    String crawlPath;
    Environment environment;

    public static final int RUNNING = 1;
    public static final int STOPED = 2;

    {
//        HashMap<String, String> headers = new HashMap<String, String>();
//        headers.put(HttpHeaderMetadata.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
//        headers.put(HttpHeaderMetadata.COOKIE, "wdcid=6bff909e26a9e1d4; ALLYESID4=09895E0EE3D51841; xh_regionalNews_v1=12; wdlast=1422273537");
        HttpRequestHeaders headers = new HttpRequestHeaders();
        headers.setCookie("wdcid=6bff909e26a9e1d4; ALLYESID4=09895E0EE3D51841; xh_regionalNews_v1=12; wdlast=1422273537");
        headers.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
        request = new HttpRequest(headers);
    }

    public Crawler(String crawlPath){
        this.crawlPath = crawlPath;
    }

    public void inject() throws Exception{
        Inject inject = new Inject(environment);
        inject.inject(seeds);
    }

    public void injectForcedSeeds() throws Exception{
        Inject inject = new Inject(environment);
        inject.inject(forcedSeeds);
    }

    public void start(int depth) throws Exception{
        File dir = new File(crawlPath);
        boolean needInject = true;

        if (resumable && dir.exists()){
            needInject = false;
        }

        if (resumable && !dir.exists()){
            dir.mkdirs();
        }

        if (!resumable){
            if (dir.exists()){
                FileUtils.deleteDir(dir);
            }
            dir.mkdirs();
            if (seeds.isEmpty() && forcedSeeds.isEmpty()){
                LOG.info("Error: please add at least one seed");
                return;
            }
        }
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environment = new Environment(dir, environmentConfig);

        if (needInject){
            inject();
        }

        if (!forcedSeeds.isEmpty()){
            injectForcedSeeds();
        }

        status = RUNNING;
        for (int i = 0; i < depth; i++){
            if (status == STOPED){
                break;
            }
            LOG.info("starting depth " + (i + 1));

            StandardGenerator generator = new StandardGenerator(environment);

            fetcher = new Fetcher();
            fetcher.setRetry(retry);
            fetcher.setHttpRequest(request);
            fetcher.setDbUpdater(new DBUpdater(environment));
            fetcher.setHandle(handle);
            fetcher.setThreads(threads);
            fetcher.fetchAll(generator);
        }
//        environment.close();
    }

    public void stop(){
        status = STOPED;
        fetcher.stop();
    }

    public Handle getHandle(){
        return handle;
    }

    public void setHandle(Handle handle){
        this.handle = handle;
    }

    public void setHttpRequest(HttpRequest request){
        this.request = request;
    }

    public HttpRequest getHttpRequest(){
        return request;
    }

    public void addSeed(String seed){
        seeds.add(seed);
    }

    public void addForcedSeed(String seed){
        forcedSeeds.add(seed);
    }

    public List<String> getForcedSeeds(){
        return forcedSeeds;
    }

    public void setForcedSeeds(ArrayList<String> forcedSeeds){
        this.forcedSeeds = forcedSeeds;
    }

    public boolean isResumable(){
        return resumable;
    }

    public void setResumable(boolean resumable){
        this.resumable = resumable;
    }

    public int getThreads(){
        return threads;
    }

    public void setThreads(int threads){
        this.threads = threads;
    }

    public void setProxy(Proxy proxy){
        request.setProxy(proxy);
    }

    public Proxy getProxy(){
        return request.getProxy();
    }

    public int getRetry(){
        return retry;
    }

    public void setRetry(int retry){
        this.retry = retry;
    }
}
