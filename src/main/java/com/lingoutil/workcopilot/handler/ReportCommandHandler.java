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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter SIMPLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    protected List<String> loadCommandList() {
        return reportCommands;
    }

    private LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 更新配置文件(程序的yaml和日报的json)
     * @param weekNum
     * @param nextLastDayOfWeek
     * @param configPath
     */
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

                Files.writeString(configPath, jsonObject.toString(), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
                LogUtil.info("✅ 更新JSON配置文件成功：周数 = %d, 周结束日期 = %s", weekNum, nextLastDayOfWeekStr);
            } catch (Exception e) {
                LogUtil.error("❌ 更新JSON配置文件时出错: %s", e.getMessage());
            }
        }
    }

    @Override
    protected void process(String[] argv) {
        if (argv.length < 3) {
            LogUtil.error("❌ 缺少必要参数，请提供脚本名、命令和内容 ");
            return;
        }

        String content = argv[2].trim();
        content = content.replaceAll("^\"|\"$", "");

        switch (content) {
            case "" -> {
                LogUtil.error("⚠️ 内容为空，无法写入");
                return;
            }
            case "new" -> {
                if (!argv[1].equals("r-meta")) {
                    LogUtil.error("元数据操作请使用 r-meta");
                    return;
                }
                // 处理更新周数操作
                handleWeekUpdate(argv);
                return;
            }
            case "sync" -> {
                if (!argv[1].equals("r-meta")) {
                    LogUtil.error("元数据操作请使用 r-meta");
                    return;
                }
                sync(argv);
                return;
            }
        }

        if (argv.length > 3) {
            // 说明有空格，合并后续的内容写入
            for (int i = 3; i < argv.length; i++) {
                content += (" " + argv[i]);
            }
        }

        // 处理常规日报写入
        handleDailyReport(content);
    }

    /**
     * 同步周数和周结束日期，以 json 配置为准
     * @param argv
     */
    private void sync(String[] argv) {
        // 获取JSON配置文件路径
        String reportPath = YamlConfig.getProperty(REPORT, WEEK_REPORT);
        Path reportFilePath = Path.of(reportPath);
        Path configPath = reportFilePath.getParent().resolve("settings.json");

        loadConfigFromJsonAndSync(configPath);

        int currentWeekNum = Integer.parseInt(YamlConfig.getProperty(REPORT, WEEK_NUM));
        String lastDayOfWeekStr = YamlConfig.getProperty(REPORT, LAST_DAY_OF_WEEK);

        String inputDateStr = argv.length == 4 ? argv[3] : lastDayOfWeekStr;

        try {
            LocalDate lastDayOfWeek = parseDate(inputDateStr);
            updateConfigFiles(currentWeekNum, lastDayOfWeek, configPath);
        } catch (Exception e) {
            LogUtil.error("更新周数失败，请检查日期字符串是否有误: %s", e.getMessage());
        }
    }

    /**
     * 更新周数和周结束日期，以 json 配置为准，
     * 取new的后一个字符串为日期字符串，如果没有则取json配置的周结束日期
     * 然后以此开启新的一周的计算
     * 并更新到 yaml 和 json 配置文件中
     * @param argv
     */
    private void handleWeekUpdate(String[] argv) {
        // 获取JSON配置文件路径
        String reportPath = YamlConfig.getProperty(REPORT, WEEK_REPORT);
        Path reportFilePath = Path.of(reportPath);
        Path configPath = reportFilePath.getParent().resolve("settings.json");

        int currentWeekNum = Integer.parseInt(YamlConfig.getProperty(REPORT, WEEK_NUM));
        String lastDayOfWeekStr = YamlConfig.getProperty(REPORT, LAST_DAY_OF_WEEK);

        String inputDateStr = argv.length == 4 ? argv[3] : lastDayOfWeekStr;

        try {
            LocalDate lastDayOfWeek = parseDate(inputDateStr);
            LocalDate nextLastDayOfWeek = lastDayOfWeek.plusDays(7);

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

        loadConfigFromJsonAndSync(configPath);

        LocalDate now = LocalDate.now();
        try {
            int weekNum = Integer.parseInt(YamlConfig.getProperty(REPORT, WEEK_NUM));
            LocalDate lastDayOfWeek = parseDate(YamlConfig.getProperty(REPORT, LAST_DAY_OF_WEEK));

            if (now.isAfter(lastDayOfWeek)) {
                LocalDate nextLastDayOfWeek = now.plusDays(6);
                String newWeekTitle = String.format("# Week%d[%s-%s]%n", weekNum, now.format(DATE_FORMATTER), nextLastDayOfWeek.format(DATE_FORMATTER));
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

    /**
     * 从JSON配置文件中读取周数和周结束日期
     * 更新到 yaml 和 json 配置文件中
     * @param configPath
     */
    private void loadConfigFromJsonAndSync(Path configPath) {
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
        Files.writeString(filePath, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        if (argv.length < 3) {
            hint(argv);
            return false;
        }
        return true;
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <content>", argv[0], argv[1]);
        LogUtil.usage("%s new [<last_day_of_week> in pattern yyyy.MM.dd]", argv[0], argv[1]);
    }
}