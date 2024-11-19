package com.lingoutil.workcopilot.util;

import java.net.URI;
import java.net.URISyntaxException;

public class URLUtil {
    public static boolean isURL(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        try {
            new URI(input);
            return true;
        }
        catch (URISyntaxException e) {
            return false;
        }
    }
}
