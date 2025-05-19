package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.runner.CommandRunner;
import com.lingoutil.workcopilot.util.LogUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class ConcatCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return concatCommands;
    }

    @Override
    protected void process(String[] argv) {
        String scriptName = argv[2];
        String scriptContent = argv[3];
        String deportFolderPath = YamlConfig.getProperty(SCRIPT, DEPOT);

        // 检查脚本名是否已存在
        if (YamlConfig.containProperty(PATH, scriptName)) {
            LogUtil.error("❌ 失败！脚本名 {%s} 已经存在", scriptName);
            return;
        }

        // 生成脚本路径
        String scriptPath = deportFolderPath + scriptName;
        scriptPath += CommandRunner.getOsType().equals(CommandRunner.WINDOWS) ? ".cmd" : ".sh";
        YamlConfig.addNestedProperty(PATH, scriptName, scriptPath);

        try {
            // 写入文件
            File scriptFile = new File(scriptPath);
            if (!scriptFile.exists()) {
                scriptFile.getParentFile().mkdirs(); // 确保父目录存在
                scriptFile.createNewFile(); // 创建文件
                LogUtil.info("🎉 文件创建成功: %s", scriptPath);
            }

            try (FileWriter writer = new FileWriter(scriptFile)) {
                writer.write(scriptContent.substring(1, scriptContent.length() - 1));
                LogUtil.info("✅ 成功创建脚本 {%s} 并写入内容: %s", scriptName, scriptContent);
            }

            // 设置执行权限 (非 Windows 系统)
            if (!CommandRunner.getOsType().equals(CommandRunner.WINDOWS)) {
                if (!scriptFile.setExecutable(true)) {
                    throw new IOException("无法设置脚本的执行权限");
                }
                LogUtil.info("🔧 已为脚本 {%s} 设置执行权限", scriptName);
            }

            YamlConfig.addNestedProperty(SCRIPT, scriptName, scriptPath);

        } catch (IOException e) {
            // 失败时清理
            YamlConfig.removeNestedProperty(PATH, scriptName);
            LogUtil.error("💥 处理脚本文件失败: %s", e.getMessage());
        }
    }


    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 4, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <script_name> \"<script_content>\"", argv[0], argv[1]);
    }
}
