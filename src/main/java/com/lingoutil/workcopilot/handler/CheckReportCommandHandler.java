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
        LogUtil.log("Report path: %s", reportPath);

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
                    throw new IllegalArgumentException("行数必须为正数");
                }
            }
            catch (NumberFormatException e) {
                LogUtil.error("无效的行数参数: %s", argv[2].trim());
                throw new IllegalArgumentException("行数参数无效", e);
            }
        }
        return lineNum;
    }

    private boolean validateFile(String reportPath) {
        File file = new File(reportPath);
        if (!file.exists()) {
            LogUtil.error("路径上的文件不存在: %s", reportPath);
            return false;
        }
        if (!file.isFile()) {
            LogUtil.error("路径不是一个文件: %s", reportPath);
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

                // 从后往前处理缓冲区内容
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

            // 处理文件开头未换行的最后一行
            if (lineBuffer.size() > 0 && lines.size() < lineNum) {
                lines.add(0, decodeUTF8(lineBuffer));
            }
        }
        catch (IOException e) {
            LogUtil.error("读取文件时发生错误: %s", e.getMessage(), e);
        }
        return lines;
    }

    private String decodeUTF8(ByteArrayOutputStream lineBuffer) throws UnsupportedEncodingException {
        byte[] bytes = lineBuffer.toByteArray();
        for (int i = 0, j = bytes.length - 1; i < j; i++, j--) {
            byte temp = bytes[i];
            bytes[i] = bytes[j];
            bytes[j] = temp;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private void logLines(List<String> lines) {
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
        LogUtil.usage("%s %s [<行数，从文件尾部开始>]", argv[0], argv[1]);
    }
}
