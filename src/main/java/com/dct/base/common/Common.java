package com.dct.base.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Common {

    private static final Logger log = LoggerFactory.getLogger(Common.class);
    private static final String ENTITY_NAME = "Common";

    public static <T> Map<String, Class<?>> getObjectFields(Class<T> mappingClass) {
        Map<String, Class<?>> fieldMap = new HashMap<>();

        for (Field field : mappingClass.getDeclaredFields()) {
            fieldMap.put(field.getName(), field.getType());
        }

        return fieldMap;
    }
}
