package com.lingoutil.workcopilot.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    /**
     * 追加内容到文件中。
     *
     * @param filePath 文件路径
     * @param content  要追加的内容
     * @throws IOException 如果发生I/O错误
     */
    public static void appendToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8)) {
            writer.write(content + "\n");
        }
    }
}
