package com.lingoutil.workcopilot;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.handler.CommandHandler;
import com.lingoutil.workcopilot.util.LogUtil;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Scanner;

import static com.lingoutil.workcopilot.constant.Constant.*;

public class WorkCopilotApplication {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 设置控制台输出为UTF-8编码
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        while (true) {
            System.out.print("copilot > ");
            args = ("l "+sc.nextLine()).split(" ");
            boolean verboseMode = YamlConfig.getProperty(LOG, MODE).equals(MODE_VERBOSE);

            if (verboseMode) {
                LogUtil.log("verbose mode is start: %s", verboseMode);
                long startTime = System.currentTimeMillis();
                long endTime = 0;

                int len = args.length;
                LogUtil.log("command line argument length: %s", len);

                if (len != 2 && len != 3 && len != 4) {
                    LogUtil.info("Invalid number of arguments, arguments len: %s\n", len);
                    continue;
                }

                String command = args[1];
                CommandHandler.execute(command, args);

                endTime = System.currentTimeMillis();
                LogUtil.log("duration: %s ms", endTime - startTime);
            }
            else {
                int len = args.length;

                if (len != 2 && len != 3 && len != 4) {
                    LogUtil.info("Invalid number of arguments, arguments len: %s\n", len);
                    continue;
                }

                String command = args[1];
                CommandHandler.execute(command, args);
            }
            System.out.println();
        }
    }
}
