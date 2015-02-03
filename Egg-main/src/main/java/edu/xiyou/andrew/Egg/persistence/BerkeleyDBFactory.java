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
