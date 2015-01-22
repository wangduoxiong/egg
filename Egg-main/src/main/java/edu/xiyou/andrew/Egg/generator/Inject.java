package edu.xiyou.andrew.Egg.generator;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.CrawlDatum;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 将种子注入到种子数据库内
 * Created by andrew on 15-1-22.
 */
public class Inject {
    private Environment environment;
    private Database database;

    private static AtomicInteger count = new AtomicInteger(0);  //注入种子的数量
    private static final int BUF_SIZE = 20;                     //缓冲区大小

    public Inject(Environment environment) throws Exception {
        this.environment = environment;
        database = BerkeleyDBFactory.createDB(environment, "crawl");
    }

    /**
     * 将种子注入数据库
     * @param url
     */
    public void inject(String url){
        DatabaseEntry keyEntry = new DatabaseEntry(url.getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry();
        byte[] bytes = new byte[9];
        bytes[0] = CrawlDatum.STATUS_INJECTED;
        try {
            SegmentWrite.long2Bytes(CrawlDatum.TIME_UNFETCH, bytes, 1);
        } catch (Exception e) {
            for (int i = 0; i < 8; i++){
                bytes[i + 1] = 0;
            }
        }
        valueEntry.setData(bytes);
        database.put(null, keyEntry, valueEntry);

        if (count.incrementAndGet() % 20 == 0) {
            database.sync();
        }
    }

    public void inject(List<String> list){
        for (String url : list){
            inject(url);
        }
    }

    /**
     * 关闭数据库,并将数据写入数据库
     */
    public void close(){
        if (database != null) {
            database.sync();
            database.close();
        }
    }

    public static AtomicInteger getCount() {
        return count;
    }
}
