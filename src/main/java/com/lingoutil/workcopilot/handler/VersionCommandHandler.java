package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.runner.CommandRunner;
import com.lingoutil.workcopilot.util.LogUtil;
import org.jline.utils.Log;

import java.util.List;
import java.util.Map;

import static com.lingoutil.workcopilot.constant.Constant.VERSION;
import static com.lingoutil.workcopilot.constant.Constant.versionCommands;

public class VersionCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return versionCommands;
    }

    @Override
    protected void process(String[] argv) {
        Map<String, String> propertiesMap = YamlConfig.getPropertiesMap(VERSION);
        for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals("email") || key.equals("author")) {
                continue;
            }
            LogUtil.info("%s: %s", key, value);
        }
        String version = "11.0.0";
        LogUtil.info("kernel version: %s", version);
        LogUtil.info("os: %s", CommandRunner.getOsType());
        LogUtil.info("author: lingojack | LingoJack | 达不溜勾勾");
        LogUtil.info("email: lingojack@qq.com | 3065225677@qq.com | 3065225677w@gmail.com");
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
