package com.lingoutil.workcopilot.completer;

import com.lingoutil.workcopilot.config.YamlConfig;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.Map;

/**
 * 配置项补全器，用于补全一级配置键
 */
public class ConfigCompleter implements Completer {
    
    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        List<String> topLevelKeys = YamlConfig.getAllTopLevelKeys();
        if (topLevelKeys != null) {
            for (String key : topLevelKeys) {
                candidates.add(new Candidate(key));
            }
        }
    }
}