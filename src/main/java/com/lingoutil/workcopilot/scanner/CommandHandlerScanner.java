package com.lingoutil.workcopilot.scanner;

import com.lingoutil.workcopilot.handler.CommandHandler;
import com.lingoutil.workcopilot.util.LogUtil;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class CommandHandlerScanner {

    private static final Set<String> scannedDirectories = new HashSet<>();
    private static final Set<String> registeredClasses = new HashSet<>();

    public static void scanAndRegisterHandlers(String packageName) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File dir = new File(resource.getFile());
                if (dir.exists() && !scannedDirectories.contains(dir.getAbsolutePath())) {
                    scanDirectory(dir, packageName);
                    scannedDirectories.add(dir.getAbsolutePath());
                }
            }
        }
        catch (Exception e) {
            LogUtil.error("Failed to scan and register command handlers: " + e.getMessage(), e);
        }
    }

    private static void scanDirectory(File directory, String packageName) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName());
            }
            else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                if (!registeredClasses.contains(className)) {
                    try {
                        Class<?> clazz = Class.forName(className);
                        if (CommandHandler.class.isAssignableFrom(clazz) && !clazz.equals(CommandHandler.class)) {
                            CommandHandler handler = (CommandHandler) clazz.getDeclaredConstructor().newInstance();
                            LogUtil.log("Registered command handler: %s", clazz.getName());
                            CommandHandler.register(handler);
                            registeredClasses.add(className);
                        }
                    }
                    catch (Exception e) {
                        LogUtil.log("Failed to instantiate command handler: " + className, e);
                    }
                }
            }
        }
    }
}
