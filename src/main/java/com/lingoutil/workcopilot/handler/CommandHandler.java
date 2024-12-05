package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.runner.CommandRunner;
import com.lingoutil.workcopilot.scanner.CommandHandlerScanner;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.lingoutil.workcopilot.constant.Constant.helpCommands;

public abstract class CommandHandler {

    private static final ConcurrentHashMap<String, CommandHandler> commandHandlerMap = new ConcurrentHashMap<>();

    public static void execute(String command, String[] argv) {
        if (commandHandlerMap.entrySet().isEmpty()) {
            CommandHandlerScanner.scanAndRegisterHandlers("com.lingoutil.workcopilot.handler");
        }
        CommandHandler handler = commandHandlerMap.get(command);
        if (handler != null) {
            if (handler.checkArgs(argv)) {
                handler.process(argv);
            }
        }
        else {
            // 打开alias
            CommandRunner.run(argv);
        }
    }

    public static void execute(String[] argv) {
        execute(argv[1], argv);
    }

    protected final void init() {
        List<String> commandList = loadCommandList();
        commandList.forEach(command -> commandHandlerMap.put(command, this));
    }

    public CommandHandler() {

    }

    protected abstract List<String> loadCommandList();

    protected abstract void process(String[] argv);

    protected abstract boolean checkArgs(String[] argv);

    public static void register(CommandHandler handler) {
        handler.init();
    }

    protected final boolean checkArgs(String[] argv, int expectedNum, Consumer<String[]> errorHandler) {
        int length = argv.length;
        if (length != expectedNum) {
            LogUtil.error("expected argument num is %d, but got %d", expectedNum, length);
            errorHandler.accept(argv);
            return false;
        }
        return true;
    }

    protected final boolean checkArgs(String[] argv, Consumer<String[]> errorHandler, int... expectedNums) {
        int length = argv.length;
        if (!containInArray(expectedNums,length)) {
            LogUtil.error("expected argument num is %s, but got %d", Arrays.toString(expectedNums), length);
            errorHandler.accept(argv);
            return false;
        }
        return true;
    }

    private boolean containInArray(int[] expected, int target) {
        for (int i : expected) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    protected final boolean checkArgs(String[] argv, int expectedNum) {
        return checkArgs(argv, expectedNum, this::hint);
    }

    protected void hint(String[] argv) {
        LogUtil.info("Enter command `%s %s` to check usage", argv[0], helpCommands.get(0));
    }
}
