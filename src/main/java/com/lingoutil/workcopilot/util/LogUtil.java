package com.lingoutil.workcopilot.util;

import static com.lingoutil.workcopilot.constant.Constant.LOG_MODE;
import static com.lingoutil.workcopilot.constant.Constant.MODE_VERBOSE;

public class LogUtil {

    public static final String RESET = "\u001B[0m";

    public static final String BLUE = "\u001B[34m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[93m";
    public static final String RED = "\u001B[31m";

    /**
     * 打印带颜色文本
     * @param str
     * @param colorCode
     * @param args
     */
    public static void printfWithColor(String str, String colorCode, Object... args) {
        String formattedStr = String.format(str, args);
        System.out.print(colorCode + formattedStr + RESET);
    }

    public static void printWithColor(String str, String colorCode) {
        System.out.print(colorCode + str + RESET);
    }

    /**
     * 高亮文本
     * @param str
     * @param colorCode
     * @return
     */
    public static String highlight(String str, String colorCode) {
        return colorCode + str + RESET;
    }

    /**
     * 打印文本
     * @param str
     */
    public static void print(String str) {
        System.out.print(str);
    }

    /**
     * 打印日志信息
     * @param str
     * @param args
     */
    public static void info(String str, Object... args) {
        System.out.printf(str, args);
        System.out.println();
    }

    /**
     * 打印日志信息
     * @param convertPresentSigns 是否转换百分号
     * @param str
     * @param args
     */
    public static void info(boolean convertPresentSigns, String str, Object... args) {
        if (convertPresentSigns) {
            str = escapePercentSigns(str);
        }
        info(str, args);
    }

    /**
     * 打印日志信息，仅在verbose模式下输出
     * @param str
     * @param args
     */
    public static void log(String str, Object... args) {
        if (LOG_MODE != null && LOG_MODE.equals(MODE_VERBOSE)) {
            System.out.printf(str, args);
            System.out.println();
        }
    }

    /**
     * 打印错误信息，仅在verbose模式下输出
     * @param str
     * @param args
     */
    public static void logError(String str, Object... args) {
        if (LOG_MODE != null && LOG_MODE.equals(MODE_VERBOSE)) {
            error(str, args);
        }
    }

    public static void error(String str, Object... args) {
        printWithColor("[ERROR] ", RED);
        System.out.printf(str, args);
        System.err.println();
    }

    /**
     * 打印提示信息
     *
     * @param str
     * @param args
     */
    public static void usage(String str, Object... args) {
        printWithColor("💡 Usage: ", GREEN);
        System.out.printf(str, args);
        System.out.println();
    }

    public static void printLine() {
        System.out.println("- - - - - - - - - - - - - - - - - - - - - - -");
    }

    /**
     * 转换百分号，避免被格式化参数替换
     *
     * @param str
     * @return
     */
    private static String escapePercentSigns(String str) {
        return str.replace("%", "%%");
    }

    /**
     * 首字母大写
     *
     * @param str
     * @return
     */
    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 换行
     */
    public static void newLine() {
        System.out.println();
    }
}
