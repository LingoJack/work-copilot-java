package com.lingoutil.workcopilot.handler;

import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.clearCommands;

public class ClearCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return clearCommands;
    }

    @Override
    protected void process(String[] argv) {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }
        catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 2, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        super.hint(argv);
    }
}
