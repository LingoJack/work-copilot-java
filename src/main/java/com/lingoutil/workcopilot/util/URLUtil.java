package com.lingoutil.workcopilot.util;

import java.net.URI;
import java.net.URISyntaxException;

public class URLUtil {
    public static boolean isURL(String input) {
        if (input == null || input.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(input);
            String scheme = uri.getScheme();
            return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
        } catch (URISyntaxException e) {
            return false;
        }
    }

}
