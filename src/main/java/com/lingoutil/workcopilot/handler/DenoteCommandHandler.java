package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class DenoteCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return denoteCommands;
    }

    @Override
    protected void process(String[] argv) {
        String alias = argv[2];

        String path = YamlConfig.getProperty(PATH, alias);
        if (path == null) {
            LogUtil.error("No such alias %s", alias);
            return;
        }

        String category = argv[3];
        switch (category) {
            case CATEGORY_BROWSER -> {
                YamlConfig.removeNestedProperty(category, alias);
                LogUtil.info("Remove alias %s from BROWSER successfully", alias);
            }
            case CATEGORY_EDITOR -> {
                YamlConfig.removeNestedProperty(category, alias);
                LogUtil.info("Remove alias %s from EDITOR successfully", alias);
            }
            case CATEGORY_VPN -> {
                YamlConfig.removeNestedProperty(category, alias);
                LogUtil.info("Remove alias %s from VPN successfully", alias);
            }
            case CATEGORY_OUTER_URL -> {
                YamlConfig.removeNestedProperty(category, alias);
                LogUtil.info("Remove alias %s from OUTER_URL successfully", alias);
            }
            case SCRIPT -> {
                YamlConfig.removeNestedProperty(category, alias);
                LogUtil.info("Remove alias %s from SCRIPT successfully", alias);
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
