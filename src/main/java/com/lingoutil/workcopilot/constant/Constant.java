package com.lingoutil.workcopilot.constant;

import com.lingoutil.workcopilot.config.YamlConfig;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constant {

    /**
     * 退出
     */
    public static final List<String> exitCommands = List.of("exit", "q", "quit");

    /**
     * 添加别名
     */
    public static final List<String> addCommands = List.of("set", "s");

    /**
     * 列出
     */
    public static final List<String> listCommands = List.of("ls", "list");

    /**
     * 版本
     */
    public static final List<String> versionCommands = List.of("version", "v");

    /**
     * 修改别名对应的路径
     */
    public static final List<String> modifyCommands = List.of("mf", "modify");

    /**
     * 删除别名
     */
    public static final List<String> removeCommands = List.of("rm", "remove");

    /**
     * 标记别名为xxx
     */
    public static final List<String> noteCommands = List.of("nt", "note");

    /**
     * 解除别名标记
     */
    public static final List<String> denoteCommands = List.of("dnt", "denote");

    /**
     * 重命名别名
     */
    public static final List<String> renameCommands = List.of("rename", "rn");

    /**
     * 帮助
     */
    public static final List<String> helpCommands = List.of("help", "h");

    /**
     * 上报日报
     */
    public static final List<String> reportCommands = List.of("report", "r");

    /**
     * 查看日志
     */
    public static final List<String> checkCommands = List.of("check", "c");

    /**
     * 日志设置
     */
    public static final List<String> logCommands = List.of("log");

    /**
     * 自定义脚本
     */
    public static final List<String> concatCommands = List.of("concat");

    /**
     * 清屏
     */
    public static final List<String> clearCommands = List.of("clear", "cls");

    /**
     * 在xxx下查找别名
     */
    public static final List<String> containCommands = List.of("contain", "find");

    /**
     * 查看系统信息
     */
    public static final List<String> performanceCommands = List.of("system", "ps");

    /**
     * 计时
     */
    public static final List<String> timeCommands = List.of("time");

    /**
     * 在日报中查看某些文字
     */
    public static final List<String> searchCommands = List.of("search", "select", "look", "sch");

    /**
     * 直接修改某个系统设置
     */
    public static final List<String> changeCommands = List.of("change", "chg");

    /**
     * AI 相关
     */
    public static final List<String> agentCommands = List.of("agent");

    /**
     * 所有命令
     */
    public static final List<String> allCommands = Stream.of(
            exitCommands,
            addCommands,
            listCommands,
            versionCommands,
            modifyCommands,
            removeCommands,
            noteCommands,
            denoteCommands,
            renameCommands,
            helpCommands,
            reportCommands,
            checkCommands,
            logCommands,
            concatCommands,
            clearCommands,
            containCommands,
            performanceCommands,
            timeCommands,
            searchCommands,
            changeCommands,
            agentCommands
    ).flatMap(List::stream).collect(Collectors.toList());

    public static final String PATH = "path";
    public static final String VERSION = "version";
    public static final String LOG = "log";
    public static final String INNER_URL = "inner_url";
    public static final String OUTER_URL = "outer_url";
    public static final String EDITOR = "editor";
    public static final String BROWSER = "browser";
    public static final String VPN = "vpn";
    public static final String WEEK_REPORT = "week_report";
    public static final String WEEK_NUM = "week_num";
    public static final String LAST_DAY_OF_WEEK = "last_day";
    public static final String REPORT = "report";

    public static final String SCRIPT = "script";
    public static final String MODE = "mode";
    public static final String MODE_VERBOSE = "verbose";
    public static final String MODE_CONCISE = "concise";
    public static final String DEPOT = "depot";
    public static final String SETTING = "setting";
    public static final String SEARCH_ENGINE = "search-engine";

    public static final String CATEGORY_BROWSER = "browser";
    public static final String CATEGORY_EDITOR = "editor";
    public static final String CATEGORY_VPN = "vpn";
    public static final String CATEGORY_OUTER_URL = "outer_url";

    public static String LOG_MODE = YamlConfig.initializeProperty(LOG, MODE);

    public final static String STRATEGY = "strategy";
    public final static String NORMAL = "normal";

    public final static String GOOGLE_SEARCH = "https://www.google.com/search?q=%s";
    public final static String BING_SEARCH = "https://www.bing.com/search?q=%s";
    public final static String BAIDU_SEARCH = "https://www.baidu.com/s?wd=%s";

    public final static String GOOGLE = "google";
    public final static String BING = "bing";
    public final static String BAIDU = "baidu";

    public final static String TIME_COUNTDOWN = "countdown";

    public final static String ALL = "all";

    public final static String DEFAULT = "";

    /**
     * list 命令的支持的所有 part
     */
    public static final List<String> allListCommandParts = List.of("",
            ALL,
            DEFAULT,
            EDITOR,
            BROWSER,
            VPN,
            SCRIPT,
            INNER_URL,
            OUTER_URL,
            VERSION,
            PATH,
            SETTING,
            REPORT,
            LOG
    );

    /**
     * 支持标记的 category
     */
    public static final List<String> allNoteCategory = List.of(
            CATEGORY_BROWSER,
            CATEGORY_EDITOR,
            CATEGORY_VPN,
            CATEGORY_OUTER_URL,
            SCRIPT
    );
}
