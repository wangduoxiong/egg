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
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import edu.xiyou.andrew.Egg.net.CrawlDatum;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 将任务存入数据库内
 * linkDB 是存新产生的任务:一开始的初始化的种子;分析页面得出的链接
 * visited 是存入已经浏览过的任务
 * Created by andrew on 15-2-1.
 */
public class BerkeleyWrite {
    private Database linkDB;
    private Database visitedDB;
    private Environment environment;

    private static AtomicLong linkTotalNum = new AtomicLong(0);    //记录已经添加了新任务的数量
    private static AtomicLong visitedTotalNum = new AtomicLong(0); //记录已经浏览过的任务数量
    private static AtomicLong linkNum;               //从每次初始化之后开始计数，记录已经添加的新任务数量
    private static AtomicLong visitedNum;            //从每次初始化之后开始计数，记录已经浏览过的任务数量

    public BerkeleyWrite(Environment environment){
        this.environment = environment;
    }

    public void init(){
        linkNum = new AtomicLong(0);
        visitedNum = new AtomicLong(0);
        visitedDB = BerkeleyDBFactory.createDB(environment, "visitedDB");
        linkDB = BerkeleyDBFactory.createDB(environment, "linkDB");
    }

    public void writeLink(CrawlDatum datum){
        if ((datum == null) || (datum.getUrl() == null) ||
                (datum.getUrl().length() < 7)){
            return;
        }


        System.out.println("link: " + datum);



        DatabaseEntry keyEntry = new DatabaseEntry(datum.getUrl().getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry();
        byte[] bytes = new byte[9];
        bytes[0] = datum.getStatus();
        BerkeleyWrite.long2Bytes(datum.getFetchTime(), bytes, 1);
        valueEntry.setData(bytes);
        linkDB.put(null, keyEntry, valueEntry);
    }

    public void writeLink(List<CrawlDatum> datums){
        if ((datums == null) || (datums.size()==0)){
            return;
        }
        for (CrawlDatum datum : datums) {
            writeLink(datum);
        }
    }

    public void writeVisited(CrawlDatum datum){
        if ((datum == null) || (datum.getUrl() == null) ||
                (datum.getUrl().length() < 7)){
            return;
        }
        DatabaseEntry keyEntry = new DatabaseEntry(datum.getUrl().getBytes());
        DatabaseEntry valueEntry = new DatabaseEntry();
        byte[] bytes = new byte[9];
        bytes[0] = datum.getStatus();
        BerkeleyWrite.long2Bytes(datum.getFetchTime(), bytes, 1);
        valueEntry.setData(bytes);
        linkDB.put(null, keyEntry, valueEntry);
    }

    public void sync(){
        if (linkDB != null){
            linkDB.sync();
        }
        if (visitedDB != null){
            visitedDB.sync();
        }
    }

    public void close(){
        if (visitedDB != null){
            visitedDB.close();
            visitedDB = null;
        }
        if (linkDB != null){
            linkDB.close();
            linkDB = null;
        }
    }

    /**
     * 将long型数据转化称bytes数组
     * @param value long 值
     * @param bytes byte数组
     * @param offset byte数组的便宜量
     */
    public static void long2Bytes(long value, byte[] bytes, int offset){
        for (int i=0;i<8;i++) {
            bytes[i + offset] = (byte)(value>>>(56-(i*8)) & 0xFF);
        }
    }

    /**
     * 将long转化成bytes数组,无偏移
     * @param value
     * @param bytes
     * @throws Exception 数组越界
     */
    public static void long2Bytes(long value, byte[] bytes){
        long2Bytes(value, bytes, 0);
    }

    /**
     * 将bytes数组转化成long
     * @param bytes 数组
     * @param offset 偏移量
     * @return long数据
     * @throws Exception 数组长度不够
     */
    public static long bytes2Long(byte[] bytes, int offset) {
        long temp = 0;
        long res = 0;
        int end = 8 + offset;
        for ( ;offset < end;offset++) {
            res <<= 8;
            temp = (bytes[offset] & 0xff);
            res |= temp;
        }
        return res;
    }

    /**
     * 将数组转化成long
     * @param bytes 数组
     * @throws Exception 数组长度不够
     */
    public static long bytes2Long(byte[] bytes){
        return bytes2Long(bytes, 0);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public static long getLinkTotalNum() {
        return linkTotalNum.get();
    }

    public static long getVisitedTotalNum() {
        return visitedTotalNum.get();
    }

    public static long getLinkNum() {
        return linkNum.get();
    }

    public static long getVisitedNum() {
        return visitedNum.get();
    }
}
