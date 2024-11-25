package com.lingoutil.workcopilot.runner;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;
import com.lingoutil.workcopilot.util.URLUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class CommandRunner {

    private static final StringBuilder sb = new StringBuilder();

    public static void run(String[] argv) {
        String script = argv[0];
        String alias = argv[1];
        int length = argv.length;

        // 检查别名是否存在
        if (!YamlConfig.containProperty(PATH, alias)
                && !YamlConfig.containProperty(INNER_URL, alias)
                && !YamlConfig.containProperty(OUTER_URL, alias)) {
            LogUtil.error("❌ 无法找到别名对应的路径或网址 {%s}。请检查配置文件。", alias);
            return;
        }

        if (YamlConfig.containProperty(BROWSER, alias)) {
            if (length == 2) {
                // j bs 直接打开浏览器
                open(alias);
            }
            else {
                String urlAlias = argv[2];
                String url = null;
                if (YamlConfig.getProperty(INNER_URL, urlAlias) != null) {
                    url = YamlConfig.getProperty(INNER_URL, urlAlias);
                }
                else if (YamlConfig.getProperty(OUTER_URL, urlAlias) != null) {
                    Map<String, String> vpnMap = YamlConfig.getPropertiesMap(VPN);
                    List<String> vpnAlias = vpnMap.keySet().stream().limit(1).toList();
                    open(vpnAlias.get(0));
                    url = YamlConfig.getProperty(OUTER_URL, urlAlias);
                }
                else {
                    // 如果urlAlias既不在inner_url里也不在outer_url里，即不是网址别名
                    if (length == 3) {
                        // 说明是搜索或网址
                        if (URLUtil.isURL(urlAlias)) {
                            // 如果是网址，直接赋值给url
                            url = urlAlias;
                        }
                        else {
                            // 如果是搜索，则选择搜索引擎凭借urlAlias
                            url = getSearchUrlWithEngine(urlAlias);
                        }
                    }
                    else if (length == 4) {
                        // 如果指定了搜索引擎
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
                            LogUtil.error("❌ 未知的搜索引擎: %s", engine);
                            LogUtil.usage("%s %s <search_keyword> <search_engine>", script, alias);
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

    private static String getSearchUrlWithEngine(String urlAlias) {
        String engine = YamlConfig.getProperty(SETTING, SEARCH_ENGINE);
        String searchPattern = null;
        if (engine.equalsIgnoreCase(GOOGLE)) {
            searchPattern = GOOGLE_SEARCH;
        }
        else if (engine.equalsIgnoreCase(BING)) {
            searchPattern = BING_SEARCH;
        }
        else if (engine.equalsIgnoreCase(BAIDU)) {
            searchPattern = BAIDU_SEARCH;
        }
        else {
            LogUtil.info("未指定搜索引擎，使用默认搜索引擎：%s", BING);
            searchPattern = BING_SEARCH;
        }
        return String.format(searchPattern, urlAlias);
    }

    public static boolean open(String alias, String filePath) {
        try {
            String path = YamlConfig.getProperty(PATH, alias);
            if (path == null || path.trim().isEmpty()) {
                LogUtil.error("❌ 未找到别名对应的路径: %s。请检查路径配置。", alias);
                return false;
            }

            // 构建命令
            String command = String.format("cmd /c start \"\" \"%s\" \"%s\"", path, filePath);
            Runtime.getRuntime().exec(command);

            LogUtil.info("✅ 启动 {%s}，路径 %s: {%s}", alias, filePath, path);
            return true;
        }
        catch (IOException e) {
            LogUtil.error("💥 启动 %s 失败，文件 %s: %s", alias, filePath, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean open(String alias) {
        try {
            String path = null;

            if (YamlConfig.containProperty(PATH, alias)) {
                path = YamlConfig.getProperty(PATH, alias);
            }
            else if (YamlConfig.containProperty(INNER_URL, alias)) {
                path = YamlConfig.getProperty(INNER_URL, alias);
            }
            else if (YamlConfig.containProperty(OUTER_URL, alias)) {
                path = YamlConfig.getProperty(OUTER_URL, alias);
            }
            else {
                LogUtil.error("❌ 未找到别名对应的路径或网址: %s。请检查配置文件。", alias);
            }

            // 使用 cmd /c start 打开路径
            String command = String.format("cmd /c start \"\" \"%s\"", path);
            Runtime.getRuntime().exec(command);

            LogUtil.info("✅ 启动 %s : {%s}", alias, path);
            return true;
        }
        catch (IOException e) {
            LogUtil.error("💥 启动 %s 失败: %s", alias, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
