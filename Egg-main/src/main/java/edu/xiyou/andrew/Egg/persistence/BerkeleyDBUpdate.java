/*
 *Copyright (c) 2015 Andrew-Wang.
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package edu.xiyou.andrew.Egg.persistence;

import com.sleepycat.je.*;
import edu.xiyou.andrew.Egg.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.sleepycat.je.LockMode.DEFAULT;

/**
 * Created by andrew on 15-2-1.
 */
public class BerkeleyDBUpdate {
    private static Logger logger = LoggerFactory.getLogger(BerkeleyDBUpdate.class);

    protected Environment environment = null;
    protected Database lockDatabase;
    private BerkeleyWrite write;

    public BerkeleyDBUpdate(Environment environment){
        this.environment = environment;
        this.write = new BerkeleyWrite(environment);
    }

    public void lock(){
        lockDatabase = BerkeleyDBFactory.createDB(environment, "lock");
        DatabaseEntry keyEntry = new DatabaseEntry("lock".getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry("locked".getBytes());
        lockDatabase.put(null, keyEntry, valueEntry);
        lockDatabase.sync();
        lockDatabase.close();
        lockDatabase = null;
    }

    public void unlock(){
        lockDatabase = BerkeleyDBFactory.createDB(environment, "lock");
        DatabaseEntry keyEntry = new DatabaseEntry("lock".getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry("unlock".getBytes());
        lockDatabase.put(null, keyEntry, valueEntry);
        lockDatabase.sync();
        lockDatabase.close();
        lockDatabase = null;
    }

    public boolean islock(){
        boolean islock = false;
        lockDatabase = BerkeleyDBFactory.createDB(environment, "lock");
        DatabaseEntry keyEntry = new DatabaseEntry("lock".getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry();
        try {
            if (lockDatabase.get(null, keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                String info = new String(valueEntry.getData());
                if (info.equals("locked")) {
                    return true;
                }
            }
        }finally {
            lockDatabase.close();
            lockDatabase = null;
        }
        return false;
    }

    public void merge(){
        logger.info("merge start");
        try {
            Database crawlDB = BerkeleyDBFactory.createDB(environment, "crawlDB");
            Database linkDB = BerkeleyDBFactory.createDB(environment, "linkDB");
            Database visitedDB = BerkeleyDBFactory.createDB(environment, "visitedDB");
            logger.info("create databases success");
            Cursor visitedCurser = visitedDB.openCursor(null, null);
            DatabaseEntry keyEntry = new DatabaseEntry();
            DatabaseEntry valueEntry = new DatabaseEntry();

            try {
                logger.info("merge visited database");
                if (Config.interval != -1) {
                    while (visitedCurser.getNext(keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                        if ((System.currentTimeMillis() - BerkeleyWrite.bytes2Long(valueEntry.getData(), 1)) > Config.interval) {
                            crawlDB.put(null, keyEntry, valueEntry);
                            visitedCurser.delete();
                        }
                    }
                }
                logger.info("merged visited databse");
            }finally {
                visitedCurser.close();
                visitedDB.close();
            }

            Cursor linkCursor = linkDB.openCursor(null, null);
            try {
                logger.info("merge link database");
                while (linkCursor.getNext(keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS){
                    crawlDB.put(null, keyEntry, valueEntry);
                    linkCursor.delete();
                }
                logger.info("merged link database");
            }finally {
                linkCursor.close();
                linkDB.close();
            }
            if (crawlDB != null){
                crawlDB.close();
            }
            logger.info("end merge");
            //environment.removeDatabase(null, "linkDB");
            logger.info("remove link database");
        }catch (Exception e){
            logger.info("Exception: " + e, new Exception());
        }

    }

    public void close(){
        write.close();
    }

    public BerkeleyWrite getWrite() {
        return write;
    }
}
