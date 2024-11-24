package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class RenameCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return renameCommands;
    }

    @Override
    protected void process(String[] argv) {
        String script = argv[0];
        String alias = argv[2];
        String newAlias = argv[3];

        if (YamlConfig.getPropertiesMap(PATH).containsKey(alias)) {
            String path = YamlConfig.getProperty(PATH, alias);
            YamlConfig.renameProperty(PATH, alias, newAlias);
            YamlConfig.renameProperty(BROWSER, alias, newAlias);
            YamlConfig.renameProperty(EDITOR, alias, newAlias);
            YamlConfig.renameProperty(VPN, alias, newAlias);
            YamlConfig.renameProperty(SCRIPT, alias, newAlias);
            LogUtil.info("✅ Renamed %s to %s successfully! Path: %s 🎉", alias, newAlias, path);
        }
        if (YamlConfig.getPropertiesMap(INNER_URL).containsKey(alias)) {
            String url = YamlConfig.getProperty(INNER_URL, alias);
            YamlConfig.renameProperty(INNER_URL, alias, newAlias);
            LogUtil.info("✅ Renamed %s to %s successfully! Inner URL: %s 🚀", alias, newAlias, url);
        }
        if (YamlConfig.getPropertiesMap(OUTER_URL).containsKey(alias)) {
            String url = YamlConfig.getProperty(OUTER_URL, alias);
            YamlConfig.renameProperty(OUTER_URL, alias, newAlias);
            LogUtil.info("✅ Renamed %s to %s successfully! Outer URL: %s 🌐", alias, newAlias, url);
        }
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 4, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("💡 Usage: %s %s <alias> <new_alias>", argv[0], argv[1]);
    }
}
