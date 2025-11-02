package com.lingoutil.workcopilot.completer;

import java.util.List;
import java.util.Map;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import com.lingoutil.workcopilot.config.YamlConfig;
import static com.lingoutil.workcopilot.constant.Constant.*;

// 可刷新的别名补全器
public class RefreshableAliasCompleter implements Completer {

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        List<String> categories = List.of(PATH, INNER_URL, OUTER_URL);
        for (String category : categories) {
            Map<String, String> aliases = YamlConfig.getPropertiesMap(category);
            if (aliases != null) {
                for (String alias : aliases.keySet()) {
                    candidates.add(new Candidate(alias, alias, category, null, null, null, true));
                }
            }
        }
    }
}
