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
                    LogUtil.info("脚本文件{%s}已删除：%s ✅", alias, path);  // 成功删除文件
                    YamlConfig.removeNestedProperty(SCRIPT, alias);
                }
                else {
                    LogUtil.info("脚本删除失败，脚本：%s，路径：%s ❌", alias, path);  // 文件删除失败
                }
            }
            LogUtil.info("成功从 PATH 中移除别名 %s ✅", alias);  // 成功移除路径
        }
        else {
            Map<String, String> innerUrlMap = YamlConfig.getPropertiesMap(INNER_URL);
            Map<String, String> outerUrlMap = YamlConfig.getPropertiesMap(OUTER_URL);
            if (innerUrlMap.containsKey(alias)) {
                YamlConfig.removeNestedProperty(INNER_URL, alias);
                LogUtil.info("成功从 INNER_URL 中移除别名 %s ✅", alias);  // 成功移除 INNER_URL
            }
            else if (outerUrlMap.containsKey(alias)) {
                YamlConfig.removeNestedProperty(OUTER_URL, alias);
                LogUtil.info("成功从 OUTER_URL 中移除别名 %s ✅", alias);  // 成功移除 OUTER_URL
            }
            else {
                LogUtil.error("别名 %s 不存在 ❌", alias);  // 别名不存在
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
