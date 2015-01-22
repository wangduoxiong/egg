package edu.xiyou.andrew.Egg.generator;

import com.sleepycat.je.*;
import edu.xiyou.andrew.Egg.utils.Config;
import edu.xiyou.andrew.Egg.utils.RegexRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andrew on 15-1-22.
 */
public class DBUpdater {
    private Environment environment;
    private Database lockDatabases;
    private SegmentWrite segmentWrite;

    private final static Logger LOG = LoggerFactory.getLogger(DBUpdater.class);

    public DBUpdater(Environment environment) throws Exception {
        this.environment = environment;
        lockDatabases = BerkeleyDBFactory.createDB(environment, "lock");
        segmentWrite = new SegmentWrite(environment);
    }

    public void locked() {
        DatabaseEntry keyEntry = new DatabaseEntry("lock".getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry("locked".getBytes());
        lockDatabases.put(null, keyEntry, valueEntry);
        lockDatabases.sync();
    }

    public void unlocked(){
        DatabaseEntry keyEntry = new DatabaseEntry("lock".getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry("unlocked".getBytes());
        lockDatabases.put(null, keyEntry, valueEntry);
        lockDatabases.sync();
    }

    public boolean islocked(){
        DatabaseEntry keyEntry = new DatabaseEntry("lock".getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry();
        if (lockDatabases.get(null, keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS){
            String info = new String(valueEntry.getData());
            if (info.equals("locked")){
                return true;
            }
        }
        return false;
    }

    public void merge(){
        LOG.info("merge start");
        try {
            Database crawlDatabase = BerkeleyDBFactory.createDB(environment, "crawl");
            Database fetchDatabase = BerkeleyDBFactory.createDB(environment, "fetch");
            Database linkDatabase = BerkeleyDBFactory.createDB(environment, "link");
            Cursor fetchCursor = fetchDatabase.openCursor(null, null);
            DatabaseEntry keyEntry = new DatabaseEntry();
            DatabaseEntry valueEntry = new DatabaseEntry();

            LOG.info("merge fetch database");
            while (fetchCursor.getNext(keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS){
                if (!(System.currentTimeMillis() - SegmentWrite.bytes2Long(valueEntry.getData(), 1) > Config.interval)){
                    crawlDatabase.put(null, keyEntry, valueEntry);
                }
            }
            fetchCursor.close();
            fetchDatabase.close();

            LOG.info("merge link database");
            Cursor linkCurser = linkDatabase.openCursor(null, null);
            while (linkCurser.getNext(keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS){
                crawlDatabase.put(null, keyEntry, valueEntry);
            }
            linkCurser.close();
            linkDatabase.close();
            LOG.info("end meger");

            crawlDatabase.sync();
            crawlDatabase.close();

            environment.removeDatabase(null, "fetch");
            LOG.debug("remove fetch database");
            environment.removeDatabase(null, "link");
            LOG.debug("remove link database");
        } catch (Exception e) {
            LOG.info("open database failed");
        }
    }

    public void close(){
        if (lockDatabases != null){
            lockDatabases.sync();
            lockDatabases.close();
        }
    }

    public SegmentWrite getSegmentWrite() {
        return segmentWrite;
    }
}
