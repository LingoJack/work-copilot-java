package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.component.FuzzyMatcher;
import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class SearchCommandHandler extends CommandHandler {

	private List<String> options = List.of("-f", "-fuzzy");

	@Override
	protected List<String> loadCommandList() {
		return searchCommands;
	}

	@Override
	protected void process(String[] argv) {
		int lineNum = parseLineCount(argv);

		if (lineNum == -1) {
			return;
		}

		String reportPath = YamlConfig.getProperty(REPORT, WEEK_REPORT);

		LogUtil.info(false, "📂 正在读取周报文件路径: %s", reportPath);

		if (!validateFile(reportPath)) {
			return;
		}

		List<String> lines = readLastNLines(new File(reportPath), lineNum);

		String target = argv[3];

		boolean fuzzyMatch = false;
		if (argv.length == 5 && options.contains(argv[4])) {
			LogUtil.info("启用模糊匹配...");
			fuzzyMatch = true;
		}

		// 高效找出lines里含有target字符串的行，并按照格式"[序号] xxx"输出，其中涉及到target的要高亮显示为黄色
		LogUtil.info("🔍 搜索目标关键字: %s", getHighlightTargetStr(target));

		int index = 0; // 输出行的序号
		for (String line : lines) {
			if (match(line, target, fuzzyMatch)) {
				index++;
				String highlightedLine = processMatchLine(line, target, fuzzyMatch);
				LogUtil.info("[%d] %s", index, highlightedLine);
			}
		}

		if (index == 0) {
			LogUtil.info("nothing found \uD83D\uDE22");
		}
	}

	private String processMatchLine(String line, String target, boolean fuzzy) {
		if (!fuzzy) {
			return line.replaceAll(target, getHighlightTargetStr(target));
		} else {

			List<FuzzyMatcher.Interval> intervals = FuzzyMatcher.getMatchIntervals(line, target);
			if (intervals.isEmpty()) {
				return line; // 如果没有匹配，直接返回原始行
			}
			// 创建一个可变的 StringBuilder 用于替换和高亮
			StringBuilder highlightedLine = new StringBuilder(line);
			int offset = 0; // 用于调整替换后字符的偏移量
			for (FuzzyMatcher.Interval interval : intervals) {
				int lowerBound = interval.getLowerBound() + offset;
				int upperBound = interval.getUpperBound() + offset;

				// 提取当前区间的字符串
				String matchedTarget = highlightedLine.substring(lowerBound, upperBound + 1);

				// 将匹配部分替换为高亮内容
				String highlightedTarget = getHighlightTargetStr(matchedTarget);

				// 替换并调整偏移量
				highlightedLine.replace(lowerBound, upperBound + 1, highlightedTarget);
				offset += highlightedTarget.length() - matchedTarget.length();
			}
			return highlightedLine.toString();
		}
	}

	private static boolean match(String line, String target, boolean fuzzy) {
		return fuzzy ? FuzzyMatcher.match(line, target) : line.contains(target);
	}

	private String getHighlightTargetStr(String target) {
		return LogUtil.highlight(target, LogUtil.GREEN);
	}

	@Override
	protected boolean checkArgs(String[] argv) {
		return checkArgs(argv, this::hint, 4, 5);
	}

	@Override
	protected void hint(String[] argv) {
		LogUtil.usage("%s %s <line_count_from_tail> <target>", argv[0], argv[1]);
	}

	private int parseLineCount(String[] argv) {
		int lineNum = 5; // 默认值为 5 行
		if (argv.length == 4 || argv.length == 5) {
			try {
				if (argv[2].equals("all")) {
					lineNum = Integer.MAX_VALUE;
				} else {
					lineNum = Integer.parseInt(argv[2].trim());
					if (lineNum <= 0) {
						LogUtil.error("❌ 行数必须为正整数或`all`，请重试！");
						return 5; // 回退到默认值
					}
				}
			} catch (NumberFormatException e) {
				LogUtil.error("❌ 无效的行数参数: %s，请输入正确的数字！", argv[2].trim());
				return -1;
			}
		}
		return lineNum;
	}

	private boolean validateFile(String reportPath) {
		File file = new File(reportPath);
		if (!file.exists()) {
			LogUtil.error("❌ 文件不存在: %s，请检查路径是否正确！", reportPath);
			return false;
		}
		if (!file.isFile()) {
			LogUtil.error("❌ 路径不是有效文件: %s，请提供一个有效的文件路径！", reportPath);
			return false;
		}
		return true;
	}

	private List<String> readLastNLines(File file, int lineNum) {
		List<String> lines = new LinkedList<>();
		int bufferSize = 16384; // 每次读取 16KB
		byte[] buffer = new byte[bufferSize];
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
			long fileLength = randomAccessFile.length();
			long pointer = fileLength;
			ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream();
			while (pointer > 0 && lines.size() < lineNum) {
				int bytesToRead = (int) Math.min(bufferSize, pointer);
				pointer -= bytesToRead;
				randomAccessFile.seek(pointer);
				randomAccessFile.readFully(buffer, 0, bytesToRead);
				for (int i = bytesToRead - 1; i >= 0; i--) {
					byte b = buffer[i];
					if (b == '\n') {
						if (lineBuffer.size() > 0) {
							lines.add(0, decodeUTF8(lineBuffer));
							lineBuffer.reset();
							if (lines.size() >= lineNum) {
								break;
							}
						}
					} else {
						lineBuffer.write(b);
					}
				}
			}
			if (lineBuffer.size() > 0 && lines.size() < lineNum) {
				lines.add(0, decodeUTF8(lineBuffer));
			}
		} catch (IOException e) {
			LogUtil.error("❌ 读取文件时发生错误: %s", e.getMessage(), e);
		}
		return lines;
	}

	private String decodeUTF8(ByteArrayOutputStream lineBuffer) {
		byte[] bytes = lineBuffer.toByteArray();
		reverseArray(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	private void reverseArray(byte[] array) {
		for (int i = 0, j = array.length - 1; i < j; i++, j--) {
			byte temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}
	}
}
