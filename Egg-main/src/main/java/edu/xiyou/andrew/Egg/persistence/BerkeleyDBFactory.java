package edu.xiyou.andrew.Egg.persistence;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;

/**
 * Berkeley工厂类 返回数据库实例
 * Created by andrew on 15-2-1.
 */
public class BerkeleyDBFactory {
    /**
     *
     * @param databaseConfig
     * @param environment
     * @param tableName
     * @return
     */
    public static Database createDB(DatabaseConfig databaseConfig, Environment environment, String tableName){
        return environment.openDatabase(null, tableName, databaseConfig);
    }

    /**
     * 使用默认数据库配置,根据传入的数据库名和环境配置返回数据库实例
     * @param environment
     * @param tableName 数据库名
     * @return Database
     */
    public static Database createDB(Environment environment, String tableName){
        return environment.openDatabase(null, tableName, defaultDatabaseConfig());
    }

    /**
     * 返回数据库默认配置
     * @return DatabaseConfig
     */
    public static DatabaseConfig defaultDatabaseConfig(){
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        databaseConfig.setTransactional(false);

        return databaseConfig;
    }
}
