package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;
import org.apache.commons.logging.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

        File file = new File(reportPath);

        if (!file.exists()) {
            LogUtil.error("File on path {%s} does not exist", reportPath);
            return;
        }

        List<String> lines = readLastNLines(file, lineNum);

        for (String line : lines) {
            LogUtil.info(true, line);
        }
    }

    private int parseLineCount(String[] argv) {
        int lineNum = 5;
        if (argv.length == 3) {
            try {
                lineNum = Integer.parseInt(argv[2].trim());
            }
            catch (NumberFormatException e) {
                LogUtil.error("Invalid line count: %s", argv[2].trim());
                throw new IllegalArgumentException("Invalid line count", e);
            }
        }
        return lineNum;
    }

    private List<String> readLastNLines(File file, int lineNum) {

        if (file.length() < 10 * 1024 * 1024) { // 小于 10MB
            return readLastNLinesFast(file, lineNum);
        }
        else {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                Deque<String> deque = new LinkedList<>();

                String line;
                while ((line = reader.readLine()) != null) {
                    if (deque.size() == lineNum) {
                        deque.pollFirst();
                    }
                    deque.addLast(line);
                }

                lines.addAll(deque);
            }
            catch (IOException e) {
                LogUtil.error("Error reading file: %s", e.getMessage(), e);
            }
            return lines;
        }
    }

    private List<String> readLastNLinesFast(File file, int lineNum) {
        List<String> lines = new ArrayList<>();
        try {
            // 使用 NIO 读取文件
            List<String> allLines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

            // 直接提取最后 N 行
            int start = Math.max(0, allLines.size() - lineNum);
            lines.addAll(allLines.subList(start, allLines.size()));
        }
        catch (IOException e) {
            LogUtil.error("Error reading file: %s", e.getMessage(), e);
        }
        return lines;
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
        LogUtil.usage("%s %s [<line_count_from_tail>]", argv[0], argv[1]);
    }
}
