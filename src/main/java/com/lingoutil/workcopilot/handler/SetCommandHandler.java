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

        // 处理路径中包含空格的情况，将argv[3]及之后的所有参数拼接起来
        String path;
        if (argv.length > 4) {
            // 路径包含空格，需要拼接（不转义，直接用空格连接）
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 3; i < argv.length; i++) {
                pathBuilder.append(argv[i]);
                if (i < argv.length - 1) {
                    pathBuilder.append(" "); // 改为普通空格，不转义
                }
            }
            path = pathBuilder.toString();
        } else {
            path = argv[3];
        }

        // 去除路径两端的引号（单引号或双引号）
        path = removeQuotes(path);

        // 去除路径中的转义反斜杠（将 "\ " 替换为 " "）
        path = path.replace("\\ ", " ");

        if (isURL(path)) {
            addAsUrl(alias, path);
        } else {
            addAsPath(alias, path);
        }
    }

    /**
     * 去除字符串两端的引号（单引号或双引号）
     */
    private String removeQuotes(String str) {
        if (str == null || str.length() < 2) {
            return str;
        }

        // 检查是否被单引号包围
        if (str.startsWith("'") && str.endsWith("'")) {
            return str.substring(1, str.length() - 1);
        }

        // 检查是否被双引号包围
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }

        return str;
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
                    modifyCommands.getFirst());
        }
    }

    private static void addAsUrl(String alias, String path) {
        Map<String, String> innerUrlMap = YamlConfig.getPropertiesMap(INNER_URL);
        Map<String, String> outerUrlMap = YamlConfig.getPropertiesMap(OUTER_URL);
        if (!innerUrlMap.containsKey(alias) && !outerUrlMap.containsKey(alias)) {
            YamlConfig.addNestedProperty(INNER_URL, alias, path);
            LogUtil.info("✅ Added %s with URL {%s} successfully! 🚀", alias, path);
        } else {
            LogUtil.error("Alias %s already exists. 😢 Please use command `%s` to modify", alias,
                    modifyCommands.get(0));
        }
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        if (argv.length < 4) {
            hint(argv);
            return true;
        }
        return true;
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <alias> <path>", argv[0], argv[1]);
    }
}
