package edu.xiyou.andrew.Egg.generator;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import java.io.File;

/**
 * Created by andrew on 15-1-20.
 */
public class BerkeleyDBFactory{
    public static Database createDB(Environment environment, String tableName) throws Exception{
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);

        DatabaseConfig databaseConfig = new DatabaseConfig();

        databaseConfig.setAllowCreate(true);
        databaseConfig.setSortedDuplicates(true);

        Database database = environment.openDatabase(null, tableName, databaseConfig);
        return database;
    }
}
