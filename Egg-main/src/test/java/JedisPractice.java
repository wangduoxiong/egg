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

import edu.xiyou.andrew.Egg.utils.NumberTools;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrew on 15-3-28.
 */
public class JedisPractice {
    private static JedisPool pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1");
    private static Jedis jedis = pool.getResource();

    public static void stringOperation(){
        jedis.set("name", "无源");
        System.out.println(jedis.get("name"));

        jedis.append("name", "之水");
        System.out.println(jedis.get("name"));

        jedis.mset("name1", "fuck", "name2", "you");
        System.out.println(jedis.mget("name1", "name2"));
    }

    public static void listOperation(){
        jedis.flushDB();
        jedis.rpush("list", "1");
        jedis.rpush("list", "2");
        jedis.rpush("list", "3");
        jedis.rpush("list", "4");
        jedis.lpush("list", "5");
        jedis.rpush("list", "6");
        jedis.rpush("list", "7");
        jedis.rpush("list", "8");

        System.out.println(jedis.lpop("list"));
        System.out.println(jedis.lrange("list", 0, -1));

        jedis.rpush("list","1");
        jedis.rpush("list","2");
        jedis.rpush("list","3");
        System.out.println(jedis.lrange("list", 0, -1));

        System.out.println(jedis.ltrim("list", 0, 2));
        System.out.println(jedis.lrange("list", 0, -1));

    }

    public static void mapOperation(){
        Map<String, String> map = new HashMap<String, String>();

        map.put("name", "蜗牛");
        map.put("name1", "会飞");
        System.out.println(jedis.hmset("map", map));
        System.out.println(jedis.hlen("map"));
        System.out.println(jedis.hkeys("map"));
        System.out.println(jedis.hvals("map"));
        System.out.println(jedis.hmget("map", "name", "name1"));

    }

    public static void main(String[] args) {
//        stringOperation();
        listOperation();
//        byte[] bytes = new byte[8];
//        NumberTools.long2Bytes(101001010, bytes);
//        mapOperation();
    }
}
