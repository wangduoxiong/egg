package edu.xiyou.andrew.Egg.persistence;

import com.sleepycat.je.Database;
import com.sleepycat.je.Environment;

/**
 * Created by andrew on 15-2-1.
 */
public class BerkeleyDBUpdate {
    Environment environment = null;
    Database crawlDB = null;

    public BerkeleyDBUpdate(Environment environment){
        this.environment = environment;
    }
}
