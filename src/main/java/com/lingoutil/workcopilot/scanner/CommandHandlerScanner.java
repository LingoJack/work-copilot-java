package com.lingoutil.workcopilot.scanner;

import com.lingoutil.workcopilot.handler.CommandHandler;
import com.lingoutil.workcopilot.util.LogUtil;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CommandHandlerScanner {

    private static final Set<String> registeredClasses = new HashSet<>();

    public static void scanAndRegisterHandlers(String packageName) {
        try {
            LogUtil.log("开始扫描包: %s", packageName);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');

            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("jar")) {
                    processJar(resource, packageName);
                } else {
                    processFileSystem(resource, packageName);
                }
            }
        } catch (Exception e) {
            LogUtil.error("扫描失败: " + e.getMessage(), e);
        }
    }

    private static void processJar(URL jarUrl, String packageName) throws Exception {
        String jarPath = jarUrl.getPath().substring(0, jarUrl.getPath().indexOf("!"));
        try (JarFile jar = new JarFile(new URL(jarPath).getFile())) {
            Enumeration<JarEntry> entries = jar.entries();
            String packagePath = packageName.replace('.', '/');

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                    String className = entryName
                            .replace('/', '.')
                            .substring(0, entryName.length() - 6);
                    registerIfHandler(className);
                }
            }
        }
    }

    private static void processFileSystem(URL fileUrl, String packageName) throws Exception {
        File dir = new File(fileUrl.toURI());
        if (!dir.exists()) return;

        scanDir(dir, packageName);
    }

    private static void scanDir(File dir, String packageName) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDir(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' +
                        file.getName().substring(0, file.getName().length() - 6);
                registerIfHandler(className);
            }
        }
    }

    private static void registerIfHandler(String className) {
        if (registeredClasses.contains(className)) return;

        try {
            Class<?> clazz = Class.forName(className);
            if (isConcreteHandler(clazz)) {
                CommandHandler handler = (CommandHandler) clazz.getDeclaredConstructor().newInstance();
                CommandHandler.register(handler);
                registeredClasses.add(className);
                LogUtil.log("已注册处理器: %s", className);
            }
        } catch (ClassNotFoundException e) {
            LogUtil.log("类未找到: %s (可能依赖缺失)", className);
        } catch (Exception e) {
            LogUtil.log("注册失败: %s, 原因: %s", className, e.getMessage());
        }
    }

    private static boolean isConcreteHandler(Class<?> clazz) {
        return CommandHandler.class.isAssignableFrom(clazz) &&
                !clazz.isInterface() &&
                !clazz.isAnnotation() &&
                !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers());
    }
}