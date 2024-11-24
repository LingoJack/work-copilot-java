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
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                return false;
            }

            // 验证 host 是否为 localhost
            String host = uri.getHost();
            if (!"localhost".equalsIgnoreCase(host)) {
                return false;
            }

            // 验证是否有端口
            int port = uri.getPort();
            if (port == -1) { // -1 表示未指定端口
                return false;
            }

            return true;
        }
        catch (URISyntaxException e) {
            return false;
        }
    }
}
