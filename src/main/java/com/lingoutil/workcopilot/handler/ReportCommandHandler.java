package com.lingoutil.workcopilot.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class ReportCommandHandler extends CommandHandler {
    private static final String NEW_WEEK_CONFIG_UPDATE = "new";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter SIMPLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    protected List<String> loadCommandList() {
        return reportCommands;
    }

    private LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    private void updateConfigFiles(int weekNum, LocalDate nextLastDayOfWeek, Path configPath) {
        String nextLastDayOfWeekStr = nextLastDayOfWeek.format(DATE_FORMATTER);

        // 更新YAML配置
        try {
            YamlConfig.addNestedProperty(REPORT, WEEK_NUM, String.valueOf(weekNum));
            YamlConfig.addNestedProperty(REPORT, LAST_DAY_OF_WEEK, nextLastDayOfWeekStr);
            LogUtil.info("✅ 更新YAML配置文件成功：周数 = %d, 周结束日期 = %s", weekNum, nextLastDayOfWeekStr);
        } catch (Exception e) {
            LogUtil.error("❌ 更新YAML配置文件时出错: %s", e.getMessage());
        }

        // 更新JSON配置
        if (configPath != null && Files.exists(configPath)) {
            try {
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("week_num", new JsonPrimitive(weekNum));
                jsonObject.add("last_day", new JsonPrimitive(nextLastDayOfWeekStr));

                Files.writeString(configPath, jsonObject.toString(),
                        StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
                LogUtil.info("✅ 更新JSON配置文件成功：周数 = %d, 周结束日期 = %s", weekNum, nextLastDayOfWeekStr);
            } catch (Exception e) {
                LogUtil.error("❌ 更新JSON配置文件时出错: %s", e.getMessage());
            }
        }
    }

    @Override
    protected void process(String[] argv) {
        if (argv.length < 3) {
            LogUtil.error("❌ 缺少必要参数，请提供脚本名、命令和内容。");
            return;
        }

        String content = argv[2].trim();
        content = content.replaceAll("^\"|\"$", "");

        if (content.isEmpty()) {
            LogUtil.error("⚠️ 内容为空，无法写入。");
            return;
        }

        // 处理更新周数操作
        if (content.equals(NEW_WEEK_CONFIG_UPDATE)) {
            handleWeekUpdate(argv);
            return;
        }

        // 处理常规日报写入
        handleDailyReport(content);
    }

    private void handleWeekUpdate(String[] argv) {
        int currentWeekNum = Integer.parseInt(YamlConfig.getProperty(REPORT, WEEK_NUM));
        String lastDayOfWeekStr = YamlConfig.getProperty(REPORT, LAST_DAY_OF_WEEK);

        String inputDateStr = argv.length == 4 ? argv[3] : lastDayOfWeekStr;

        try {
            LocalDate lastDayOfWeek = parseDate(inputDateStr);
            LocalDate nextLastDayOfWeek = lastDayOfWeek.plusDays(7);

            // 获取JSON配置文件路径
            String reportPath = YamlConfig.getProperty(REPORT, WEEK_REPORT);
            Path reportFilePath = Path.of(reportPath);
            Path configPath = reportFilePath.getParent().resolve("settings.json");

            updateConfigFiles(currentWeekNum + 1, nextLastDayOfWeek, configPath);
        } catch (Exception e) {
            LogUtil.error("更新周数失败，请检查日期字符串是否有误: %s", e.getMessage());
        }
    }

    private void handleDailyReport(String content) {
        String reportPath = YamlConfig.getProperty(REPORT, WEEK_REPORT);
        LogUtil.info("📂 从配置文件中读取到路径：%s", reportPath);

        Path reportFilePath = Path.of(reportPath);
        if (!Files.exists(reportFilePath)) {
            LogUtil.error("❌ 路径不存在：%s", reportPath);
            return;
        }

        Path workDir = reportFilePath.getParent();
        Path configPath = workDir.resolve("settings.json");
        LogUtil.log("日报所在路径：%s, 配置文件：%s", workDir, configPath);

        loadConfigFromJson(configPath);

        LocalDate now = LocalDate.now();
        try {
            int weekNum = Integer.parseInt(YamlConfig.getProperty(REPORT, WEEK_NUM));
            LocalDate lastDayOfWeek = parseDate(YamlConfig.getProperty(REPORT, LAST_DAY_OF_WEEK));

            if (now.isAfter(lastDayOfWeek)) {
                LocalDate nextLastDayOfWeek = now.plusDays(6);
                String newWeekTitle = String.format("# Week%d[%s-%s]%n",
                        weekNum,
                        now.format(DATE_FORMATTER),
                        nextLastDayOfWeek.format(DATE_FORMATTER)
                );
                updateConfigFiles(weekNum + 1, nextLastDayOfWeek, configPath);
                appendToFile(reportFilePath, newWeekTitle);
            }

            String todayStr = now.format(SIMPLE_DATE_FORMATTER);
            String logEntry = String.format("- 【%s】 %s%n", todayStr, content);
            appendToFile(reportFilePath, logEntry);
            LogUtil.info("✅ 成功将内容写入：%s", reportPath);
        } catch (Exception e) {
            LogUtil.error("❌ 操作时发生错误: %s", e.getMessage(), e);
        }
    }

    private void loadConfigFromJson(Path configPath) {
        if (!Files.exists(configPath)) {
            LogUtil.error("❌ 日报配置文件不存在：%s", configPath);
            return;
        }

        try {
            String jsonContent = Files.readString(configPath);
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
            String lastDayOfWeekStr = jsonObject.get("last_day").getAsString();
            int weekNum = jsonObject.get("week_num").getAsInt();
            LogUtil.info("✅ 从日报配置文件中读取到：last_day = %s, week_num = %d", lastDayOfWeekStr, weekNum);

            LocalDate lastDayOfWeek = parseDate(lastDayOfWeekStr);
            updateConfigFiles(weekNum, lastDayOfWeek, configPath);
        } catch (Exception e) {
            LogUtil.error("❌ 解析日报配置文件时出错: %s", e.getMessage());
        }
    }

    private void appendToFile(Path filePath, String content) throws IOException {
        Files.writeString(filePath, content, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, this::hint, 3, 4);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <content>", argv[0], argv[1]);
        LogUtil.usage("%s new [<last_day_of_week> in pattern yyyy.MM.dd]", argv[0], argv[1]);
    }
}