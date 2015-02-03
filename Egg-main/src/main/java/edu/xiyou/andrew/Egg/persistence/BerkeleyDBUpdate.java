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
    protected Environment environment = null;
    protected Database crawlDB = null;
    private static Logger logger = LoggerFactory.getLogger(BerkeleyDBUpdate.class);
    private long interval = -1;                 //相同事件爬取的间隔时间 -1 代表不重复爬取

    public BerkeleyDBUpdate(Environment environment){
        this.environment = environment;
    }

    public void writeInfo(Map<DatabaseEntry, DatabaseEntry> map){
        if (map == null){
            return;
        }
        Database infoDB = BerkeleyDBFactory.createDB(environment, "infoDB");
        try {
            Set<Map.Entry<DatabaseEntry, DatabaseEntry>> entrySet = map.entrySet();
            for (Map.Entry<DatabaseEntry, DatabaseEntry> entry : entrySet) {
                infoDB.put(null, entry.getKey(), entry.getValue());
            }
        }finally {
            infoDB.sync();
            infoDB.close();
            infoDB = null;
        }
    }

    public void lock(){
        DatabaseEntry keyEntry = new DatabaseEntry("lock".getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry("locked".getBytes());
        Map<DatabaseEntry, DatabaseEntry> map = new HashMap<DatabaseEntry, DatabaseEntry>(1);
        map.put(keyEntry, valueEntry);
        writeInfo(map);
    }

    public void unlock(){
        DatabaseEntry keyEntry = new DatabaseEntry("lock".getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry("unlock".getBytes());
        Map<DatabaseEntry, DatabaseEntry>map = new HashMap<DatabaseEntry, DatabaseEntry>(1);
        map.put(keyEntry, valueEntry);
        writeInfo(map);
    }

    public boolean islock(){
        Database infoDB = BerkeleyDBFactory.createDB(environment, "infoDB");
        DatabaseEntry keyEntry = new DatabaseEntry("lock".getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry();
        try {
            if (infoDB.get(null, keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                String info = new String(valueEntry.getData());
                if (info.equals("lock")) {
                    return true;
                }
            }
        }finally {
            infoDB.sync();
            infoDB.close();
            infoDB = null;
        }
        return false;
    }

    public void merge(){
        logger.info("merge start");
        try {
            Database crawlDB = BerkeleyDBFactory.createDB(environment, "crawlDB");
            Database linkDB = BerkeleyDBFactory.createDB(environment, "linkDB");
            Database visitedDB = BerkeleyDBFactory.createDB(environment, "visited");
            logger.info("create databases success");
            Cursor visitedCurser = visitedDB.openCursor(null, null);
            DatabaseEntry keyEntry = new DatabaseEntry();
            DatabaseEntry valueEntry = new DatabaseEntry();

            try {
                logger.info("merge visited database");
                if (interval != -1) {
                    while (visitedCurser.getNext(keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
                        if ((System.currentTimeMillis() - BerkeleyWrite.bytes2Long(valueEntry.getData(), 1)) > interval) {
                            crawlDB.put(null, keyEntry, valueEntry);
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
                }
                logger.info("merged link database");
            }finally {
                linkCursor.close();
                linkDB.close();
            }
            logger.info("end merge");
        }finally {
            environment.removeDatabase(null, "linkDB");
            logger.info("remove link database");
            crawlDB.close();
        }
    }
}
