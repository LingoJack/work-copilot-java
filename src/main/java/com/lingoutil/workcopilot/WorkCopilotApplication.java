package com.lingoutil.workcopilot;

import com.lingoutil.workcopilot.completer.ConfigCompleter;
import com.lingoutil.workcopilot.completer.ConfigItemCompleter;
import com.lingoutil.workcopilot.completer.RefreshableAliasCompleter;
import com.lingoutil.workcopilot.handler.CommandHandler;
import com.lingoutil.workcopilot.runner.CommandRunner;
import com.lingoutil.workcopilot.util.LogUtil;
import org.jline.builtins.Completers;
import org.jline.reader.*;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.widget.AutopairWidgets;
import org.jline.widget.AutosuggestionWidgets;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static com.lingoutil.workcopilot.constant.Constant.*;
import static com.lingoutil.workcopilot.util.LogUtil.RESET;
import static com.lingoutil.workcopilot.util.LogUtil.YELLOW;
import static com.lingoutil.workcopilot.util.LogUtil.info;

public class WorkCopilotApplication {

    public static void main(String[] args) {

        // 启动的脚本是 j
        // 例如 j <args...>

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        if (args.length == 0) {
            // 交互模式
            switch (CommandRunner.getOsType()) {
                case CommandRunner.MAC -> {
                    LogUtil.log("run with interactive mode on unix");
                    runWithMultiModeOnUnix();
                }
                case CommandRunner.WINDOWS -> {
                    LogUtil.log("run with interactive mode on win");
                    runWithMultiModeOnWin();
                }
            }
            return;
        }

        // 执行模式
        LogUtil.log("run with executive mode");

        // 补全 j 开头
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "j";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        args = newArgs;

        // 运行
        runWithSingleMode(args);
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
        } else {
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

            ArgumentCompleter exitArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(exitCommands),
                    NullCompleter.INSTANCE);

            ArgumentCompleter addArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(addCommands),
                    new StringsCompleter("<alias>"),
                    new Completers.FileNameCompleter(),
                    NullCompleter.INSTANCE);
            addArgumentCompleter.setStrict(false);

            ArgumentCompleter listArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(listCommands),
                    new StringsCompleter(allListCommandParts),
                    NullCompleter.INSTANCE);

            ArgumentCompleter versionArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(versionCommands),
                    NullCompleter.INSTANCE);

            ArgumentCompleter helpArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(helpCommands),
                    NullCompleter.INSTANCE);

            ArgumentCompleter modifyArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(modifyCommands),
                    new RefreshableAliasCompleter(),
                    new Completers.FileNameCompleter(),
                    NullCompleter.INSTANCE);

            ArgumentCompleter removeArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(removeCommands),
                    new RefreshableAliasCompleter(),
                    NullCompleter.INSTANCE);

            ArgumentCompleter noteArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(noteCommands),
                    new RefreshableAliasCompleter(),
                    new StringsCompleter(allNoteCategory),
                    NullCompleter.INSTANCE);

            ArgumentCompleter denoteArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(denoteCommands),
                    new RefreshableAliasCompleter(),
                    new StringsCompleter(allNoteCategory),
                    NullCompleter.INSTANCE);

            ArgumentCompleter renameArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(renameCommands),
                    new RefreshableAliasCompleter(),
                    new StringsCompleter("<new_alias>"),
                    NullCompleter.INSTANCE);

            ArgumentCompleter reportArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(reportCommands),
                    new StringsCompleter("\"<content>\""),
                    NullCompleter.INSTANCE);

            ArgumentCompleter checkArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(checkCommands),
                    new StringsCompleter(List.of("", "<line_count_from_end>")),
                    NullCompleter.INSTANCE);

            ArgumentCompleter logArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(logCommands),
                    new StringsCompleter("mode"),
                    new StringsCompleter("concise", "verbose"),
                    NullCompleter.INSTANCE);

            ArgumentCompleter concatArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(concatCommands),
                    new StringsCompleter("<script_name>"),
                    new StringsCompleter("\"<script_content>\""),
                    NullCompleter.INSTANCE);

            ArgumentCompleter clearArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(clearCommands),
                    NullCompleter.INSTANCE);

            ArgumentCompleter containArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(containCommands),
                    new StringsCompleter(allNoteCategory),
                    new RefreshableAliasCompleter(),
                    NullCompleter.INSTANCE);

            ArgumentCompleter performanceArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(performanceCommands),
                    NullCompleter.INSTANCE);

            ArgumentCompleter timeArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(timeCommands),
                    new StringsCompleter("countdown"),
                    new StringsCompleter("10s", "15m", "1h"),
                    NullCompleter.INSTANCE);

            ArgumentCompleter searchArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(searchCommands),
                    new StringsCompleter("all", "100", "50", "10"),
                    new StringsCompleter("<keyword>"),
                    NullCompleter.INSTANCE);

            ArgumentCompleter changeArgumentCompleter = new ArgumentCompleter(
                    new StringsCompleter(changeCommands),
                    new ConfigCompleter(),
                    new ConfigItemCompleter(),
                    new StringsCompleter("<new_value>"),
                    NullCompleter.INSTANCE);
            changeArgumentCompleter.setStrict(false);

            // ========== 聚合补全器 ==========
            Completer completer = new AggregateCompleter(
                    exitArgumentCompleter,
                    addArgumentCompleter,
                    listArgumentCompleter,
                    versionArgumentCompleter,
                    helpArgumentCompleter,
                    modifyArgumentCompleter,
                    removeArgumentCompleter,
                    noteArgumentCompleter,
                    denoteArgumentCompleter,
                    renameArgumentCompleter,
                    reportArgumentCompleter,
                    checkArgumentCompleter,
                    logArgumentCompleter,
                    concatArgumentCompleter,
                    clearArgumentCompleter,
                    containArgumentCompleter,
                    performanceArgumentCompleter,
                    timeArgumentCompleter,
                    searchArgumentCompleter,
                    changeArgumentCompleter);

            // 行阅读器
            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .history(new DefaultHistory())
                    .completer(completer)
                    .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                    .option(LineReader.Option.MENU_COMPLETE, true)
                    .build();

            // 自动建议
            AutosuggestionWidgets autosuggestionWidgets = new AutosuggestionWidgets(reader);
            autosuggestionWidgets.enable();

            // 自动配对
            AutopairWidgets autopairWidgets = new AutopairWidgets(reader, true);
            autopairWidgets.enable();

            LogUtil.info("Welcome to use work copilot \uD83D\uDE80 ~");
            String prompt = YELLOW + "copilot > " + RESET;
            while (true) {
                try {
                    // 显示提示符并读取输入
                    String input = reader.readLine(prompt);

                    LogUtil.log("input: %s", input);

                    // !开头表示执行bash命令
                    if (input.startsWith("!")) {
                        String bashCommand = input.substring(1);
                        bashCommand = " " + bashCommand;
                        // 这里 work dir 和当前一样, 命令是 stateless 的
                        Process process = new ProcessBuilder("/bin/bash", "-c", bashCommand)
                                .inheritIO()
                                .directory(new File(System.getProperty("user.dir")))
                                .start();
                        process.waitFor();
                        System.out.println();
                        continue;
                    }

                    // copilot 命令
                    String[] args = ("j " + input).split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    if (isIllegal(args)) {
                        continue;
                    }
                    String command = args[1];

                    long startTime = System.currentTimeMillis();

                    CommandHandler.execute(command, args);

                    long endTime = System.currentTimeMillis();
                    LogUtil.log("duration: %s ms", endTime - startTime);

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
            } else {
                if (isIllegal(args)) {
                    continue;
                }
                String command = args[1];
                CommandHandler.execute(command, args);
            }
            System.out.println();
        }
    }

    /**
     * 检查参数是否合法
     *
     * @param args
     * @return
     */
    private static boolean isIllegal(String... args) {
        int len = args.length;
        LogUtil.log("command line argument length: %s", len);
        LogUtil.log("command: %s", String.join(" ", args));
        if (len != 2 && len != 3 && len != 4 && len != 5 && len < 6) {
            LogUtil.error("Invalid number of arguments, arguments len: %s\n", len);
            return true;
        }
        return false;
    }
}
