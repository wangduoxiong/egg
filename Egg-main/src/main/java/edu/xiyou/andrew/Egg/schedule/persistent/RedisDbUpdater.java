package edu.xiyou.andrew.Egg.schedule.persistent;

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

import edu.xiyou.andrew.Egg.parse.CrawlDatum;
import edu.xiyou.andrew.Egg.utils.Config;
import edu.xiyou.andrew.Egg.utils.NumberTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by andrew on 15-3-28.
 */
public class RedisDbUpdater implements DbUpdater{
    private Jedis jedis;
    private static Logger logger = LoggerFactory.getLogger(RedisDbUpdater.class);

    {
        jedis = RedisFactory.getRedisInstance();
    }

    @Override
    public void write2Visted(CrawlDatum datum){
        if (datum == null){
            return;
        }

        jedis.hset("visited", datum.getUrl(), datum.getFetchTime() + "");
    }

    @Override
    public void write2Visited(Map<String, String> map){
        jedis.hmset("visited", map);
    }

    @Override
    public void write2Links(String... urls){
        for (String url : urls) {
            System.out.println(url);
            jedis.lpush("links", url);
        }
    }

    @Override
    public void write2Datums(String... urls){
        for (String url : urls){
            jedis.lpush("datums", url);
        }
    }

    @Override
    public List<String> readFromLinks(){
        return jedis.lrange("links", 0, -1);
    }

    @Override
    public List<String> readFromDatums(){
        return jedis.lrange("datums", 0, -1);
    }

    @Override
    public Map<String, String> readFromVisited(){
        return jedis.hgetAll("visited");
    }

    public void delDatums(){
        jedis.del("datums");
    }

    public void merge(){
        logger.info("----> merge start...");
        while (jedis.llen("links") > 0){
            jedis.lpush("datums", jedis.lpop("links"));
        }

        System.out.println(jedis.lrange("datums", 0, -1));
        if (Config.interval != Config.JUST_ONE) {
            Map<String, String> visitedMap = readFromVisited();
            Iterator<Map.Entry<String, String>> visitedIter = visitedMap.entrySet().iterator();
            while (visitedIter.hasNext()) {
                Map.Entry<String, String> entry = visitedIter.next();
                if (System.currentTimeMillis() - Long.valueOf(entry.getValue()) > Config.interval) {
                    jedis.lpush("datums", entry.getKey());
                    jedis.hdel("visited", entry.getKey());
                }
            }
        }

        logger.info("----> merge end....");
    }

    public void close(){
        RedisFactory.returnResource(jedis);
    }

    public void flushAll(){
        jedis.flushAll();
    }

    public void flushDB(){
        jedis.flushDB();
    }

    public static void main(String[] args) throws InterruptedException {
        RedisDbUpdater updater = new RedisDbUpdater();
        updater.flushAll();
        updater.write2Links("111");
        updater.write2Links("222");
        System.out.println(updater.readFromLinks());
        Map<String, String> map = new HashMap<String, String>();
        map.put("333", System.currentTimeMillis() + "");
        Thread.sleep(20);
        map.put("444", System.currentTimeMillis() + "");
        Thread.sleep(40);
        updater.write2Visited(map);

        updater.write2Visted(new CrawlDatum("333", System.currentTimeMillis()));
        Thread.sleep(20);
        updater.write2Visted(new CrawlDatum("444", System.currentTimeMillis()));
        Thread.sleep(40);

        updater.merge();
        System.out.println(updater.readFromDatums());
    }
}
