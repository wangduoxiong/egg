package edu.xiyou.andrew.Egg.utils;

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

/**
 * 对数操作的一些工具方法
 * Created by andrew on 15-3-28.
 */
public class NumberTools {
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
}
