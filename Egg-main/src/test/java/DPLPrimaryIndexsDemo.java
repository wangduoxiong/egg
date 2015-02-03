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

import com.sleepycat.je.Database;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.impl.Store;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by andrew on 15-1-28.
 */
public class DPLPrimaryIndexsDemo {
//    Environment environment = null;
//    EntityStore store = null;

    public void testDPLPKeyPut(String path, String storeName){
        Environment environment = null;
        EntityStore store = null;

        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);

        try {
            environment = new Environment(new File(path), environmentConfig);

            StoreConfig storeConfig = new StoreConfig();
            storeConfig.setAllowCreate(true);
            storeConfig.setTransactional(true);

            store = new EntityStore(environment, storeName, storeConfig);

            System.out.println("----> Put data by PrimaryIndex.....");
            PrimaryIndex<String, UserInfo4DPL> pIndex = store.getPrimaryIndex(String.class, UserInfo4DPL.class);

            List<UserInfo4DPL> list = initObject();
            for (UserInfo4DPL vo : list){
                System.out.println("----> Put object :\t" + vo);
                UserInfo4DPL exit = pIndex.put(vo);
                System.out.println("----> After put get the key exit object:\t" + exit);
            }
            UserInfo4DPL data = list.get(0);
            System.out.println("----> Object 'description' change to www.baidu.com");
            data.setDescription("www.baidu.com");
            System.out.println("----> Put object:\n" + data);
            UserInfo4DPL retData = pIndex.put(data);
            System.out.println("----> After put get the key exit object:\n" + retData);
            System.out.println("注意:put 返回值是当前key的前一个值");
            System.out.println();


        }catch (Exception e){
            System.out.println(e);
        }finally {
            if (store != null){
                store.close();
            }
            if (environment != null){
                environment.cleanLog();
                environment.close();
            }
        }
    }

    public void testDPLPKeyRead(String path, String storeName){
        Environment environment = null;
        EntityStore store = null;

        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);

        try {
            environment = new Environment(new File(path), environmentConfig);

            StoreConfig storeConfig = new StoreConfig();
            storeConfig.setAllowCreate(true);
            storeConfig.setTransactional(true);

            store = new EntityStore(environment, storeName, storeConfig);

            System.out.println("----> Read data by PrimaryIndex ....");
            PrimaryIndex<String, UserInfo4DPL> pIndex = store.getPrimaryIndex(String.class, UserInfo4DPL.class);

            String mykey = "ctosun";
            System.out.println("----> Get data by key: " + mykey);
            UserInfo4DPL getData = pIndex.get(mykey);
            System.out.println("----> The key value:\n" + getData);

            System.out.println("----> Read all data list:");
            EntityCursor<UserInfo4DPL> cursor = pIndex.entities();
            try {
                Iterator<UserInfo4DPL> iterator = cursor.iterator();
                while (iterator.hasNext()){
                    System.out.println("----> Cursor data: " + iterator.next());
                }
            }finally {
                cursor.close();
            }
        }catch (Exception e){
            System.out.println(e);
        }finally {
            if (store != null){
                store.close();
            }

            if (environment != null){
                environment.cleanLog();
                environment.close();

            }
        }
    }

    public void testDPLPKeyDelete(String path, String storeName){
        Environment environment = null;
        EntityStore store = null;

        EnvironmentConfig environmentConfig = new EnvironmentConfig();
        environmentConfig.setAllowCreate(true);
        environmentConfig.setTransactional(true);

        try{
            environment = new Environment(new File(path), environmentConfig);

            StoreConfig storeConfig = new StoreConfig();
            storeConfig.setAllowCreate(true);
            storeConfig.setTransactional(true);

            store = new EntityStore(environment, storeName, storeConfig);

            PrimaryIndex<String, UserInfo4DPL>pIndex = store.getPrimaryIndex(String.class, UserInfo4DPL.class);

            System.out.println("----> Before delete display all list: ");
            EntityCursor<UserInfo4DPL> cursor = pIndex.entities();

            try{
                Iterator<UserInfo4DPL> iterator = cursor.iterator();
                while (iterator.hasNext()){
                    System.out.println("----> Cursor data: " + iterator.next());
                }
            }finally {
                cursor.close();
            }

            System.out.println("----> Delete data by PrimaryKey");
            String pkey = "testKey";
            boolean flag = pIndex.delete(pkey);
            System.out.println("----> Delete object :" + pkey + " result:" + flag);
            System.out.println("----> Delete data by EntityCursor");
            cursor = pIndex.entities();

            try {
                Iterator<UserInfo4DPL> iterator = cursor.iterator();
                while (iterator.hasNext()){
                    UserInfo4DPL vo = iterator.next();
                    if ("007".equals(vo.getUserId())){
                        System.out.println("----> Delete object :" + vo);
                        iterator.remove();
                    }
                }
            }finally {
                cursor.close();
            }
        }catch (Exception e){
            System.out.println(e);
        }finally {
            if (store != null){
                store.close();
            }
            if (environment != null){
                environment.cleanLog();
                environment.close();
            }
        }
    }

    private List<UserInfo4DPL> initObject() {
        List<UserInfo4DPL> list = new ArrayList<UserInfo4DPL>();
        list.add(new UserInfo4DPL("ctosun", "Jack", "welcome to www.baidu.com"));
        list.add(new UserInfo4DPL("testKey", "Jack", "welcome to www.baidu.com"));
        list.add(new UserInfo4DPL("007", "Jack", "welcome to www.baidu.com"));
        return list;
    }

    public static void main(String[] args){
        System.out.println("====> Demo Test Start <====");
        DPLPrimaryIndexsDemo demo = new DPLPrimaryIndexsDemo();
        String path = "/home/andrew/practice/";
        String storeName = "DPL-demo";

        System.out.println("====> 测试数据 存储...");
        demo.testDPLPKeyPut(path, storeName);
        System.out.println();

        System.out.println("====> 测试数据 读取...");
        demo.testDPLPKeyRead(path, storeName);
        System.out.println();

        System.out.println("====> 测试数据 删除...");
        demo.testDPLPKeyDelete(path, storeName);
        System.out.println();

        System.out.println("====> Demo Test End!! <====");
    }
}
