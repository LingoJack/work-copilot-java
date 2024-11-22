package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class RemoveCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return removeCommands;
    }

    @Override
    protected void process(String[] argv) {
        String alias = argv[2];
        if (YamlConfig.containProperty(PATH, alias)) {
            YamlConfig.removeNestedProperty(PATH, alias);
            YamlConfig.removeNestedProperty(EDITOR, alias);
            YamlConfig.removeNestedProperty(VPN, alias);
            YamlConfig.removeNestedProperty(BROWSER, alias);

            if (YamlConfig.containProperty(SCRIPT, alias)) {
                String path = YamlConfig.getProperty(SCRIPT, alias);
                File file = new File(path);
                boolean delete = file.delete();
                if (delete) {
                    LogUtil.info("脚本文件{%s}已删除：%s", alias, path);
                    YamlConfig.removeNestedProperty(SCRIPT, alias);
                }
                else {
                    LogUtil.info("脚本删除失败，脚本：%s，路径：%s", alias, path);
                }
            }
            LogUtil.info("Remove alias %s from PATH successfully", alias);
        }
        else {
            Map<String, String> innerUrlMap = YamlConfig.getPropertiesMap(INNER_URL);
            Map<String, String> outerUrlMap = YamlConfig.getPropertiesMap(OUTER_URL);
            if (innerUrlMap.containsKey(alias)) {
                YamlConfig.removeNestedProperty(INNER_URL, alias);
                LogUtil.info("Remove alias %s from INNER_URL successfully", alias);
            }
            else if (outerUrlMap.containsKey(alias)) {
                YamlConfig.removeNestedProperty(OUTER_URL, alias);
                LogUtil.info("Remove alias %s from OUTER_URL successfully", alias);
            }
            else {
                LogUtil.error("Alias %s does not exist", alias);
            }
        }
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 3, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <alias>", argv[0], argv[1]);
    }
}
