package com.lingoutil.workcopilot.handler;

import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.concatCommands;

public class ConcatCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return concatCommands;
    }

    @Override
    protected void process(String[] argv) {

    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return false;
    }

    @Override
    protected void hint(String[] argv) {
        super.hint(argv);
    }
}
