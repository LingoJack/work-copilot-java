package com.lingoutil.workcopilot.constant;

import com.lingoutil.workcopilot.config.YamlConfig;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constant {
    public static final List<String> exitCommands = List.of("exit", "-q", "quit", "-quit", "-exit");

    public static final List<String> addCommands = List.of("set", "-set", "s");

    public static final List<String> listCommands = List.of("ls", "list", "-list");

    public static final List<String> versionCommands = List.of("-version", "version", "v");

    public static final List<String> modifyCommands = List.of("mf", "-modify", "modify");

    public static final List<String> removeCommands = List.of("rm", "-remove", "remove");

    public static final List<String> noteCommands = List.of("nt", "-note", "note");

    public static final List<String> denoteCommands = List.of("dnt", "denote", "-denote");

    public static final List<String> renameCommands = List.of("rename", "-rename", "rn");

    public static final List<String> helpCommands = List.of("help", "-help", "-h");

    public static final List<String> reportCommands = List.of("-r", "report", "-report", "r");

    public static final List<String> checkCommands = List.of("-c", "check", "-check", "c");

    public static final List<String> logCommands = List.of("log", "-log");

    public static final List<String> concatCommands = List.of("concat", "-concat");

    public static final List<String> clearCommands = List.of("clear", "cls");

    public static final List<String> containCommands = List.of("contain", "-contain","find","-find");

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
            containCommands
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

    public static final String CATEGORY_BROWSER = "browser";
    public static final String CATEGORY_EDITOR = "editor";
    public static final String CATEGORY_VPN = "vpn";
    public static final String CATEGORY_OUTER_URL = "outer-url";

    public static String LOG_MODE = YamlConfig.initializeProperty(LOG, MODE);

    public final static String STRATEGY = "strategy";
    public final static String NORMAL = "normal";

    public final static String GOOGLE_SEARCH = "https://www.google.com/search?q=%s";
    public final static String BING_SEARCH = "https://www.bing.com/search?q=%s";
    public final static String BAIDU_SEARCH = "https://www.baidu.com/s?wd=%s";

    public final static String GOOGLE = "google";
    public final static String BING = "bing";
    public final static String BAIDU = "baidu";
}
