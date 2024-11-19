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
        print(PATH);
        print(INNER_URL);
        print(OUTER_URL);
        print(EDITOR);
        print(BROWSER);
        print(VPN);
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 2, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s", argv[0], argv[1]);
    }

    private void print(String parentKey) {
        Map<String, String> properties = YamlConfig.getPropertiesMap(parentKey);
        LogUtil.info("[%s]", LogUtil.capitalizeFirstLetter(parentKey));
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String shortKey = entry.getKey();
            String value = entry.getValue();
            LogUtil.info("%s: %s", shortKey, value);
        }
        LogUtil.printLine();
    }
}
