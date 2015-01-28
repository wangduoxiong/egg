import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
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

            String value3 = "testValue3";
            DatabaseEntry keyEntry = new DatabaseEntry(key.getBytes("utf-8"));
            DatabaseEntry valueEntry = new DatabaseEntry(value.getBytes("utf-8"));
            DatabaseEntry value2Entry = new DatabaseEntry(value2.getBytes("utf-8"));
            DatabaseEntry value3Entry = new DatabaseEntry(value3.getBytes("utf-8"));

            OperationStatus status = database.put(null, keyEntry, valueEntry);
            System.out.println("----> Put status: " + status);

            System.out.println("----> putNoOverwrite with the exit key");
            status = database.putNoOverwrite(null, keyEntry, valueEntry);
            System.out.println("----> putNoOverwrite status: " + status);

            System.out.println("----> putNoOverwrite the same key and value");
            status = database.putNoOverwrite(null, keyEntry, value3Entry);
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

//    public static void main(String[] main){
//        System.out.println("====> demo test start <=====");
//        testPutData("/home/andrew/practice/", "testPutData1");
//        System.out.println("====> demo test end!! <=====");
//    }

    public static void testGetData(String path, String dbName){
        Environment environment = null;
        Database database = null;

        System.out.println("----> EnvironmentConfig init");
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);

        try {
            System.out.println("----> Environment init");
            environment = new Environment(new File(path), environmentConfig);

            System.out.println("----> DatabaseConfig init");
            DatabaseConfig databaseConfig = new DatabaseConfig();
            databaseConfig.setAllowCreate(true);
            databaseConfig.setTransactional(true);
            databaseConfig.setSortedDuplicates(true);

            System.out.println("----> Database open");
            database = environment.openDatabase(null, dbName, databaseConfig);
            System.out.println();

            String key = "testKey";
            System.out.println("----> Get data by key = " + key);
            DatabaseEntry keyEntry = new DatabaseEntry(key.getBytes("utf-8"));
            DatabaseEntry valueEntry = new DatabaseEntry();
            OperationStatus status = database.get(null, keyEntry, valueEntry, LockMode.DEFAULT);

            System.out.println("----> Status value: " + status);
            if (status == OperationStatus.SUCCESS){
                System.out.println("----> Get key = " + key + " ----> Get value = " + new String(valueEntry.getData(), "utf-8"));
            }else {
                System.out.println("----> No record found for key = " + key);
            }
            System.out.println();

            String value1 = "testValue";
            System.out.println("----> GetSearchBoth by key = " + key + " and value = " + value1);
            DatabaseEntry dataEntry = new DatabaseEntry(value1.getBytes("utf-8"));
            status = database.getSearchBoth(null, keyEntry, dataEntry, LockMode.DEFAULT);
            System.out.println("----> SearchBoth status value: " + status);
            System.out.println();

            String value2 = "testValue1";
            System.out.println("----> GetSearchBoth by key = " + key + " and value = " + value2);
            dataEntry = new DatabaseEntry(value2.getBytes("utf-8"));
            status = database.getSearchBoth(null, keyEntry, dataEntry, LockMode.DEFAULT);
            System.out.println("----> SearchBoth status value: " + status);
            System.out.println();
        }catch (Exception e){
            System.err.println(e);
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
//        System.out.println("====> Demo test start <====");
//        testGetData("/home/andrew/practice/", "testPutData");
//        System.out.println("====> Demo test end!! <====");
//    }

    public static void testBind(String path, String dbName){
        Environment environment = null;
        Database database = null;

        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);

        try {
            environment = new Environment(new File(path), environmentConfig);

            DatabaseConfig databaseConfig = new DatabaseConfig();
            databaseConfig.setAllowCreate(true);
            databaseConfig.setTransactional(true);

            System.out.println("----> Open database");
            database = environment.openDatabase(null, dbName, databaseConfig);

            MyBindTest<String> stringTest = new MyBindTest<String>(database, "myString", String.class, "testvalue");
            stringTest.runDemo();

            MyBindTest<Boolean> booleanTest = new MyBindTest<Boolean>(database, "myBoolean", Boolean.class, Boolean.TRUE);
            booleanTest.runDemo();

            MyBindTest<Byte> byteTest = new MyBindTest<Byte>(database, "myByte", Byte.class, Byte.valueOf("65"));
            byteTest.runDemo();

            MyBindTest<Short> shortTest = new MyBindTest<Short>(database, "myShort", Short.class, Short.valueOf("200"));
            shortTest.runDemo();

            MyBindTest<Integer> integerTest = new MyBindTest<Integer>(database, "myInteger", Integer.class, Integer.valueOf(200));
            integerTest.runDemo();

            MyBindTest<Long> longTest = new MyBindTest<Long>(database, "myLong", Long.class, Long.valueOf(200));
            longTest.runDemo();

            MyBindTest<Float> floatTest = new MyBindTest<Float>(database, "myFloat", Float.class, Float.valueOf(200.200f));
            floatTest.runDemo();

            MyBindTest<Double> doubleTest = new MyBindTest<Double>(database, "myDouble", Double.class, Double.valueOf(200.200));
            doubleTest.runDemo();


        }catch (Exception e){
            System.err.println(e);
        }finally {
            if (database != null){
                database.close();
            }

            if (environment != null){
                environment.cleanLog();
                environment.close();
            }
        }
    }

    static class MyBindTest<T>{
        private Database database;
        private String keyName;
        private T value;
        private Class<T> clazz;

        public MyBindTest(Database database, String keyName, Class<T> clazz, T value){
            this.database = database;
            this.keyName = keyName;
            this.clazz = clazz;
            this.value = value;
        }

        public void runDemo() throws Exception{
            System.out.println("------------------------------------------");
            String type = clazz.getSimpleName();
            System.out.println("----> Bind Object Type: " + type);
            DatabaseEntry keyEntry = new DatabaseEntry(keyName.getBytes("utf-8"));
            System.out.println("----> Put key = " + keyName + "," + type + " value = " + value);
            DatabaseEntry valueEntry = new DatabaseEntry();
            EntryBinding<T> binding = TupleBinding.getPrimitiveBinding(clazz);
            binding.objectToEntry(value, valueEntry);
            OperationStatus status = database.put(null, keyEntry, valueEntry);
            System.out.println("----> Put status: " + status);

            System.out.println("----> Get data by key: " + keyName);
            DatabaseEntry findEntry = new DatabaseEntry();
            status = database.get(null, keyEntry, findEntry, LockMode.DEFAULT);

            if (status == OperationStatus.SUCCESS){
                T value = binding.entryToObject(findEntry);
                System.out.println("----> Get key = " + keyName + " , value = " + value);
            }else {
                System.out.println("----> No record found for key = " + keyName);
            }
        }
    }

    public static void main(String[] args){
        System.out.println("====> Demo test start <====");
        testBind("/home/andrew/practice/", "testBinding");
        System.out.println("====> Demo test end!! <=====");
    }
}

