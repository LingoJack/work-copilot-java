package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class ContainCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return containCommands;
    }

    @Override
    protected void process(String[] argv) {
        if (argv.length == 4) {
            String containerListStr = argv[1];
            String targetAlias = argv[3];

            // 从[%s|%s|%s|%s|%s]中解析List<String>
            List<String> containerList = parseContainerListStr(containerListStr);
            List<String> lines = findAlias(containerList, targetAlias);
            printResults(lines);
        }
        else {
            String targetAlias = argv[2];
            List<String> lines = findAlias(List.of(PATH, SCRIPT, BROWSER, EDITOR, VPN), targetAlias);
            printResults(lines);
        }
    }

    private static void printResults(List<String> lines) {
        LogUtil.info("find %d results", lines.size());
        for (String line : lines) {
            LogUtil.info(line);
        }
    }

    private static List<String> findAlias(List<String> containerList, String targetAlias) {
        List<String> lines = new ArrayList<>();
        for (String container : containerList) {
            if (YamlConfig.containProperty(container, targetAlias)) {
                lines.add(String.format("[%s] %s: %s", container, targetAlias, YamlConfig.getProperty(container, targetAlias)));
            }
        }
        return lines;
    }

    private List<String> parseContainerListStr(String containerListStr) {
        return List.of(containerListStr.split(","));
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        int length = argv.length;
        if (length != 3 && length != 4) {
            hint(argv);
            return false;
        }
        return true;
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s [%s[,%s[,%s[,%s[,%s] %s <alias>", argv[0], PATH, BROWSER, VPN, EDITOR, SCRIPT, argv[1]);
    }
}
