package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.changeCommands;

public class ChangeCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return changeCommands;
    }

    @Override
    protected void process(String[] argv) {
        String part = argv[2];
        String field = argv[3];
        if (!YamlConfig.containProperty(part, field)) {
            LogUtil.error("❌ 在配置文件中未找到该字段：%s.%s", part, field);
            return;
        }
        String oldValue = YamlConfig.getProperty(part, field);
        String newValue = argv[4];
        YamlConfig.addNestedProperty(part, field, newValue);
        LogUtil.info("✅ 已修改 %s.%s 的值为 %s，旧值为 %s", part, field, newValue, oldValue);
        LogUtil.info("\uD83D\uDEA7 此命令可能会导致配置文件属性错乱而使 Copilot 无法正常使用，请确保在您清楚在做什么的情况下使用");
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 5, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <part> <field> <value>", argv[0], argv[1]);
    }
}
