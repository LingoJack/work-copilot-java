package com.lingoutil.workcopilot.config;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class YamlConfig {

    private static final String CONFIG_FILE_NAME = "application.yml";

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

            // 初始化缓存
            initializeCache();
        }
        catch (ConfigurationException e) {
            throw new RuntimeException("加载配置文件失败：" + CONFIG_FILE_NAME, e);
        }
    }

    private static void initializeCache() {
        Iterator<String> keys = config.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = config.getString(key);
            propertyCache.put(key, value);
        }
    }

    public static String getProperty(String... keys) {
        String fullKey = String.join(".", keys);
        return propertyCache.getOrDefault(fullKey, config.getString(fullKey));
    }

    public static Map<String, String> getPropertiesMap(String parentKey) {
        return propertiesMapCache.computeIfAbsent(parentKey, YamlConfig::loadPropertiesMap);
    }

    private static Map<String, String> loadPropertiesMap(String parentKey) {
        Map<String, String> properties = new LinkedHashMap<>();
        Iterator<String> keys = config.getKeys(parentKey);

        while (keys.hasNext()) {
            String key = keys.next();
            String shortKey = key.substring(parentKey.length() + 1); // 去掉父级前缀
            String value = config.getString(key);
            properties.put(shortKey, value);
        }

        return properties;
    }

    public static Iterator<String> getPropertiesIterator(String parentKey) {
        Map<String, String> properties = getPropertiesMap(parentKey);
        return properties.keySet().iterator();
    }

    /**
     * 添加顶级键值对到配置文件并保存。
     *
     * @param key   要添加的键
     * @param value 键对应的值
     */
    public static void addProperty(String key, String value) {
        config.setProperty(key, value);
        propertyCache.put(key, value);
        saveConfig();
    }

    /**
     * 添加嵌套键值对到配置文件并保存。
     *
     * @param parentKey 父级键
     * @param childKey  子级键
     * @param value     子级键对应的值
     */
    public static void addNestedProperty(String parentKey, String childKey, String value) {
        String fullKey = parentKey + "." + childKey;
        config.setProperty(fullKey, value);
        propertyCache.put(fullKey, value);
        saveConfig();
    }

    /**
     * 保存配置到 YAML 文件。
     */
    private static void saveConfig() {
        try {
            builder.save();
        }
        catch (ConfigurationException e) {
            throw new RuntimeException("保存配置文件失败：" + CONFIG_FILE_NAME, e);
        }
    }

    /**
     * 删除配置文件中的指定键值对，并保存更改。
     *
     * @param key 要删除的键
     */
    public static void removeProperty(String key) {
        if (config.containsKey(key)) {
            config.clearProperty(key); // 删除指定的键值对
            propertyCache.remove(key);
            saveConfig();             // 保存更改
        }
    }

    /**
     * 删除配置文件中的嵌套键值对，并保存更改。
     *
     * @param parentKey 父级键
     * @param childKey  子级键
     */
    public static void removeNestedProperty(String parentKey, String childKey) {
        String fullKey = parentKey + "." + childKey;
        removeProperty(fullKey);
    }
}
