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