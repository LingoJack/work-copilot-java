package com.lingoutil.workcopilot;

import com.lingoutil.workcopilot.handler.CommandHandler;
import com.lingoutil.workcopilot.util.LogUtil;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static com.lingoutil.workcopilot.constant.Constant.*;
import static com.lingoutil.workcopilot.util.LogUtil.YELLOW;

public class WorkCopilotApplication {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 设置控制台输出为UTF-8编码
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        if (args.length == 1) {
            // just j command
            runWithMultiMode(sc);
        }
        else {
            // j another argument
            runWithSingleMode(args);
        }
    }

    private static void runWithSingleMode(String[] args) {
        boolean verboseMode = LOG_MODE.equals(MODE_VERBOSE);
        if (verboseMode) {
            LogUtil.log("verbose mode is start: %s", verboseMode);
            long startTime = System.currentTimeMillis();
            long endTime = 0;
            if (!isValidArgsNum(args)) {
                return;
            }
            String command = args[1];
            CommandHandler.execute(command, args);
            endTime = System.currentTimeMillis();
            LogUtil.log("duration: %s ms", endTime - startTime);
        }
        else {
            if (!isValidArgsNum(args)) {
                return;
            }
            String command = args[1];
            CommandHandler.execute(command, args);
        }
    }

    private static void runWithMultiMode(Scanner sc) {
        String[] args;
        LogUtil.info("Welcome to use work copilot \uD83D\uDE80 ~");
        while (true) {
            LogUtil.printWithColor("copilot > ", YELLOW);
            args = ("j " + sc.nextLine()).split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            boolean verboseMode = LOG_MODE.equals(MODE_VERBOSE);
            if (verboseMode) {
                LogUtil.log("verbose mode is start: %s", verboseMode);
                long startTime = System.currentTimeMillis();
                long endTime = 0;
                if (!isValidArgsNum(args)) {
                    continue;
                }
                String command = args[1];
                CommandHandler.execute(command, args);
                endTime = System.currentTimeMillis();
                LogUtil.log("duration: %s ms", endTime - startTime);
            }
            else {
                if (!isValidArgsNum(args)) {
                    continue;
                }
                String command = args[1];
                CommandHandler.execute(command, args);
            }
            System.out.println();
        }
    }

    private static boolean isValidArgsNum(String[] args) {
        int len = args.length;
        LogUtil.log("command line argument length: %s", len);
        if (len != 2 && len != 3 && len != 4 && len != 5) {
            LogUtil.error("Invalid number of arguments, arguments len: %s\n", len);
            return false;
        }
        return true;
    }
}
