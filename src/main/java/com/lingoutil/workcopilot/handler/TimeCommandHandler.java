package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.util.LogUtil;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.lingoutil.workcopilot.constant.Constant.TIME_COUNTDOWN;
import static com.lingoutil.workcopilot.constant.Constant.timeCommands;

public class TimeCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return timeCommands;
    }

    @Override
    protected void process(String[] argv) {
        String function = argv[2];
        switch (function) {
            case TIME_COUNTDOWN -> {
                String threshold = argv[3];
                long durationInSeconds = parseDuration(threshold);
                if (durationInSeconds <= 0) {
                    LogUtil.error("Invalid duration: %s", threshold);
                    return;
                }

                LogUtil.info("⏳ Countdown started for %s seconds...", durationInSeconds);
                runCountdownWithProgress(durationInSeconds);
            }
            default -> LogUtil.error("❌ No such function %s", function);
        }
    }

    private long parseDuration(String threshold) {
        try {
            if (threshold.endsWith("s")) {
                return Long.parseLong(threshold.replace("s", ""));
            }
            else if (threshold.endsWith("m")) {
                return TimeUnit.MINUTES.toSeconds(Long.parseLong(threshold.replace("m", "")));
            }
            else if (threshold.endsWith("h")) {
                return TimeUnit.HOURS.toSeconds(Long.parseLong(threshold.replace("h", "")));
            }
            else {
                // 默认单位为分钟
                return TimeUnit.MINUTES.toSeconds(Long.parseLong(threshold));
            }
        }
        catch (NumberFormatException e) {
            LogUtil.error("Invalid duration format: %s", threshold);
            return -1;
        }
    }

    private void runCountdownWithProgress(long durationInSeconds) {
        try {
            long startTime = System.nanoTime();
            long total = durationInSeconds;

            for (long remaining = total; remaining > 0; remaining--) {
                // 显示倒计时
                String timeLeft = String.format("⏱️ %02d:%02d", remaining / 60, remaining % 60);

                // 计算进度条
                int progressWidth = 60; // 进度条总宽度
                int completed = (int) ((total - remaining) * progressWidth / total);
                String progressBar = "["
                        + "=".repeat(completed)
                        + ">"
                        + " ".repeat(progressWidth - completed - 1)
                        + "]";

                // 输出进度条和剩余时间
                System.out.printf("\r%s %s", timeLeft, progressBar);

                // 校准下一秒的输出时间
                long nextTick = startTime + (total - remaining + 1) * 1_000_000_000L;
                long sleepTime = nextTick - System.nanoTime();
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime / 1_000_000, (int) (sleepTime % 1_000_000));
                }
            }

            // 倒计时结束
            System.out.println("\r🎉 Time's up! [============================================================>] 🎉");
            beepOnFinish();
            displayCelebration();
        }
        catch (InterruptedException e) {
            LogUtil.error("Countdown interrupted!");
            Thread.currentThread().interrupt();
        }
    }


    private void displayCelebration() {
        String[] celebrationFrames = {
                "\uD83D\uDD14 Ding Ding! Time's Up!\uD83D\uDD14",
                "💢😤💢 Stop! Stop! Stop! 💢😤💢",
                "🔥😠🔥 How dare you don’t stop! 🔥😠🔥"
        };
        for (int i = 0; i < 6; i++) { // 播放6次动画
            System.out.printf("\r%s", celebrationFrames[i % celebrationFrames.length]);
            try {
                Thread.sleep(500); // 每帧显示500ms
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        LogUtil.newLine();
    }

    private void beepOnFinish() {
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    protected boolean checkArgs(String[] argv) {
        return checkArgs(argv, 4, this::hint);
    }

    @Override
    protected void hint(String[] argv) {
        LogUtil.usage("%s %s <function> <arg>", argv[0], argv[1]);
        LogUtil.info("""
                Functions:
                  countdown <time>   Starts a countdown. Examples:
                                      150s - 150 seconds
                                      1m   - 1 minute
                                      1h   - 1 hour
                                      Default unit: minutes
                """);
    }
}
