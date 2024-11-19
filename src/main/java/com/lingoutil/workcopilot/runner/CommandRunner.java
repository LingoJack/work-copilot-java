package com.lingoutil.workcopilot.runner;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.io.IOException;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class CommandRunner {

    private static final StringBuilder sb = new StringBuilder();

    public static void run(String[] argv) {
        String script = argv[0];
        String alias = argv[1];
        int length = argv.length;

        if (YamlConfig.getProperty(PATH, alias) == null) {
            LogUtil.error("Path corresponding to alias {%s} can not be found", alias);
        }

        if (YamlConfig.getProperty(BROWSER, alias) != null) {
            if (length != 3) {
                open(alias);
            }
            else {
                String urlAlias = argv[2];
                String url = null;
                if (YamlConfig.getProperty(INNER_URL, urlAlias) != null) {
                    url = YamlConfig.getProperty(INNER_URL, urlAlias);
                }
                else if (YamlConfig.getProperty(OUTER_URL, urlAlias) != null) {
                    open(YamlConfig.getPropertiesMap(VPN).get(0));
                    url = YamlConfig.getProperty(OUTER_URL, urlAlias);
                }
                else {
                    url = urlAlias;
                }
                open(alias, url);
            }
        }
        else if (YamlConfig.getProperty(EDITOR, alias) != null) {
            if (length != 3) {
                open(alias);
            }
            else {
                String filePath = argv[2];
                open(alias, filePath);
            }
        }
        else if (YamlConfig.getProperty(VPN, alias) != null) {
            open(alias);
        }
        else {
            // path是应用路径，如何打开
            open(alias);
        }
    }

    public static boolean open(String alias, String filePath) {
        try {
            String path = YamlConfig.getProperty(PATH, alias);
            if (path == null || path.trim().isEmpty()) {
                LogUtil.error("Path not found for alias: %s", alias);
                return false;
            }

            // 构建命令
            String command = String.format("cmd /c start \"\" \"%s\" \"%s\"", path, filePath);
            Runtime.getRuntime().exec(command);

            LogUtil.info("Start {%s} with path %s: {%s}", alias, filePath, path);
            return true;
        }
        catch (IOException e) {
            LogUtil.error("Failed to start %s with file %s: %s", alias, filePath, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean open(String alias) {
        try {
            String path = YamlConfig.getProperty(PATH, alias);
            if (path == null || path.trim().isEmpty()) {
                LogUtil.error("Path not found for alias: %s", alias);
                return false;
            }

            // 使用 cmd /c start 打开路径
            String command = String.format("cmd /c start \"\" \"%s\"", path);
            Runtime.getRuntime().exec(command);

            LogUtil.info("Start %s : {%s}", alias, path);
            return true;
        }
        catch (IOException e) {
            LogUtil.error("Failed to start %s: %s", alias, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
