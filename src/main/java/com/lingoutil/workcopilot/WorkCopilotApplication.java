package com.lingoutil.workcopilot;

import com.lingoutil.workcopilot.handler.CommandHandler;
import com.lingoutil.workcopilot.runner.CommandRunner;
import com.lingoutil.workcopilot.util.LogUtil;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static com.lingoutil.workcopilot.constant.Constant.LOG_MODE;
import static com.lingoutil.workcopilot.constant.Constant.MODE_VERBOSE;
import static com.lingoutil.workcopilot.util.LogUtil.RESET;
import static com.lingoutil.workcopilot.util.LogUtil.YELLOW;

public class WorkCopilotApplication {

    public static void main(String[] args) {
        // 设置控制台输出为UTF-8编码
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        if (args.length == 1) {
            if (CommandRunner.getOsType().equals(CommandRunner.MAC)) {
                runWithMultiModeOnUnix();
            }
            else {
                runWithMultiModeOnWin();
            }
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
            if (isIllegal(args)) {
                return;
            }
            String command = args[1];
            CommandHandler.execute(command, args);
            endTime = System.currentTimeMillis();
            LogUtil.log("duration: %s ms", endTime - startTime);
        }
        else {
            if (isIllegal(args)) {
                return;
            }
            String command = args[1];
            CommandHandler.execute(command, args);
        }
    }

    private static void runWithMultiModeOnUnix() {
        try {
            // 创建 JLine 终端和读取器
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .history(new DefaultHistory())
                    .build();

            LogUtil.info("Welcome to use work copilot \uD83D\uDE80 ~");
            String prompt = YELLOW + "copilot > " + RESET; // 设置为亮黄色
            while (true) {
                try {
                    // 显示提示符并读取输入
                    String input = reader.readLine(prompt);
                    String[] args = ("j " + input).split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                    boolean verboseMode = LOG_MODE.equals(MODE_VERBOSE);
                    if (verboseMode) {
                        LogUtil.log("verbose mode is start: %s", verboseMode);
                        long startTime = System.currentTimeMillis();
                        long endTime = 0;
                        if (isIllegal(args)) {
                            continue;
                        }
                        String command = args[1];
                        CommandHandler.execute(command, args);
                        endTime = System.currentTimeMillis();
                        LogUtil.log("duration: %s ms", endTime - startTime);
                    }
                    else {
                        if (isIllegal(args)) {
                            continue;
                        }
                        String command = args[1];
                        CommandHandler.execute(command, args);
                    }
                    System.out.println();
                } catch (UserInterruptException e) {
                    LogUtil.info("\nProgram interrupted. Use 'exit' to quit.");
                } catch (EndOfFileException e) {
                    LogUtil.info("\nGoodbye!");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runWithMultiModeOnWin() {
        Scanner sc = new Scanner(System.in);
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
                if (isIllegal(args)) {
                    continue;
                }
                String command = args[1];
                CommandHandler.execute(command, args);
                endTime = System.currentTimeMillis();
                LogUtil.log("duration: %s ms", endTime - startTime);
            }
            else {
                if (isIllegal(args)) {
                    continue;
                }
                String command = args[1];
                CommandHandler.execute(command, args);
            }
            System.out.println();
        }
    }

    private static boolean isIllegal(String... args) {
        int len = args.length;
        LogUtil.log("command line argument length: %s", len);
        if (len != 2 && len != 3 && len != 4 && len != 5) {
            LogUtil.error("Invalid number of arguments, arguments len: %s\n", len);
            return true;
        }
        return false;
    }
}
