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
import com.sleepycat.je.*;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by andrew on 15-1-28.
 */
public class BerkeleyDBDemo {
    Environment environment = null;
    Database database = null;

    public BerkeleyDBDemo(String path, String dbName) {
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        environment = new Environment(file, environmentConfig);
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setAllowCreate(true);
        database = environment.openDatabase(null, dbName, databaseConfig);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        BerkeleyDBDemo demo = new BerkeleyDBDemo("/home/andrew/practice/", "demoDB");
        DatabaseEntry keyEntry = new DatabaseEntry("key".getBytes("utf-8"));
        DatabaseEntry valueEntry = new DatabaseEntry("value".getBytes("utf-8"));
        demo.database.put(null, keyEntry, valueEntry);

        Cursor cursor = demo.database.openCursor(null, null);
        OperationStatus status = null;
        do {
            status = cursor.getNext(keyEntry, valueEntry, LockMode.DEFAULT);
            System.out.println(status);
            System.out.println("key: " + new String(keyEntry.getData(), "utf-8")
                    + " ,value; " + new String(valueEntry.getData(), "utf-8"));
        }while (true);

    }
}