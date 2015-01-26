package edu.xiyou.andrew.Egg.generator;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import edu.xiyou.andrew.Egg.pageprocessor.pageinfo.CrawlDatum;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 将链接数据写入Berkeley数据库的基本类库
 * Created by andrew on 15-1-20.
 */
public class SegmentWrite {
    private static final int BUFF_SIZE = 20;
    protected Database fetchDB; //已爬取数据库
    protected Database linkDB;

    private static AtomicInteger fetchCount;    //已爬取的数量
    private static AtomicInteger linkCount;     //链接的数量
    private Environment environment;

    static {
        fetchCount = new AtomicInteger(0);
        linkCount = new AtomicInteger(0);
    }

    public SegmentWrite(Environment environment) throws Exception {
        this.environment = environment;
        fetchDB = BerkeleyDBFactory.createDB(environment, "fetch");
        linkDB = BerkeleyDBFactory.createDB(environment, "link");
    }

    public void writeFetch(CrawlDatum datum) throws Exception{
        DatabaseEntry keyEntry = new DatabaseEntry(datum.getUrl().getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry();
        byte[] value = new byte[9];
        value[0] = datum.getStatus();
        long tmp = datum.getFetchTime();

        value[1] = (byte)((tmp >> 56) & 0xFF);
        value[2] = (byte)((tmp >> 48) & 0xFF);
        value[3] = (byte)((tmp >> 40) & 0xFF);
        value[4] = (byte)((tmp >> 32) & 0xFF);
        value[5] = (byte)((tmp >> 24) & 0xFF);
        value[6] = (byte)((tmp >> 16) & 0xFF);
        value[7] = (byte)((tmp >> 8) & 0xFF);
        value[8] = (byte)((tmp >> 0) & 0xFF);
        valueEntry.setData(value);

//        a = (long)( (buf[7] & 0xFF) |
//                (buf[6] & 0xFF) << 8 |
//                (buf[5] & 0xFF) << 16 |
//                (buf[4] & 0xFF) << 24 |
//                (buf[3] & 0xFF << 32) |
//                (buf[2] & 0xFF) << 40 |
//                (buf[1] & 0xFF) << 48 |
//                (buf[0] & 0xFF) << 56
//        );

        fetchDB.put(null, keyEntry, valueEntry);
        if (fetchCount.incrementAndGet() % BUFF_SIZE == 0){
            fetchDB.sync();
        }
    }

    /**
     * 将long型数据转化称bytes数组
     * @param value long 值
     * @param bytes byte数组
     * @param offset byte数组的便宜量
     */
    public static void long2Bytes(long value, byte[] bytes, int offset) throws Exception{
        if (bytes.length < 8){
            throw new Exception("byte array not enough 8");
        }

        bytes[offset] = (byte)((value >> 56) & 0xFF);
        bytes[offset + 1] = (byte)((value >> 48) & 0xFF);
        bytes[offset + 2] = (byte)((value >> 40) & 0xFF);
        bytes[offset + 3] = (byte)((value >> 32) & 0xFF);
        bytes[offset + 4] = (byte)((value >> 24) & 0xFF);
        bytes[offset + 5] = (byte)((value >> 16) & 0xFF);
        bytes[offset + 6] = (byte)((value >> 8) & 0xFF);
        bytes[offset + 7] = (byte)((value >> 0) & 0xFF);
    }

    /**
     * 将long转化成bytes数组,无偏移
     * @param value
     * @param bytes
     * @throws Exception 数组越界
     */
    public static void long2Bytes(long value, byte[] bytes) throws Exception {
        long2Bytes(value, bytes, 0);
    }

    /**
     * 将bytes数组转化成long
     * @param bytes 数组
     * @param offset 偏移量
     * @return long数据
     * @throws Exception 数组长度不够
     */
    public static long bytes2Long(byte[] bytes, int offset) throws Exception{
        if (bytes.length < 8){
            throw new Exception("byte array not enough 8");
        }

        long value;
        value = (long)( (bytes[7 + offset] & 0xFF) |
                (bytes[6 + offset] & 0xFF) << 8 |
                (bytes[5 + offset] & 0xFF) << 16 |
                (bytes[4 + offset] & 0xFF) << 24 |
                (bytes[3 + offset] & 0xFF << 32) |
                (bytes[2 + offset] & 0xFF) << 40 |
                (bytes[1 + offset] & 0xFF) << 48 |
                (bytes[0 + offset] & 0xFF) << 56
        );
        return value;
    }

    /**
     * 将数组转化成long
     * @param bytes 数组
     * @throws Exception 数组长度不够
     */
    public static void bytes2Long(byte[] bytes) throws Exception {
        bytes2Long(bytes, 0);
    }

    public void writeFetchs(List<CrawlDatum> datums) throws Exception {
        for (CrawlDatum datum : datums){
            writeFetch(datum);
        }
    }

    /**
     * 将link写入数据库,key是链接url, value值value数组，value[0]代表爬取状态,状态可进行|运算,同时存取多个状态,
     *                                              value[1-8]代表爬取时间
     * @param link
     * @throws Exception
     */
    public void writeLink(String link) throws Exception{
        DatabaseEntry keyEntry = new DatabaseEntry(link.getBytes());
        byte[] value = new byte[9];
        value[0] = CrawlDatum.STATUS_UNFETCH;
        long2Bytes(CrawlDatum.TIME_UNFETCH, value, 1);
        DatabaseEntry valueEntry = new DatabaseEntry(value);
        linkDB.put(null, keyEntry, valueEntry);
        if (linkCount.incrementAndGet() % BUFF_SIZE == 0){
            linkDB.sync();
        }
    }

    public void writeLinks(List<String> links) throws Exception {
        for (String link : links){
            writeLink(link);
        }
    }

    public void close(){
        if (fetchDB != null){
            fetchDB.close();
        }
        if (linkDB != null){
            linkDB.close();
        }
        if (environment != null){
            environment.close();
        }
    }

    public static AtomicInteger getFetchCount() {
        return fetchCount;
    }

    public static AtomicInteger getLinkCount() {
        return linkCount;
    }
}
