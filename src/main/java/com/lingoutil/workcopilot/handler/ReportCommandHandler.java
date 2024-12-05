package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class ReportCommandHandler extends CommandHandler {
	@Override
	protected List<String> loadCommandList() {
		return reportCommands;
	}

	private final static String NEW_WEEK_CONFIG_UPDATE = "new";

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

	private Instant parseDate(String dateStr) throws ParseException {
		return LocalDate.parse(dateStr, DATE_FORMATTER).atStartOfDay(ZoneId.systemDefault()).toInstant();
	}

	private void updateWeekConfig(int weekNum, Date nextLastDayOfWeek) {
		try {
			String nextLastDayOfWeekStr = new SimpleDateFormat("yyyy.MM.dd").format(nextLastDayOfWeek);
			YamlConfig.addNestedProperty(REPORT, WEEK_NUM, String.valueOf(weekNum));
			YamlConfig.addNestedProperty(REPORT, LAST_DAY_OF_WEEK, nextLastDayOfWeekStr);
			LogUtil.info("✅ 更新配置文件成功：周数 = %d, 周结束日期 = %s", weekNum, nextLastDayOfWeekStr);
		}
		catch (Exception e) {
			LogUtil.error("❌ 更新配置文件时出错: %s", e.getMessage());
		}
	}

	@Override
	protected void process(String[] argv) {
		if (argv.length < 3) {
			LogUtil.error("❌ 缺少必要参数，请提供脚本名、命令和内容。");
			return;
		}

		String content = argv[2].trim();

		// 如果 content 被引号包围 ""，去除引号
		if (content.startsWith("\"") && content.endsWith("\"")) {
			content = content.substring(1, content.length() - 1);
		}

		if (content.isEmpty()) {
			LogUtil.error("⚠️ 内容为空，无法写入。");
			return;
		}

		int weekNum = Integer.parseInt(YamlConfig.getProperty(REPORT, WEEK_NUM));
		String lastDayOfWeekStr = YamlConfig.getProperty(REPORT, LAST_DAY_OF_WEEK);

		if (content.equals(NEW_WEEK_CONFIG_UPDATE)) {
			String dataStr = argv.length == 4 ? argv[3] : lastDayOfWeekStr;
			Date lastDayOfWeek = null;
			Date nextLastDayOfWeek = null;

			try {
				lastDayOfWeek = new SimpleDateFormat("yyyy.MM.dd").parse(dataStr);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(lastDayOfWeek);
				calendar.add(Calendar.DAY_OF_MONTH, 7);
				nextLastDayOfWeek = calendar.getTime();
			}
			catch (ParseException e) {
				LogUtil.error("更新周数失败，请检查日期字符串是否有误");
				return;
			}
			updateWeekConfig(weekNum + 1, nextLastDayOfWeek);
			return;
		}

		String reportPath = YamlConfig.getProperty(REPORT, WEEK_REPORT);
		LogUtil.info("📂 从配置文件中读取到路径：%s", reportPath);

		File file = new File(reportPath);
		if (!file.exists()) {
			LogUtil.error("❌ 路径不存在：%s", reportPath);
			return;
		}

		Date now = new Date();
		try {
			Date lastDayOfWeek = new SimpleDateFormat("yyyy.MM.dd").parse(lastDayOfWeekStr);

			if (now.after(addOneDay(lastDayOfWeek))) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(now);
				calendar.add(Calendar.DAY_OF_MONTH, 6);
				Date nextLastDayOfWeek = calendar.getTime();
				String newWeekTitle = String.format("# Week%d[%s-%s]\n", weekNum,
						DATE_FORMATTER.format(now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()),
						DATE_FORMATTER.format(nextLastDayOfWeek.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
				);
				weekNum++;
				updateWeekConfig(weekNum, nextLastDayOfWeek);
				appendToFile(reportPath, newWeekTitle);
			}

			String todayStr = new SimpleDateFormat("yyyy/MM/dd").format(now);
			String logEntry = String.format("- 【%s】 %s\n", todayStr, content);
			appendToFile(reportPath, logEntry);
			LogUtil.info("✅ 成功将内容写入：%s", reportPath);
		}
		catch (Exception e) {
			LogUtil.error("❌ 操作时发生错误: %s", e.getMessage(), e);
		}
	}

	// 使用 UTF-8 编码的文件追加方法
	private void appendToFile(String filePath, String content) throws IOException {
		try (FileChannel channel = new FileOutputStream(filePath, true).getChannel()) {
			byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			channel.write(buffer);
		}
	}

	private Date addOneDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
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
