package io.github.meritepk.webapp.util;

import java.util.regex.Pattern;

public interface LogUtils {

    Pattern SANITIZER = Pattern.compile("[\r\n]");
    String EMPTY = "";

    static String sanitize(String value) {
        return value == null ? null : SANITIZER.matcher(value).replaceAll(EMPTY);
    }
}
