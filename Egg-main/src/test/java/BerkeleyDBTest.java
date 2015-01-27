import com.sleepycat.je.*;
import edu.xiyou.andrew.Egg.generator.SegmentWrite;
import edu.xiyou.andrew.Egg.generator.StandardGenerator;

import java.io.File;

/**
 * Created by andrew on 15-1-19.
 */
public class BerkeleyDBTest {
    private static Environment environment;
    private static DatabaseConfig databaseConfig;
    private static Database database;
    private static EnvironmentConfig environmentConfig;
    private static Cursor cursor;

    //    static {
//        environmentConfig = new EnvironmentConfig();
//        environmentConfig.setAllowCreate(true);
//        File file = new File("/home/andrew/test");
//        if (!file.exists()){
//            file.mkdirs();
//        }
//        environment = new Environment(file, environmentConfig);
//        databaseConfig = new DatabaseConfig();
//        databaseConfig.setAllowCreate(true);
//        databaseConfig.setReadOnly(true);
//
//        database = environment.openDatabase(null, "test2", databaseConfig);
//        cursor = database.openCursor(null, null);
//    }
//
//    public static void set(byte[] key, byte[] data){
//        DatabaseEntry keyEntry = new DatabaseEntry();
//        DatabaseEntry dataEntry = new DatabaseEntry();
//
//        keyEntry.setData(key);
//        dataEntry.setData(data);
//        database.put(null, keyEntry, dataEntry);
//    }
//
//    public static byte[] get(byte[] key){
//        DatabaseEntry keyEntry = new DatabaseEntry(key);
//        DatabaseEntry dataEntry = new DatabaseEntry();
//
//        if (cursor.getSearchKey(keyEntry, dataEntry, LockMode.DEFAULT) != OperationStatus.SUCCESS){
//            return null;
//        }
//
//        System.out.println(cursor.count());
//
//        if (cursor.getNextDup(keyEntry, dataEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS){
////            if (cursor.getNext(keyEntry, dataEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
//                return dataEntry.getData();
////            }
//        }
//
//        if (database.get(null, keyEntry, dataEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS){
//            return dataEntry.getData();
//        }
//
//        return null;
//    }
//
//    public static void close(){
//        database.close();
//        environment.close();
//    }
////
////    public static void main(String[] args){
//////        BerkeleyDBTest.set("123".getBytes(), "测试".getBytes());
//////        BerkeleyDBTest.set("123".getBytes(), "aaaaa".getBytes());
////        byte[] buf;
////        buf = BerkeleyDBTest.get("123".getBytes());
////        if (buf != null){
////            System.out.println(new String(buf));
////        }
////        cursor.close();
////        close();
////    }
//
//    static {
//        try {
//
//            environmentConfig = new EnvironmentConfig();
//            environmentConfig.setAllowCreate(true);
//            File file = new File("/home/andrew/test");
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            environment = new Environment(file, environmentConfig);
//
//            databaseConfig = new DatabaseConfig();
//            databaseConfig.setAllowCreate(true);
//            databaseConfig.setSortedDuplicates(true);
//
//            database = environment.openDatabase(null, "test2", databaseConfig);
//            cursor = database.openCursor(null, null);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public static void init(){
//
//    }


    public static void main(String[] args) throws Exception {
        File dir = new File("/home/andrew/test");
        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environment = new Environment(dir, environmentConfig);
        StandardGenerator generator = new StandardGenerator(environment);
        System.out.println(generator.nextDatum().getUrl());
    }


//    public static void main(String[] args) throws Exception {
//
//
//        DatabaseConfig databaseConfig = new DatabaseConfig();
//
//        databaseConfig.setAllowCreate(true);
//        //databaseConfig.setSortedDuplicates(true);
//        databaseConfig.setDeferredWrite(true);
//
//        EnvironmentConfig environmentConfig = new EnvironmentConfig();
//        environmentConfig.setAllowCreate(true);
//        environment = new Environment(new File("/home/andrew/test"), environmentConfig);
//
//        Database database = environment.openDatabase(null, "crawl", databaseConfig);
//
//        cursor = database.openCursor(null, CursorConfig.DEFAULT);
//
//        DatabaseEntry keyEntry = new DatabaseEntry();
//        DatabaseEntry valueEntry = new DatabaseEntry();
//
//        if (cursor.getNext(keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS){
//            System.out.println(new String(keyEntry.getData()));
//            System.out.println("" + valueEntry.getData()[0] + "  " + SegmentWrite.bytes2Long(valueEntry.getData(), 1));
//        }
//    }

//    public static void main(String[] args){
////        BerkeleyDBTest.init();
////        BerkeleyDBTest.set("123".getBytes(), "测试".getBytes());
////        BerkeleyDBTest.set("123".getBytes(), "aaaaa".getBytes());
////        byte[] buf;
////        buf = BerkeleyDBTest.get("123".getBytes());
////        if (buf != null){
////            System.out.println(new String(buf));
////        }
////        cursor.close();
////
////        BerkeleyDBTest.close();
//
//        byte[] buf = new byte[8];
//        long tmp = 1695179958;
//        long a;
//
//        buf[0] = (byte)((tmp >> 56) & 0xFF);
//        buf[1] = (byte)((tmp >> 48) & 0xFF);
//        buf[2] = (byte)((tmp >> 40) & 0xFF);
//        buf[3] = (byte)((tmp >> 32) & 0xFF);
//        buf[4] = (byte)((tmp >> 24) & 0xFF);
//        buf[5] = (byte)((tmp >> 16) & 0xFF);
//        buf[6] = (byte)((tmp >> 8) & 0xFF);
//        buf[7] = (byte)((tmp >> 0) & 0xFF);
//
//        a = (long)( (buf[7] & 0xFF) |
//                (buf[6] & 0xFF) << 8 |
//                (buf[5] & 0xFF) << 16 |
//                (buf[4] & 0xFF) << 24 |
//                (buf[3] & 0xFF << 32) |
//                (buf[2] & 0xFF) << 40 |
//                (buf[1] & 0xFF) << 48 |
//                (buf[0] & 0xFF) << 56
//        );
//
//        System.out.println(a);
//
//    }
}
