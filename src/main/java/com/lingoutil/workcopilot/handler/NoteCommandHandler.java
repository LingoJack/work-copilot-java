package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class NoteCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return noteCommands;
    }

    @Override
    protected void process(String[] argv) {
        String alias = argv[2];
        if (!YamlConfig.containProperty(PATH, alias)
                && !YamlConfig.containProperty(INNER_URL, alias)
                && !YamlConfig.containProperty(OUTER_URL, alias)) {
            LogUtil.error("No such alias %s", alias);
            return;
        }

        String path = YamlConfig.getProperty(PATH, alias);

        String category = argv[3];
        switch (category) {
            case CATEGORY_BROWSER -> {
                YamlConfig.addNestedProperty(category, alias, path);
                LogUtil.info("Add alias %s to BROWSER successfully", alias);
            }
            case CATEGORY_EDITOR -> {
                YamlConfig.addNestedProperty(category, alias, path);
                LogUtil.info("Add alias %s to EDITOR successfully", alias);
            }
            case CATEGORY_VPN -> {
                YamlConfig.addNestedProperty(category, alias, path);
                LogUtil.info("Add alias %s to VPN successfully", alias);
            }
            case CATEGORY_OUTER_URL -> {
                path = YamlConfig.getProperty(INNER_URL, alias);
                YamlConfig.addNestedProperty(category, alias, path);
                YamlConfig.removeNestedProperty(INNER_URL, alias);
                LogUtil.info("Add alias %s to OUTER_URL successfully", alias);
            }
            case SCRIPT -> {
                YamlConfig.addNestedProperty(category, alias, path);
                LogUtil.info("Add alias %s to SCRIPT successfully", alias);
            }
            default -> {
                hint(argv);
            }
        }
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 4, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <alias> <category (%s, %s, %s, %s, %s)>",
                argv[0],
                argv[1],
                CATEGORY_EDITOR, CATEGORY_BROWSER, CATEGORY_VPN, CATEGORY_OUTER_URL, SCRIPT);
    }
}
