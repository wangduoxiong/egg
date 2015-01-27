import com.sleepycat.je.*;
import edu.xiyou.andrew.Egg.generator.SegmentWrite;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by andrew on 15-1-27.
 */
public class BerkeleyDBPractice {
    public static void testDatabase(String path){
        Environment environment = null;
        String dbName1 = "test1";
        String dbName2 = "test2";
        Database database1 = null;
        Database database2 = null;

        System.out.println("---->environmentConfig init.");
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);

        try {
            System.out.println("---->environment init.");
            environment = new Environment(new File(path), environmentConfig);
            System.out.println("---->databaseConfig init.");
            DatabaseConfig databaseConfig = new DatabaseConfig();

            databaseConfig.setAllowCreate(true);
            databaseConfig.setTransactional(true);

            System.out.println("---->openDatabase: name is test1");
            database1 = environment.openDatabase(null, dbName1, databaseConfig);
            System.out.println("---->name: " + database1.getDatabaseName());
            System.out.println("---->config: " + database1.getConfig());

            System.out.println("---->openDatabase: name is test2");
            database2 = environment.openDatabase(null, dbName2, databaseConfig);
            System.out.println("---->name: " + database2.getDatabaseName());
            System.out.println("---->config: " + database2.getConfig());
        }catch (Exception e){
            System.out.println(e);

        }finally {
            if (database1 != null){
                database1.close();
            }
            if (database2 != null){
                database2.close();
            }

            if (environment != null){
                environment.cleanLog();
                environment.close();
                environment = null;
            }
        }

    }

//    public static void main(String[] args){
//        System.out.println("====> demo test start <====");
//        testDatabase("/home/andrew/practice/");
//        System.out.println("====> demo test end!! <====");
//    }

    public static void testStats(String path, String dbName){
        Environment environment = null;
        Database database = null;

        System.out.println("---->environmentConfig init.");
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);
        environmentConfig.setConfigParam(EnvironmentConfig.NODE_MAX_ENTRIES, "4");

        try {
            System.out.println("---->environment init.");
            environment = new Environment(new File(path), environmentConfig);
            System.out.println("---->environment stats: ");
            System.out.println(environment.getStats(null));

            System.out.println("---->environment init.");
            DatabaseConfig databaseConfig = new DatabaseConfig();
            databaseConfig.setAllowCreate(true);
            databaseConfig.setTransactional(true);

            System.out.println("---->openDatabase ");
            database = environment.openDatabase(null, dbName, databaseConfig);

            System.out.println("---->database stats: ");
            System.out.println(database.getStats(null));
        }catch (Exception e){
            System.err.println("Exception: " + e);
        }finally {
            if (database != null){
                database.close();
            }
            if (environment != null){
                environment.cleanLog();
                environment.close();
                environment = null;
            }
        }
    }

//    public static void main(String[] args){
//        System.out.println("====> demo test start <====");
//        testStats("/home/andrew/practice/", "testStats");
//        System.out.println("====> demo test end!! <====");
//    }

    public static void testPutData(String path, String dbName){
        Environment environment = null;
        Database database = null;

        System.out.println("----> EnvironmentConfig init");
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);

        System.out.println("----> DatabaseConfig init");
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        dbConfig.setTransactional(true);
        dbConfig.setSortedDuplicates(true);

        try {
            System.out.println("----> Emvironment init");
            environment = new Environment(new File(path), environmentConfig);

            System.out.println("----> OpenDatabase: " + dbName);
            database = environment.openDatabase(null, dbName, dbConfig);

            System.out.println("----> Put a key with a value");
            String key = "testKey";
            String value = "testValue";
            String value2 = "testValue2";
            DatabaseEntry keyEntry = new DatabaseEntry(key.getBytes("utf-8"));
            DatabaseEntry valueEntry = new DatabaseEntry(value.getBytes("utf-8"));
            DatabaseEntry value2Entry = new DatabaseEntry(value2.getBytes("utf-8"));

            OperationStatus status = database.put(null, keyEntry, valueEntry);
            System.out.println("----> Put status: " + status);

            System.out.println("----> putNoOverwrite with the exit key");
            status = database.putNoOverwrite(null, keyEntry, valueEntry);
            System.out.println("----> putNoOverwrite status: " + status);

            System.out.println("----> putNoDupData the same key and value");
            status = database.putNoDupData(null, keyEntry, valueEntry);
            System.out.println("----> putNoDupData status: " + status);

            System.out.println("----> putNoDupData the same key and new value");
            status = database.putNoDupData(null, keyEntry, value2Entry);
            System.out.println("----> putNoDupData status: " + status);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            if (database != null){
                database.close();
            }

            if (environment != null){
                environment.cleanLog();
                environment.close();
                environment = null;
            }
        }
    }

    public static void main(String[] main){
        System.out.println("====> demo test start <=====");
        testPutData("/home/andrew/practice/", "testPutData");
        System.out.println("====> demo test end!! <=====");
    }
}
