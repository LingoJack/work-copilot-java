package com.lingoutil.workcopilot.util;

import java.net.URI;
import java.net.URISyntaxException;

public class URLUtil {
    public static boolean isURL(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }
        try {
            // 尝试解析为 URI
            URI uri = new URI(input);

            // 验证 scheme 是否为 http 或 https
            String scheme = uri.getScheme();
            return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
        }
        catch (URISyntaxException e) {
            return false;
        }
    }

}
