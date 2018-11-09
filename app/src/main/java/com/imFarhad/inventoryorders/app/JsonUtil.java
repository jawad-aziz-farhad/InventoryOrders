package com.imFarhad.inventoryorders.app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;


/**
 * Created by Farhad on 11/10/2018.
 */

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T fromJson(byte[] value, Class<T> clas) throws Exception {
        return objectMapper.readValue(value, clas);
    }
    public static <T> T fromJson(String value, Class<T> clas) throws Exception {
        return objectMapper.readValue(value, clas);
    }

    public static String asJson(String value) throws Exception{
        return objectMapper.writeValueAsString(value);
    }

    public static <T> T convert(Map<String, Object> value, Class<T> clas) throws Exception{
        return objectMapper.convertValue(value, clas);
    }
}
