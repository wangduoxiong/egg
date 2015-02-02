package edu.xiyou.andrew.Egg.generator;

import com.sleepycat.je.*;
import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.Egg.persistence.BerkeleyDBFactory;
import edu.xiyou.andrew.Egg.persistence.BerkeleyWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by andrew on 15-2-2.
 */
public class StardardGenerator implements Generator{
    private Environment environment;
    private Database crawlDB;
    private Cursor cursor;
    private static Logger logger = LoggerFactory.getLogger(StardardGenerator.class);
    private static AtomicLong tatol = new AtomicLong(0);       //目前产生的任务数量

    public StardardGenerator(Environment environment){
        this.environment = environment;
        crawlDB = BerkeleyDBFactory.createDB(environment, "crawlDB");
        cursor = crawlDB.openCursor(null, null);
    }

    @Override
    public CrawlDatum next() {
        DatabaseEntry keyEntry = new DatabaseEntry();
        DatabaseEntry valueEntry = new DatabaseEntry();
        byte[] value = new byte[9];
        long count = count();

        CrawlDatum datum = null;
        while ((tatol.get() < count) || (cursor.getNext(keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS)){
            tatol.incrementAndGet();
            value = valueEntry.getData();
            datum = new CrawlDatum(new String(keyEntry.getData()), value[0], BerkeleyWrite.bytes2Long(value, 1));
            if (datum != null){
                return datum;
            }
        }

        return datum;
    }

    public long count(){
        return crawlDB.count();
    }
}
