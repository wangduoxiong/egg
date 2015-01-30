package edu.xiyou.andrew.Egg.generator;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.CrawlDatum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.DocFlavor;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 将种子注入到种子数据库内
 * Created by andrew on 15-1-22.
 */
public class Inject {
    private Environment environment;
    private Database database = null;

    private static AtomicInteger count = new AtomicInteger(0);  //注入种子的数量
    private static final int BUF_SIZE = 20;                     //缓冲区大小
    private static final Logger LOG = LoggerFactory.getLogger(Inject.class);

    public Inject(Environment environment) throws Exception {
        this.environment = environment;
    }

    /**
     * 将种子注入数据库
     * @param url
     */
    public void inject(String url){
        try {
            database = BerkeleyDBFactory.createDB(environment, "crawl");
            DatabaseEntry keyEntry = new DatabaseEntry(url.getBytes());
            DatabaseEntry valueEntry = new DatabaseEntry();
            byte[] bytes = new byte[9];
            bytes[0] = CrawlDatum.STATUS_INJECTED;
            try {
                SegmentWrite.long2Bytes(CrawlDatum.TIME_UNFETCH, bytes, 1);
            } catch (Exception e) {
                for (int i = 0; i < 8; i++) {
                    bytes[i + 1] = 0;
                }
            }
            valueEntry.setData(bytes);
            database.put(null, keyEntry, valueEntry);
        } catch (Exception e) {
            LOG.info("Exception: " + e);
        }finally {
            if (database != null){
                database.sync();
                database.close();
                database = null;
            }
        }
    }

    public void inject(List<String> list){
        try {
            database = BerkeleyDBFactory.createDB(environment, "crawl");
            DatabaseEntry keyEntry = new DatabaseEntry();
            DatabaseEntry valueEntry = new DatabaseEntry();
            byte[] bytes = new byte[9];
            bytes[0] = CrawlDatum.STATUS_INJECTED;

            for (String seed : list){
                keyEntry.setData(seed.getBytes());
                try {
                    SegmentWrite.long2Bytes(CrawlDatum.TIME_UNFETCH, bytes, 1);
                }catch (Exception e){
                    for (int i = 1; i < 9; i++){
                        bytes[i] = 0;
                    }
                }
                valueEntry.setData(bytes);
                database.put(null, keyEntry, valueEntry);
            }
        } catch (Exception e) {
            LOG.info("Exception: " + e);
        }finally {
            if (database != null){
                database.sync();
                database.close();
                database = null;
            }
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
