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

    // 静态变量存储操作系统类型
    private static final String osType;

    public static final String WINDOWS = "win";

    public static final String MAC = "mac";

    // 静态代码块在类加载时判断操作系统类型
    static {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains(WINDOWS)) {
            osType = WINDOWS;
        }
        else if (os.contains("mac")) {
            osType = MAC;
        }
        else {
            osType = "unknown";
        }
    }

    public static void run(String[] argv) {
        String script = argv[0];
        String alias = argv[1];
        int length = argv.length;

        // 检查别名是否存在
        if (!isAliasExist(alias)) {
            LogUtil.error("❌ 无法找到别名对应的路径或网址 {%s}。请检查配置文件。", alias);
            return;
        }

        if (YamlConfig.containProperty(BROWSER, alias)) {
            openBrowser(argv, length, alias, script);
        }
        else if (YamlConfig.containProperty(EDITOR, alias)) {
            openEditor(argv, length, alias);
        }
        else if (YamlConfig.containProperty(VPN, alias)) {
            openVPN(alias);
        }
        else {
            // path是应用路径，如何打开
            open(alias);
        }
    }

    private static void openVPN(String alias) {
        open(alias);
    }

    private static void openEditor(String[] argv, int length, String alias) {
        if (length != 3) {
            open(alias);
        }
        else {
            String filePath = argv[2];
            open(alias, filePath);
        }
    }

    private static void openBrowser(String[] argv, int length, String alias, String script) {
        if (length == 2) {
            open(alias);
        }
        else {
            openBrowserWithUrlAliasOrSearchedContent(argv, length, script, alias);
        }
    }

    private static boolean isAliasExist(String alias) {
        return YamlConfig.containProperty(PATH, alias)
                || YamlConfig.containProperty(INNER_URL, alias)
                || YamlConfig.containProperty(OUTER_URL, alias);
    }

    private static void openBrowserWithUrlAliasOrSearchedContent(String[] argv, int length, String script, String alias) {
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
                url = URLUtil.isURL(urlAlias) ? urlAlias : getSearchUrlWithEngine(urlAlias);
            }
            else if (length == 4) {
                // 如果指定了搜索引擎
                String engine = argv[3];
                url = getSearchUrlWithEngine(urlAlias, engine);
            }
        }
        open(alias, url);
    }

    private static String getSearchUrlWithEngine(String urlAlias) {
        String engine = YamlConfig.getProperty(SETTING, SEARCH_ENGINE);
        return getSearchUrlWithEngine(urlAlias, engine);
    }

    private static String getSearchUrlWithEngine(String urlAlias, String engine) {
        String searchPattern;
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

            // 根据操作系统类型选择命令
            String command = "";
            if (WINDOWS.equals(osType)) {
                command = String.format("cmd /c start \"\" \"%s\" \"%s\"", path, filePath);
                Runtime.getRuntime().exec(command);
            }
            else if (MAC.equals(osType)) {
                String[] commands = {"open", path, filePath};
                Runtime.getRuntime().exec(commands);
            }
            else {
                LogUtil.error("💥 当前操作系统不支持此功能: %s", osType);
                return false;
            }

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
            String path = getPathByAlias(alias);

            if (path == null || path.trim().isEmpty()) {
                return false;
            }

            // 根据操作系统类型选择命令
            if (WINDOWS.equals(osType)) {
                String command = String.format("cmd /c start \"\" \"%s\"", path);
                Runtime.getRuntime().exec(command);
            }
            else if (MAC.equals(osType)) {
                String[] commands = {"open", path};
                Runtime.getRuntime().exec(commands);
            }
            else {
                LogUtil.error("💥 当前操作系统不支持此功能: %s", osType);
                return false;
            }

            LogUtil.info("✅ 启动 %s : {%s}", alias, path);
            return true;
        }
        catch (IOException e) {
            LogUtil.error("💥 启动 %s 失败: %s", alias, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static String getPathByAlias(String alias) {
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
        return path;
    }

    public static String getOsType() {
        return osType;
    }
}
