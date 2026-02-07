package com.hxh.gateway.common.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 描述：自定义工具类
 *
 * @author huxuehao
 **/
public class MeUtil {
    private static final String datetimeFormat = "yyyy-MM-dd HH:mm:ss";
    private static final String dateFormat = "yyyy-MM-dd";

    private static final String INT = "int";
    private static final String BIGINT = "bigint";
    private static final String DECIMAL = "decimal";
    private static final String DATE = "date";
    private static final String DATETIME = "datetime";
    private static final String VARCHAR = "varchar";

    private static final String salt = "dcs-1a2b3c4d5f-job";

    public static String String2SqlType(String str) {
        if (isInt(str)) {
            return INT;
        } else if (isBigint(str)) {
            return BIGINT;
        } else if(isDecimal(str)) {
            return DECIMAL;
        } else if(isDatetime(str)) {
            return DATETIME;
        } else if (isDate(str)) {
            return DATE;
        } else {
            return VARCHAR;
        }
    }
    private static boolean isInt(String str) {
        return str != null && str.matches("^[1-9]\\d{1,10}$");
    }
    private static boolean isBigint(String str) {
        return str != null && str.matches("^[1-9][0-9]{11,19}$");
    }
    private static boolean isDecimal(String str) {
        return str != null && str.matches("^[1-9]*\\.[1-9]*$");
    }
    private static boolean isDate(String str) {
        if (str.matches("^[1-9][0-9]{3}-[0-9]{1,2}-[0-9]{1,2}$")){
            return true;
        } else if (str.matches("^[0-9]{1,2}-[0-9]{1,2}$")) {
            return true;
        } else if (str.matches("^[1-9][0-9]{3}/[0-9]{1,2}/[0-9]{1,2}$")){
            return true;
        } else if (str.matches("^[0-9]{1,2}/[0-9]{1,2}$")) {
            return true;
        } else if (str.matches("^[1-9][0-9]{3}年[0-9]{1,2}月[0-9]{1,2}日$")){
            return true;
        } else return str.matches("^[0-9]{1,2}月[0-9]{1,2}日$");
    }
    private static boolean isDatetime(String str) {
        if (str.matches("^[1-9][0-9]{3}-[0-9]{1,2}-[0-9]{1,2}\\s[0-9]{2}:[0-9]{2}:[0-9]{2}||[1-9][0-9]{3}-[0-9]{1,2}-[0-9]{1,2}\\s[0-9]{2}:[0-9]{2}$")){
            return true;
        } else if (str.matches("^[1-9][0-9]{3}/[0-9]{1,2}/[0-9]{1,2}\\s[0-9]{2}:[0-9]{2}:[0-9]{2}||[1-9][0-9]{3}/[0-9]{1,2}/[0-9]{1,2}\\s[0-9]{2}:[0-9]{2}$")){
            return true;
        } else return str.matches("^[1-9][0-9]{3}年[0-9]{1,2}月[0-9]{1,2}日\\s[0-9]{2}时[0-9]{2}分[0-9]{2}秒||[1-9][0-9]{3}年[0-9]{1,2}月[0-9]{1,2}日\\s[0-9]{2}时[0-9]{2}分$");
    }

    /**
     * 匹配子串在父串中最后一次出现的位置
     * @param parentString 父串
     * @param subString 子串
     * @return 最后一次出现的位置的索引
     */
    public static Integer lastAppearLocation(String parentString, String subString){
        if (parentString == null || subString == null) {
            return -1;
        } else {
            int index = 0;
            String[] split = parentString.split(subString);
            int size = split.length;
            if (parentString.endsWith(subString)){ /* 子串在末尾 */
                return parentString.length()-subString.length();
            }else if (size == 1) { /* 父串中只存在一个子串 */
                if (parentString.startsWith(subString)){ /* 子串在头 */
                    return 0;
                } else {  /* 子串在尾 */
                    return split[0].length();
                }
            } else if(size == 2){ /* 存在一个子串在中间 */
                return split[0].length();
            }else { /* 存在多个子串 */
                for (int i =0 ;i < size-1; i++) {
                    index += split[i].length();
                }
                index += (size-2)*(subString.length());
            }
            return index;
        }
    }

    /**
     * KMP算法：匹配子串在父串中第一次出现的位置
     * @param parentString 父串
     * @param subString  子串
     * @return 第一次出现的位置的索引
     */
    public static Integer firstAppearLocation(String parentString, String subString) {
        if(parentString == null || subString ==null) return -1;
        if(parentString.length() < subString.length()) return -1;
        if(parentString.length() == subString.length()) {
            /* 当长度相等且数值相等时 */
            if(parentString.equals(subString)) return 0;
            else return -1;
        }

        return getLocate(parentString, subString);
    }
    /**
     * 返回subStr在str中出现的首个位置
     */
    private static int getLocate(String str, String subStr) {
        /* 记录“被匹配字符串”前移的位置数 */
        int locate = 0;
        /* 记录“被匹配字符串”前移后的字符串 */
        String afterMoveStr;

        /* 获取 str 和 subStr匹配成功的字符串 */
        String partStr = getSameStr(str, subStr, 0);
        /* 如果partStr（已匹配字符串） == subStr（匹配字符串），那么匹配成功 */
        while(!partStr.equals(subStr)) {
            /* 如果已匹配的字符串的长度小于2，那么不可能产生“部分匹配值”，既需要“被匹配字符串”前移1位即可 */
            if(partStr.length() <= 1) {
                locate++;
                afterMoveStr = str.substring(locate); /* “被匹配字符串”前移1位即可 */
                /* 如果前移后的字符串长度小于“匹配字符串”，既为匹配失败，否自继续匹配 */
                if(afterMoveStr.length()>=subStr.length())
                    partStr = getSameStr(afterMoveStr, subStr, 0);
                else
                    return -1;
            }else { /* 如果已匹配的字符串的长度大于2，那么可能产生“部分匹配值” */
                /* /获取"部分匹配值" */
                int partialMatchVal = getPartialMatchVal(partStr);
                /* 计算“被匹配字符串”一共需要前移的位置 */
                locate += (partStr.length()-partialMatchVal);
                afterMoveStr = str.substring(locate); /* 前移 */
                /* 如果前移后的字符串长度小于“匹配字符串”，既为匹配失败，否自继续匹配 */
                if(afterMoveStr.length()>=subStr.length())
                    /* 获取afterMoveStr与subStr字符串相同字符串 */
                    partStr = getSameStr(afterMoveStr, subStr,partialMatchVal);
                else
                    return -1;
            }
        }
        return locate;
    }
    /**
     * 计算"部分匹配值"
     * @param partStr ： 已匹配的字符串
     */
    private static int getPartialMatchVal(String partStr) {
        int nums = 0; /* 存放"部分匹配值" */
        List<String> list1 = new ArrayList<>(); /* 存放所有的前缀 */
        List<String> list2 = new ArrayList<>(); /* 存放所有的后缀 */
        /* 从头到尾和从尾到头进行扫描（排除首尾的扫描） */
        for (int i=0,j=partStr.length()-1; i<partStr.length()-1 && j>0; i++,j--) {
            list1.add(partStr.substring(0, i+1)); /* 获取前缀字符串 */
            list2.add(partStr.substring(j)); /* 获取后缀字符串 */
        }
        /* 比较最大相同前缀（部分匹配值） */
        for (int i = 0; i < list1.size(); i++) {
            if(list1.get(i).equals(list2.get(i)) && list1.get(i).length()>nums) {
                nums = list1.get(i).length();
            }
        }
        return nums;
    }
    /**
     * 计算str  和  subStr 中已匹配的字符串
     * @param str ： 被匹配字符串
     * @param subStr 匹配字符串
     * @param partialMatchVal 开始时匹配的位置
     */
    private static String getSameStr(String str, String subStr, int partialMatchVal) {
        /* 根据KMP算法的特点，索引0~（partialMatchVal-1）的字符串肯定是相等的，先将其加入sameStr */
        String sameStr = subStr.substring(0,partialMatchVal);

        /* 从索引partialMatchVal开始比较两个字符串，如果有相等的字符，则追加到sameStr中。 */
        for (int i = partialMatchVal; i < subStr.length(); i++) {
            if(str.charAt(i) == subStr.charAt(i)) {
                sameStr += subStr.charAt(i);
            }else {
                break;
            }
        }
        return sameStr;
    }

    /**
     * 判断对象是否为空
     */
    public static Boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return ((String) obj).trim().length() == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        if (obj instanceof Optional) {
            return !((Optional<?>)obj).isPresent();
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence)obj).length() == 0;
        }
        return false;
    }

    public static boolean hasEmpty(Object... os) {
        for (Object o : os) {
            if (isEmpty(o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对象转map
     *
     * @param o 对象
     */
    public static Map<String,Object> ObjectToMap(Object o) {
        return JSON.parseObject(JSON.toJSONString(o), new TypeReference<Map<String, Object>>(){});
    }

    /**
     * 当前sql类型Timestamp的当前时间
     */
    public static Timestamp currentTimestamp() {
        return new Timestamp(new Date().getTime());
    }

    /**
     * 数据库datetime转String
     */
    public static String sqlTimeForm(Timestamp sqlDateTime) {
        return new SimpleDateFormat(datetimeFormat).format(sqlDateTime);
    }

    /** 时间戳格式化 */
    public static String timeStampForm(long lo) {
        return new SimpleDateFormat(datetimeFormat).format(new Date(lo));
    }
    /**
     * 将数据库 Timestamp 格式化 (yyyy-MM-dd HH:mm:ss)
     */
    public static String sqlTimestampForm(Timestamp sqlDateTime, String datetimeFormat) {
        return new SimpleDateFormat(datetimeFormat).format(sqlDateTime);
    }

    /**
     * 获取当前已经格式化后的时间 (yyyy-MM-dd HH:mm:ss)
     */
    public static String currentDatetime() {
        return currentDatetime(datetimeFormat);
    }

    public static String currentDatetime(String timeFormat) {
        return sqlTimestampForm(currentTimestamp(), timeFormat);
    }

    /**
     * 获取当前已经格式化后的日期 (yyyyMMdd)
     */
    public static String currentDate() {
        Timestamp timestamp = currentTimestamp();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(timestamp);
    }

    /**
     * 使用默认的加盐值进行MD5加密
     * @param inputPass  原始密码
     * @return 加密后的字符串
     */
    public static String md5(String inputPass) {
        String str = String.valueOf(salt.charAt(0))+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
        return DigestUtils.md5Hex(str);
    }

    /**
     * 使用自定义的盐值进行MD5加密
     * @param inputPass  原始密码
     * @param salt 盐值
     * @return 加密后的字符串
     */
    public static String md5(String inputPass, String salt) {
        String str = String.valueOf(salt.charAt(0)) + salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
        return DigestUtils.md5Hex(str);
    }

    /**
     * 获取UUID
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * 获取异常栈信息
     */
    public static String catchExceptionStackInfo(Exception e) {
        if (e == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
    /**
     * 获取异常栈信息
     */
    public static String catchThrowableStackInfo(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 线程休眠
     * @param millis 毫秒数
     */
    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 创建线程池
     * @param corePoolSize  核心线程数
     * @param maxPoolSize   最大线程数
     * @param capacity      排队人数
     * @param threadPrefix  线程前缀
     */
    public static ThreadPoolTaskExecutor createThreadPool(int corePoolSize, int maxPoolSize, int capacity, String threadPrefix){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(capacity);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        if (threadPrefix != null) {
            executor.setThreadNamePrefix(threadPrefix);
        }
        executor.initialize();
        return executor;
    }


}
