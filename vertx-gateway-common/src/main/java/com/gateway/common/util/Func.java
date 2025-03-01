package com.gateway.common.util;

import org.springframework.lang.Nullable;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 描述：
 *
 * @author huxuehao
 **/
public class Func {
    private static final Map<String, String> EVN_VALUES_CACHE = new HashMap<>();

    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Optional) {
            return !((Optional<?>)obj).isPresent();
        } else if (obj instanceof CharSequence) {
            return ((CharSequence)obj).length() == 0;
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection) {
            return ((Collection<?>)obj).isEmpty();
        } else {
            return obj instanceof Map && ((Map<?, ?>) obj).isEmpty();
        }
    }

    public static boolean isNotEmpty(@Nullable Object obj) {
        return !isEmpty(obj);
    }

    public static boolean isEmpty(@Nullable Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(@Nullable Object[] array) {
        return !isEmpty(array);
    }
    public static String getEvn(@Nullable String name) {
        String cacheValue = EVN_VALUES_CACHE.get(name);
        if (cacheValue != null) {
            return cacheValue;
        }

        String value = System.getenv(name);
        if (value != null) {
            EVN_VALUES_CACHE.put(name, value);
        }
        return value;
    }
    public static String getEvn(@Nullable String name, String defaultVal) {
        String evn = getEvn(name);
        return evn == null ? defaultVal : evn;
    }

    public static String currentDataTime() {
        Date date = new Date();
        return getDataTime(date);
    }
    public static String getDataTime(Date date) {
        // 创建 SimpleDateFormat 并设置时区为东八区
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        // 格式化日期
        return sdf.format(date);
    }
}
