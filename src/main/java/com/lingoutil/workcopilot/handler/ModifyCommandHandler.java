package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.List;
import java.util.Map;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class ModifyCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return modifyCommands;
    }

    @Override
    protected void process(String[] argv) {
        String alias = argv[2];

        // 处理路径中包含空格的情况
        String path;
        if (argv.length > 4) {
            // 路径包含空格，需要拼接（不转义，直接用空格连接）
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 3; i < argv.length; i++) {
                pathBuilder.append(argv[i]);
                if (i < argv.length - 1) {
                    pathBuilder.append(" "); // 普通空格，不转义
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

        Map<String, String> pathMap = YamlConfig.getPropertiesMap(PATH);
        Map<String, String> innerUrlMap = YamlConfig.getPropertiesMap(INNER_URL);
        Map<String, String> outerUrlMap = YamlConfig.getPropertiesMap(OUTER_URL);
        Map<String, String> editorMap = YamlConfig.getPropertiesMap(EDITOR);
        Map<String, String> vpnMap = YamlConfig.getPropertiesMap(VPN);
        Map<String, String> browserMap = YamlConfig.getPropertiesMap(BROWSER);

        boolean hasModified = false;
        if (pathMap.containsKey(alias)) {
            YamlConfig.addNestedProperty(PATH, alias, path);
            hasModified = true;
            LogUtil.info("modify %s with path {%s} successfully", alias, path);
        }
        if (innerUrlMap.containsKey(alias)) {
            YamlConfig.addNestedProperty(INNER_URL, alias, path);
            hasModified = true;
            LogUtil.info("modify %s with url {%s} under inner_url successfully", alias, path);
        }
        if (outerUrlMap.containsKey(alias)) {
            YamlConfig.addNestedProperty(OUTER_URL, alias, path);
            hasModified = true;
            LogUtil.info("modify %s with url {%s} under outer_url successfully", alias, path);
        }
        if (editorMap.containsKey(alias)) {
            YamlConfig.addNestedProperty(EDITOR, alias, path);
            hasModified = true;
            LogUtil.info("modify %s with path {%s} under editor successfully", alias, path);
        }
        if (browserMap.containsKey(alias)) {
            YamlConfig.addNestedProperty(BROWSER, alias, path);
            hasModified = true;
            LogUtil.info("modify %s with path {%s} under browser successfully", alias, path);
        }
        if (vpnMap.containsKey(alias)) {
            YamlConfig.addNestedProperty(VPN, alias, path);
            hasModified = true;
            LogUtil.info("modify %s with path {%s} under vpn successfully", alias, path);
        }

        if (!hasModified) {
            LogUtil.error("Alias %s does not exist. Please use command");
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

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 4, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <alias> <new_path>", argv[0], argv[1]);
    }
}
