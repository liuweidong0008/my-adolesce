package com.adolesce.common.utils.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class EnumUtils {
    /**
     * @param clazz
     * @return
     */
    public static <T extends Enum<T> & IntEnum> Map<Integer, T> mapIntEnum(
            Class<T> clazz) {
        Map<Integer, T> instanceMap = new HashMap<Integer, T>();
        EnumSet<T> values = EnumSet.allOf(clazz);
        for (T value : values) {
            instanceMap.put(value.getId(), value);
        }
        return instanceMap;
    }

    /**
     * @param clazz
     * @return
     */
    public static <T extends Enum<T> & StrEnum> Map<String, T> mapStrEnum(
            Class<T> clazz) {
        Map<String, T> instanceMap = new HashMap<String, T>();
        EnumSet<T> values = EnumSet.allOf(clazz);
        for (T value : values) {
            instanceMap.put(value.getId(), value);
        }
        return instanceMap;
    }
}