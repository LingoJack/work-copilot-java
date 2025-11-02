package com.lingoutil.workcopilot.completer;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

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
        // 添加占位符作为候选项，但不实际补全（displ 和 value 不同）
        candidates.add(new Candidate(
                "", // value: 实际补全的值为空
                placeholder, // displ: 显示的占位符文本
                null, // group
                desc, // descr
                null, // suffix
                null, // key
                false // complete: 不自动补全
        ));
    }
}
