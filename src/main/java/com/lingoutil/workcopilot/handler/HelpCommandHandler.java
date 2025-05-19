package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.helpCommands;

public class HelpCommandHandler extends CommandHandler {

    private static final String HELP_FILE_NAME = "help.txt";

    @Override
    protected List<String> loadCommandList() {
        return helpCommands;
    }

    @Override
    protected void process(String[] argv) {
        // 从resources下加载help.txt文件输出
        try {
            InputStream inputStream = getClass().getResourceAsStream("/" + HELP_FILE_NAME);
            if (inputStream == null) {
                LogUtil.error("Help file not found.");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                LogUtil.info(line);
            }
            reader.close();
        } catch (IOException e) {
            LogUtil.error("Failed to read help file: " + e.getMessage(), e);
        }
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 2, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s", argv[0], argv[1]);
    }
}
