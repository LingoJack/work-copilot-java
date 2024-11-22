package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class ListCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return listCommands;
    }

    @Override
    protected void process(String[] argv) {
        LogUtil.printLine();
        if (argv.length == 2) {
            print(PATH);
            print(INNER_URL);
            print(OUTER_URL);
            print(EDITOR);
            print(BROWSER);
            print(VPN);
            print(SCRIPT);
        }
        else {
            String parentKey = argv[2];
            print(parentKey);
        }
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        if (argv.length != 2 && argv.length != 3) {
            hint(argv);
            return false;
        }
        return true;
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s [part]", argv[0], argv[1]);
    }

    private void print(String parentKey) {
        Map<String, String> properties = YamlConfig.getPropertiesMap(parentKey);

        if (properties == null) {
            LogUtil.error("The part does not exist");
            return;
        }

        LogUtil.printf("[%s]\n", LogUtil.YELLOW, LogUtil.capitalizeFirstLetter(parentKey));

        if (properties.isEmpty()) {
            LogUtil.info("Empty");
        }
        else {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                String shortKey = entry.getKey();
                String value = entry.getValue();
                LogUtil.printf("%s", LogUtil.GREEN, shortKey);
                LogUtil.info(": %s", value);
            }
        }
        LogUtil.printLine();
    }
}
