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
