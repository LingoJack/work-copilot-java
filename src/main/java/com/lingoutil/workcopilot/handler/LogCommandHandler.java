package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class LogCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return logCommands;
    }

    @Override
    protected void process(String[] argv) {
        String key = argv[2];
        String value = argv[3];
        if (key.equals(MODE)) {
            value = value.equals(MODE_VERBOSE) ? MODE_VERBOSE : MODE_CONCISE;
            YamlConfig.addNestedProperty(LOG, MODE, value);
        }
        LOG_MODE = YamlConfig.getProperty(LOG, MODE);
        LogUtil.info("Successfully change mode to %s", key, value);
        LogUtil.info("current log mode is: %s", LOG_MODE);
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 4, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s mode (verbose | concise)", argv[0], argv[1]);
    }
}
