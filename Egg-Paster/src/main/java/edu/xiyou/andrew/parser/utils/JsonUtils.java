package edu.xiyou.andrew.parser.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**处理Json的工具类
 * Created by duoxiongwang on 15-8-23.
 */
public class JsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String object2JsonString(Object object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
    }

    public static byte[] object2JsonBytes(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(object);
    }

    public static void object2JsonFile(File file, Object object) throws IOException {
        objectMapper.writeValue(file, object);
    }

    public static void object2JsonOutputStream(OutputStream outputStream, Object object) throws IOException {
        objectMapper.writeValue(outputStream, object);
    }

    public static Object jsonString2Object(String json, Class clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    public static Object jsonBytes2Object(byte[] json, Class clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }


    public static Object jsonInputStream2Object(InputStream json, Class clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    public static Object jsonFile2Object(File file, Class clazz) throws IOException {
        return objectMapper.readValue(file, clazz);
    }
}
