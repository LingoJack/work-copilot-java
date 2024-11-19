package com.lingoutil.workcopilot.util;

import static com.lingoutil.workcopilot.constant.Constant.LOG_MODE;
import static com.lingoutil.workcopilot.constant.Constant.MODE_CONCISE;

public class LogUtil {

    public static void info(String str, Object... args) {
        System.out.printf(str, args);
        System.out.println();
    }

    public static void info(boolean convertPresentSigns, String str, Object... args) {
        if (convertPresentSigns) {
            str = escapePercentSigns(str);
        }
        System.out.printf(str, args);
        System.out.println();
    }

    public static void log(String str, Object... args) {
        if (LOG_MODE.equals(MODE_CONCISE)) {
            return;
        }
        System.out.printf(str, args);
        System.out.println();
    }

    public static void logError(String str, Object... args) {
        if (LOG_MODE.equals(MODE_CONCISE)) {
            return;
        }
        System.err.printf("[ERROR] " + str, args);
        System.err.println();
    }

    public static void error(String str, Object... args) {
        System.err.printf("[ERROR] " + str, args);
        System.err.println();
    }

    public static void usage(String str, Object... args) {
        System.out.printf("Usage: " + str, args);
        System.out.println();
    }

    public static void printLine() {
        System.out.println("---------------------------------------------");
    }

    private static String escapePercentSigns(String str) {
        return str.replace("%", "%%");
    }

     public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
