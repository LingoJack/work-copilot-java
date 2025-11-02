package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.util.LogUtil;

import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.agentCommands;

public class AgentCommandHandler extends CommandHandler {

    @Override
    protected List<String> loadCommandList() {
        return agentCommands;
    }

    @Override
    protected void process(String[] argv) {

    }

    @Override
    protected boolean checkArgs(String[] argv) {
        LogUtil.info("UNSUPPORTED...");
        return false;
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <query>", argv[0], argv[1]);
    }
}
