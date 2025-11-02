package com.lingoutil.workcopilot.completer;

import org.apache.commons.text.diff.StringsComparator;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import java.util.List;

/**
 * 占位符补全器，用于提供占位符提示
 */
public class PlaceholderCompleter implements Completer {

    private final String placeholder;

    private final String desc;

    /**
     * 构造函数
     * 
     * @param placeholder 占位符文本
     */
    public PlaceholderCompleter(String placeholder, String desc) {
        this.placeholder = placeholder;
        this.desc = desc;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        Candidate candidate = new Candidate(placeholder, desc, null, null, null, null, false);
        StringsCompleter stringsCompleter = new StringsCompleter(candidate);
        stringsCompleter.complete(reader, line, candidates);
    }
}
