package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

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
            listByParentKey(PATH);
            listByParentKey(INNER_URL);
            listByParentKey(OUTER_URL);
            listByParentKey(EDITOR);
            listByParentKey(BROWSER);
            listByParentKey(VPN);
            listByParentKey(SCRIPT);
        }
        else {
            String parentKey = argv[2];

            if (parentKey.equals("all")) {
                List<String> topLevelKeys = YamlConfig.getAllTopLevelKeys();
                for (String topLevelKey : topLevelKeys) {
                    listByParentKey(topLevelKey);
                }
            }
            else {
                listByParentKey(parentKey);
            }
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

    private void listByParentKey(String parentKey) {
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
