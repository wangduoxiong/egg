package edu.xiyou.andrew.Egg.scheduler.dbUpdater;

/*
 * Copyright (c) 2015 Andrew-Wang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import edu.xiyou.andrew.Egg.net.CrawlDatum;
import edu.xiyou.andrew.Egg.utils.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by andrew on 15-6-6.
 */
public class JedisDbUpdater implements DbUpdater{
    private Jedis jedis = JedisFactory.getRedisInstance();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public final int POPSIZE = 1000;

    public Map<String, String> readFromVisited() {
        return jedis.hgetAll(DbUpdater.VISITED_DB);
    }

    public List<String> readFromLinks() {
        return jedis.lrange(DbUpdater.LINKS_DB, 0, -1);
    }

    @Override
    public List<String> readFromDatums() {
        List<String> list = jedis.lrange(DbUpdater.DATUMS_DB, 0, POPSIZE - 1);
        jedis.ltrim(DbUpdater.DATUMS_DB, POPSIZE, -1);
        return list;
    }

    @Override
    public void write2Visited(Map<String, String> map) {
        jedis.hmset(DbUpdater.VISITED_DB, map);
    }

    @Override
    public void write2Visited(List<CrawlDatum> datums) {
        Map<String, String> map = new HashMap<String, String>((int)(datums.size() / 0.75));
        for (CrawlDatum datum : datums){
            map.put(datum.getUrl(), "" + datum.getFetchTime());
        }
        jedis.hmset(DbUpdater.VISITED_DB, map);
    }

    @Override
    public void write2Links(List<String> urls) {
        jedis.lpush(DbUpdater.LINKS_DB, urls.toArray(new String[]{"1"}));
    }

    @Override
    public void write2Datums(List<String> urls) {
        jedis.lpush(DbUpdater.DATUMS_DB, urls.toArray(new String[]{"1"}));
    }

    @Override
    public void merge() {
        logger.info("----> merge start <----");
        logger.info("---->start merge linksDB and datumsDB <----");
        while (jedis.llen(DbUpdater.LINKS_DB) > 0){
            jedis.rpoplpush(DbUpdater.LINKS_DB, DbUpdater.DATUMS_DB);
        }
        jedis.del(DbUpdater.LINKS_DB);

        logger.info("----> merge linksDB and datumsDB over <----");

        if (Config.interval != Config.INTERVAL_JUST_ONE){
            logger.info("----> start merge visitedDB and datumsDB <----");
            Map<String, String> map = jedis.hgetAll(DbUpdater.VISITED_DB);
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, String> entry = iterator.next();
                long lastFetchTime = Long.valueOf(entry.getValue());
                if ((System.currentTimeMillis() - lastFetchTime) > Config.interval){
                    jedis.hdel(DbUpdater.VISITED_DB, entry.getKey());
                    jedis.lpush(DbUpdater.DATUMS_DB, entry.getKey());
                }
            }
            logger.info("----> merge visitedDB and datumsDB end <----");
        }

        logger.info("----> merge over <----");
    }

    public void close(){
        JedisFactory.returnResource(jedis);
    }

    public void flushAll(){
        jedis.flushAll();
    }

    public void flushDB(){
        jedis.flushDB();
    }

    public static void main(String[] args) {

      /*  List<String> list = Arrays.asList("1", "2");
        String[] array = {"3", "4"};
        System.out.println(Arrays.asList(list.toArray(array)));*/

        JedisDbUpdater updater = new JedisDbUpdater();
        updater.flushDB();
        List<String> listDatums = new ArrayList<String>(20);
        listDatums.add("0");
        updater.write2Datums(listDatums);
        System.out.println("=====> datums:   " + updater.readFromDatums());

        List<String> listLinks = new ArrayList<String>(4);
        listLinks.addAll(Arrays.asList("1", "2", "3"));
        updater.write2Links(listLinks);
        System.out.println("=====> linklists:   " + updater.jedis.llen(DbUpdater.LINKS_DB));

        System.out.println("=====> links:   " + updater.readFromLinks());

        updater.merge();
        System.out.println("=====> datums:   " + updater.readFromDatums());
        System.out.println("=====> links:   " + updater.readFromLinks());

//        List<String> listDatums = new ArrayList<String>(20);
//        listDatums.addAll(Arrays.asList("1", "2", "3"));
//        updater.write2Datums(listDatums);
//        System.out.println("=====> datums:   " + updater.readFromDatums());

    }
}
