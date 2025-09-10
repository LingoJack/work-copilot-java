package com.lingoutil.workcopilot.config;

import com.lingoutil.workcopilot.util.LogUtil;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class YamlConfig {

    private static final String CONFIG_FILE_NAME = "config.yaml";

    private static final FileBasedConfigurationBuilder<YAMLConfiguration> builder;
    private static final Configuration config;

    // 缓存
    private static final ConcurrentHashMap<String, String> propertyCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Map<String, String>> propertiesMapCache = new ConcurrentHashMap<>();

    static {
        Configurations configs = new Configurations();
        try {
            builder = configs.fileBasedBuilder(YAMLConfiguration.class, CONFIG_FILE_NAME);
            config = builder.getConfiguration();
            initializeCache();
        } catch (ConfigurationException e) {
            LogUtil.error("加载配置文件失败：" + CONFIG_FILE_NAME);
            throw new RuntimeException("加载配置文件失败：" + CONFIG_FILE_NAME, e);
        }
    }

    private static void initializeCache() {
        Iterator<String> keys = config.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = config.getString(key);
            propertyCache.put(key, value);
            LogUtil.log("缓存添加键值对: " + key + " = " + value);
        }
    }

    // 获取属性值，如果缓存中没有，则重新加载
    public static String getProperty(String... keys) {
        String fullKey = String.join(".", keys);
        LogUtil.log("获取属性值，键：" + fullKey);

        if (!propertyCache.containsKey(fullKey)) {
            LogUtil.log("缓存中未找到键，重新加载...");
            reloadCache();
        }
        return propertyCache.getOrDefault(fullKey, config.getString(fullKey));
    }

    public static String initializeProperty(String... keys) {
        String fullKey = String.join(".", keys);
        return config.getString(fullKey);
    }

    // 获取属性值，支持刷新缓存
    public static String getProperty(boolean flush, String... keys) {
        String fullKey = String.join(".", keys);
        LogUtil.log("获取属性值，键：" + fullKey + "，是否刷新缓存：" + flush);

        if (flush || !propertyCache.containsKey(fullKey)) {
            LogUtil.log("缓存中未找到或刷新标志为真，获取配置文件中的值...");
            return config.getString(fullKey);
        }
        return propertyCache.getOrDefault(fullKey, config.getString(fullKey));
    }

    // 获取属性映射，如果缓存中没有，则重新加载
    public static Map<String, String> getPropertiesMap(String parentKey) {
        LogUtil.log("获取属性映射，父级键：" + parentKey);
        if (!propertiesMapCache.containsKey(parentKey)) {
            LogUtil.log("缓存中未找到，重新加载...");
            propertiesMapCache.put(parentKey, loadPropertiesMap(parentKey));
        }
        return propertiesMapCache.computeIfAbsent(parentKey, YamlConfig::loadPropertiesMap);
    }

    // 加载属性映射
    private static Map<String, String> loadPropertiesMap(String parentKey) {
        LogUtil.log("加载属性映射，父级键：" + parentKey);
        Map<String, String> properties = new LinkedHashMap<>();
        Iterator<String> keys = config.getKeys(parentKey);

        while (keys.hasNext()) {
            String key = keys.next();
            String shortKey = key.substring(parentKey.length() + 1); // 去掉父级前缀
            String value = config.getString(key);
            properties.put(shortKey, value);
            LogUtil.log("映射添加键值对: " + shortKey + " = " + value);
        }

        return properties;
    }

    public static Iterator<String> getPropertiesIterator(String parentKey) {
        Map<String, String> properties = getPropertiesMap(parentKey);
        LogUtil.log("返回属性映射的键的迭代器，父级键：" + parentKey);
        return properties.keySet().iterator();
    }

    // 添加顶级键值对到配置文件并保存
    public static void addProperty(String key, String value) {
        LogUtil.log("添加顶级键值对: " + key + " = " + value);
        config.setProperty(key, value);
        propertyCache.put(key, value);
        propertiesMapCache.clear();
        saveConfig();
    }

    // 添加嵌套键值对到配置文件并保存
    public static void addNestedProperty(String parentKey, String childKey, String value) {
        String fullKey = parentKey + "." + childKey;
        LogUtil.log("添加嵌套键值对: " + fullKey + " = " + value);
        config.setProperty(fullKey, value);
        propertyCache.put(fullKey, value);
        propertiesMapCache.clear();
        saveConfig();
    }

    // 保存配置到 YAML 文件
    private static void saveConfig() {
        try {
            LogUtil.log("保存配置到文件...");
            builder.save();
        } catch (ConfigurationException e) {
            LogUtil.log("保存配置文件失败：" + CONFIG_FILE_NAME);
            throw new RuntimeException("保存配置文件失败：" + CONFIG_FILE_NAME, e);
        }
    }

    // 删除配置文件中的指定键值对，并保存更改
    public static void removeProperty(String key) {
        LogUtil.log("删除配置中的键: " + key);
        if (config.containsKey(key)) {
            config.clearProperty(key); // 删除指定的键值对
            propertyCache.remove(key);
            propertiesMapCache.clear();
            saveConfig();             // 保存更改
        }
    }

    // 删除配置文件中的嵌套键值对，并保存更改
    public static void removeNestedProperty(String parentKey, String childKey) {
        String fullKey = parentKey + "." + childKey;
        LogUtil.log("删除嵌套键值对: " + fullKey);
        removeProperty(fullKey);
    }

    // 清理缓存
    public static void clearCache() {
        LogUtil.log("清理缓存...");
        propertyCache.clear();
        propertiesMapCache.clear();
    }

    // 重新加载缓存
    private static void reloadCache() {
        LogUtil.log("重新加载缓存...");
        clearCache();
        initializeCache();
    }

    // 重命名配置文件中的键，并刷新缓存
    public static void renameProperty(String oldKey, String newKey) {
        LogUtil.log("重命名键，从 " + oldKey + " 到 " + newKey);
        if (config.containsKey(oldKey)) {
            String value = config.getString(oldKey);
            removeProperty(oldKey); // 删除旧的键
            addProperty(newKey, value); // 添加新的键
        }
    }

    public static void renameProperty(String topKey, String oldShortKey, String newShortKey) {
        String oldKey = topKey + "." + oldShortKey;
        String newKey = topKey + "." + newShortKey;
        LogUtil.log("重命名嵌套键，从 " + oldKey + " 到 " + newKey);
        if (config.containsKey(oldKey)) {
            String value = config.getString(oldKey);
            removeProperty(oldKey); // 删除旧的键
            addProperty(newKey, value); // 添加新的键
        }
    }

    public static boolean containProperty(String... keys) {
        String fullKey = String.join(".", keys);
        return propertyCache.containsKey(fullKey);
    }

    public static List<String> getAllTopLevelKeys() {
        LogUtil.log("获取所有顶级键...");
        Set<String> topLevelKeys = new HashSet<>();
        Iterator<String> keys = config.getKeys();

        while (keys.hasNext()) {
            String key = keys.next();
            // 获取顶级键（在第一个"."之前的部分）
            int dotIndex = key.indexOf('.');
            String topKey = (dotIndex == -1) ? key : key.substring(0, dotIndex);
            topLevelKeys.add(topKey);
        }

        LogUtil.log("顶级键: " + topLevelKeys);
        return new ArrayList<>(topLevelKeys);
    }
}
