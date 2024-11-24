package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.util.LogUtil;

import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.exitCommands;

public class ExitCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return exitCommands;
    }

    @Override
    protected void process(String[] argv) {
        LogUtil.info("Bye~ See you again \uD83D\uDE2D");
        System.exit(0);
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 2, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s", argv[0], argv[1]);
    }
}
