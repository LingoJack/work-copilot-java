package com.lingoutil.workcopilot.completer;

import com.lingoutil.workcopilot.config.YamlConfig;
import com.lingoutil.workcopilot.util.LogUtil;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.Map;

/**
 * 配置项补全器，根据上级配置键补全下级配置项
 */
public class ConfigItemCompleter implements Completer {
    
    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        // 获取当前行的所有单词
        List<String> words = line.words();
        
        // 如果有足够的参数，获取父级配置键（第二个参数，索引为1）
        if (words.size() >= 2) {
            String parentKey = words.get(1);
            
            Map<String, String> properties = YamlConfig.getPropertiesMap(parentKey);
            
            if (properties != null) {
                for (String key : properties.keySet()) {
                    candidates.add(new Candidate(key));
                }
            }
        }
    }
}