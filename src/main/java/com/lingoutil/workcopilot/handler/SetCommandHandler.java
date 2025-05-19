package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.List;
import java.util.Map;

import static com.lingoutil.workcopilot.constant.Constant.*;
import static com.lingoutil.workcopilot.util.URLUtil.isURL;

public class SetCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return addCommands;
    }

    @Override
    protected void process(String[] argv) {
        String alias = argv[2];
        if (allCommands.contains(alias)) {
            // 该别名已经被预设，请更换
            LogUtil.error(String.format("Alias `%s` is already preset. Please choose another one. 😢", alias));
            return;
        }
        String path = argv[3];
        if (isURL(path)) {
            addAsUrl(alias, path);
        } else {
            addAsPath(alias, path);
        }
    }

    private static void addAsPath(String alias, String path) {
        Map<String, String> pathMap = YamlConfig.getPropertiesMap(PATH);
        if (!pathMap.containsKey(alias)) {
            YamlConfig.addNestedProperty(PATH, alias, path);
            LogUtil.info("✅ Added %s with path {%s} successfully! 🎉", alias, path);
        } else {
            LogUtil.error("Alias %s with path {%s} already exists. 😢 Please use command `%s` to modify",
                    alias,
                    pathMap.get(alias),
                    modifyCommands.get(0));
        }
    }

    private static void addAsUrl(String alias, String path) {
        Map<String, String> innerUrlMap = YamlConfig.getPropertiesMap(INNER_URL);
        Map<String, String> outerUrlMap = YamlConfig.getPropertiesMap(OUTER_URL);
        if (!innerUrlMap.containsKey(alias) && !outerUrlMap.containsKey(alias)) {
            YamlConfig.addNestedProperty(INNER_URL, alias, path);
            LogUtil.info("✅ Added %s with URL {%s} successfully! 🚀", alias, path);
        } else {
            LogUtil.error("Alias %s already exists. 😢 Please use command `%s` to modify", alias, modifyCommands.get(0));
        }
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 4, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <alias> <path>", argv[0], argv[1]);
    }
}
