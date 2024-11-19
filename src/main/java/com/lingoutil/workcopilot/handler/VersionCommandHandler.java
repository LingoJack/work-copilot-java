package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.Iterator;
import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.VERSION;
import static com.lingoutil.workcopilot.constant.Constant.versionCommands;

public class VersionCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return versionCommands;
    }

    @Override
    protected void process(String[] argv) {
        Iterator<String> propertiesIterator = YamlConfig.getPropertiesIterator(VERSION);
        while (propertiesIterator.hasNext()) {
            String key = propertiesIterator.next();
            String value = YamlConfig.getProperty(key);
            // 解析键的层级结构
            String shortKey = key.startsWith(VERSION + ".") ?
                    key.substring(VERSION.length() + 1) : key;
            LogUtil.info("%s: %s", shortKey, value);
        }
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 2, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("Usage: %s ", argv[0], argv[1]);
    }
}
