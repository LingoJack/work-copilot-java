package com.lingoutil.workcopilot;

import com.lingoutil.workcopilot.handler.CommandHandler;
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

import static com.lingoutil.workcopilot.constant.Constant.LOG_MODE;
import static com.lingoutil.workcopilot.constant.Constant.MODE_VERBOSE;
import static com.lingoutil.workcopilot.util.LogUtil.RESET;
import static com.lingoutil.workcopilot.util.LogUtil.YELLOW;

public class WorkCopilotApplication {

    public static void main(String[] args) {
        // 设置控制台输出为UTF-8编码
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        if (args.length == 1) {
            // just j command
            runWithMultiMode();
        } else {
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
        } else {
            if (!isValidArgsNum(args)) {
                return;
            }
            String command = args[1];
            CommandHandler.execute(command, args);
        }
    }

    private static void runWithMultiMode() {
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
                        if (!isValidArgsNum(args)) {
                            continue;
                        }
                        String command = args[1];
                        CommandHandler.execute(command, args);
                        endTime = System.currentTimeMillis();
                        LogUtil.log("duration: %s ms", endTime - startTime);
                    } else {
                        if (!isValidArgsNum(args)) {
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
