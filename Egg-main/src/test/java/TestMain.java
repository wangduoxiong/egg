import edu.xiyou.andrew.Egg.crawler.Crawler;
import edu.xiyou.andrew.Egg.fetcher.Handle;
import edu.xiyou.andrew.Egg.generator.Links;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.HttpResponse;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.Link;

import java.io.File;
import java.io.IOException;

/**
 * Created by andrew on 15-1-25.
 */
public class TestMain extends Crawler implements Handle{

    String crawlPath;
    public TestMain(String crawlPath) {
        super(crawlPath);
        this.crawlPath = crawlPath;
    }

    @Override
    public void onSuccess(HttpResponse response) {
        try {
            File file = new File(crawlPath + '/' + System.currentTimeMillis());
            FileUtils.writeFile(file, response.getContent());
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    @Override
    public void onError(HttpResponse response) {

    }

    @Override
    public Links visitAndGetNextLinks(HttpResponse response) {
        return null;
    }

    public static void main(String[] args) throws Exception{
        TestMain testMain = new TestMain("/home/andrew/test1");
        testMain.addSeed("http://www.xinhuanet.com/");
        testMain.start(2);
    }
}
