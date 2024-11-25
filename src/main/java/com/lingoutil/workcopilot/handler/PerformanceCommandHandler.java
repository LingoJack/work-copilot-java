package com.lingoutil.workcopilot.handler;

import com.lingoutil.workcopilot.util.LogUtil;

import java.lang.management.*;
import java.util.List;

import static com.lingoutil.workcopilot.constant.Constant.performanceCommands;

public class PerformanceCommandHandler extends CommandHandler {
    @Override
    protected List<String> loadCommandList() {
        return performanceCommands;
    }

    @Override
    protected void process(String[] argv) {
        // 获取操作系统信息
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        // 获取 JVM 运行时信息
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        // 获取内存信息
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        // 打印操作系统信息
        LogUtil.info("🖥️ 操作系统: %s %s (%s)",
                osBean.getName(), osBean.getVersion(), osBean.getArch());
        LogUtil.info("⚙️ 可用处理器: %s%d%s",
                LogUtil.GREEN, osBean.getAvailableProcessors(), LogUtil.RESET);

        // 打印 JVM 信息
        LogUtil.info("☕ JVM 名称: %s%s%s",
                LogUtil.GREEN, runtimeBean.getVmName(), LogUtil.RESET);
        LogUtil.info("📜 JVM 版本: %s%s%s",
                LogUtil.GREEN, runtimeBean.getVmVersion(), LogUtil.RESET);
        LogUtil.info("⏳ JVM 启动时间: %s%d ms%s",
                LogUtil.GREEN, runtimeBean.getStartTime(), LogUtil.RESET);

        long uptime = runtimeBean.getUptime();
        LogUtil.info("⏱️ 运行时间: %s%s%s",
                LogUtil.GREEN, formatUptime(uptime), LogUtil.RESET);

        // 打印内存使用情况
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();

        long totalUsed = heapUsage.getUsed() + nonHeapUsage.getUsed();
        long totalCommitted = heapUsage.getCommitted() + nonHeapUsage.getCommitted();
        long totalMax = (heapUsage.getMax() == -1 || nonHeapUsage.getMax() == -1)
                ? -1 : heapUsage.getMax() + nonHeapUsage.getMax();

        LogUtil.info("📦 堆内存使用: 已用 %s%d MB%s, 已分配 %s%d MB%s, 最大 %s%s%s",
                LogUtil.GREEN, bytesToMB(heapUsage.getUsed()), LogUtil.RESET,
                LogUtil.GREEN, bytesToMB(heapUsage.getCommitted()), LogUtil.RESET,
                LogUtil.GREEN, heapUsage.getMax() == -1 ? "NAN" : bytesToMB(heapUsage.getMax()) + " MB", LogUtil.RESET);

        LogUtil.info("🗂️ 非堆内存使用: 已用 %s%d MB%s, 已分配 %s%d MB%s, 最大 %s%s%s",
                LogUtil.GREEN, bytesToMB(nonHeapUsage.getUsed()), LogUtil.RESET,
                LogUtil.GREEN, bytesToMB(nonHeapUsage.getCommitted()), LogUtil.RESET,
                LogUtil.GREEN, nonHeapUsage.getMax() == -1 ? "NAN" : bytesToMB(nonHeapUsage.getMax()) + " MB", LogUtil.RESET);

        LogUtil.info("\uD83D\uDCBE 总内存使用: 已用 %s%d MB%s, 已分配 %s%d MB%s, 最大 %s%s%s",
                LogUtil.GREEN, bytesToMB(totalUsed), LogUtil.RESET,
                LogUtil.GREEN, bytesToMB(totalCommitted), LogUtil.RESET,
                LogUtil.GREEN, totalMax == -1 ? "NAN" : bytesToMB(totalMax) + " MB", LogUtil.RESET);

        // 新增：打印当前进程的 CPU 使用率
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean extendedOsBean = (com.sun.management.OperatingSystemMXBean) osBean;

            // 获取进程 CPU 使用率 (0.0 到 1.0)
            double processCpuLoad = extendedOsBean.getProcessCpuLoad() * 100;

            // 打印进程 CPU 使用率
            LogUtil.info("🚀 当前进程 CPU 使用率: %s%.2f%%%s",
                    LogUtil.GREEN, processCpuLoad, LogUtil.RESET);
        }
        else {
            LogUtil.error("⚠️ 当前 JVM 不支持获取进程 CPU 使用率！");
        }
    }


    private String formatUptime(long uptimeMillis) {
        long days = uptimeMillis / (24 * 60 * 60 * 1000);
        uptimeMillis %= (24 * 60 * 60 * 1000);
        long hours = uptimeMillis / (60 * 60 * 1000);
        uptimeMillis %= (60 * 60 * 1000);
        long minutes = uptimeMillis / (60 * 1000);
        uptimeMillis %= (60 * 1000);
        long seconds = uptimeMillis / 1000;
        long milliseconds = uptimeMillis % 1000;

        return String.format("%d 天 %02d 小时 %02d 分钟 %02d 秒 %03d 毫秒", days, hours, minutes, seconds, milliseconds);
    }

    private long bytesToMB(long bytes) {
        return bytes >> 20; // 等价于 bytes / (1024 * 1024)
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
