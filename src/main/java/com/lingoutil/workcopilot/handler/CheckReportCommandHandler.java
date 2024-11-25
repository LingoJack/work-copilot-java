package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class CheckReportCommandHandler extends CommandHandler {

    @Override
    protected List<String> loadCommandList() {
        return checkCommands;
    }

    @Override
    protected void process(String[] argv) {
        int lineNum = parseLineCount(argv);
        String reportPath = YamlConfig.getProperty(REPORT, WEEK_REPORT);

        LogUtil.info(false, "📂 正在读取周报文件路径: %s", reportPath);

        if (!validateFile(reportPath)) {
            return;
        }

        List<String> lines = readLastNLines(new File(reportPath), lineNum);
        logLines(lines);
    }

    private int parseLineCount(String[] argv) {
        int lineNum = 5; // 默认值为 5 行
        if (argv.length == 3) {
            try {
                lineNum = Integer.parseInt(argv[2].trim());
                if (lineNum <= 0) {
                    LogUtil.error("❌ 行数必须为正整数，请重试！");
                    return 5; // 回退到默认值
                }
            }
            catch (NumberFormatException e) {
                LogUtil.error("❌ 无效的行数参数: %s，请输入正确的数字！", argv[2].trim());
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

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long fileLength = raf.length();
            long pointer = fileLength;

            ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream();

            while (pointer > 0 && lines.size() < lineNum) {
                int bytesToRead = (int) Math.min(bufferSize, pointer);
                pointer -= bytesToRead;

                raf.seek(pointer);
                raf.readFully(buffer, 0, bytesToRead);

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
                    }
                    else {
                        lineBuffer.write(b);
                    }
                }
            }

            if (lineBuffer.size() > 0 && lines.size() < lineNum) {
                lines.add(0, decodeUTF8(lineBuffer));
            }
        }
        catch (IOException e) {
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

    private void logLines(List<String> lines) {
        LogUtil.info(false, "📄 最近的 %d 行内容如下：", lines.size());
        for (String line : lines) {
            LogUtil.info(true, line);
        }
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        int length = argv.length;
        if (length == 2 || length == 3) {
            return true;
        }
        else {
            hint(argv);
            return false;
        }
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("用法: %s %s [<行数，从文件尾部开始>]", argv[0], argv[1]);
        LogUtil.info(false, "❓ 示例：查看最近 10 行记录：check report 10");
    }
}
