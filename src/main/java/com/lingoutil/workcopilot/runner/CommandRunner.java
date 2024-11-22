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

        if (!YamlConfig.containProperty(PATH, alias)) {
            LogUtil.error("Path corresponding to alias {%s} can not be found", alias);
            return;
        }

        if (YamlConfig.containProperty(BROWSER, alias)) {
            if (length == 2) {
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
                    if (length == 3) {
                        // 说明是搜索的逻辑
                        String path = YamlConfig.getProperty(BROWSER, alias);
                        if (path.contains("chrome")) {
                            url = String.format(GOOGLE_SEARCH, urlAlias);
                        }
                        else {
                            url = String.format(BING_SEARCH, urlAlias);
                        }
                    }
                    else if (length == 4) {
                        String engine = argv[3];
                        if (engine.equalsIgnoreCase(GOOGLE)) {
                            url = String.format(GOOGLE_SEARCH, urlAlias);
                        }
                        else if (engine.equalsIgnoreCase(BING)) {
                            url = String.format(BING_SEARCH, urlAlias);
                        }
                        else if (engine.equalsIgnoreCase(BAIDU)) {
                            url = String.format(BAIDU_SEARCH, urlAlias);
                        }
                        else {
                            LogUtil.error("Unknown search engine: %s", engine);
                            LogUtil.usage("Usage: %s %s <search_keyword> <search_engine>", script, alias);
                            return;
                        }
                    }
                }
                open(alias, url);
            }
        }
        else if (YamlConfig.containProperty(EDITOR, alias)) {
            if (length != 3) {
                open(alias);
            }
            else {
                String filePath = argv[2];
                open(alias, filePath);
            }
        }
        else if (YamlConfig.containProperty(VPN, alias)) {
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
