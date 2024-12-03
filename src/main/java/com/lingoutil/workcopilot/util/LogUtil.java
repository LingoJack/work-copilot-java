package com.lingoutil.workcopilot.util;

import static com.lingoutil.workcopilot.constant.Constant.LOG_MODE;
import static com.lingoutil.workcopilot.constant.Constant.MODE_CONCISE;

public class LogUtil {

    public static final String RESET = "\u001B[0m";

    public static final String BLUE = "\u001B[34m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[93m";
    public static final String RED = "\u001B[31m";

    public static void printfWithColor(String str, String colorCode, Object... args) {
        String formattedStr = String.format(str, args);
        System.out.print(colorCode + formattedStr + RESET);
    }

    public static void printWithColor(String str, String colorCode) {
        System.out.print(colorCode + str + RESET);
    }

    public static String highlight(String str, String colorCode) {
        return colorCode + str + RESET;
    }

    public static void print(String str) {
        System.out.print(str);
    }

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
        if (!LOG_MODE.equals(MODE_CONCISE)) {
            System.out.printf(str, args);
            System.out.println();
        }
    }

    public static void logError(String str, Object... args) {
        if (!LOG_MODE.equals(MODE_CONCISE)) {
            printWithColor("[ERROR] ", RED);
            System.out.printf(str, args);
            System.err.println();
        }
    }

    public static void error(String str, Object... args) {
        printWithColor("[ERROR] ", RED);
        System.out.printf(str, args);
        System.err.println();
    }

    public static void usage(String str, Object... args) {
        printWithColor("💡 Usage: ", GREEN);
        System.out.printf(str, args);
        System.out.println();
    }

    public static void printLine() {
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - -");
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

    public static void newLine() {
        System.out.println();
    }
}
