package io.github.meritepk.webapp.util;

public interface EntityUtils {

    static String encode(Long value) {
        return value != null ? encode(value.longValue()) : null;
    }

    static String encode(long value) {
        return Long.toHexString(value);
    }

    static Long decode(String value) {
        try {
            return value != null ? Long.valueOf(value, 16) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
