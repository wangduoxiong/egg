package edu.xiyou.andrew.Egg.generator;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import java.io.File;

/**
 * Berkeley工厂类，传入数据库环境和数据库名，可返回数据库实例
 * Created by andrew on 15-1-20.
 */
public class BerkeleyDBFactory{
    /**
     * 传入数据库环境和数据库名，可返回数据库实例
     * @param environment 数据库环境
     * @param tableName 数据库名
     * @return 数据库实例
     * @throws Exception
     */
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
