package com.map_lib.util.common;

public class StrUtil {
    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    /**
     * 蛇形命名法
     */
    public static String toSnakeCase(String s) {
        return nameConvert(s, NameConverter.SNAKE_CASE);
    }

    /**
     * 小驼峰命名法
     */
    public static String toCamelCase(String s) {
        return nameConvert(s, NameConverter.CAMEL_CASE);
    }

    /**
     * 大驼峰命名法
     */
    public static String toPascalCase(String s) {
        return nameConvert(s, NameConverter.PASCAL_CASE);
    }

    private static String nameConvert(String s, NameConverter converter) {
        return converter.joinWords(NameConverter.splitName(s));
    }
}
