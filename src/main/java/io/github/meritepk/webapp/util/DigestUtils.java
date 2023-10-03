package io.github.meritepk.webapp.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.codec.Hex;

public interface DigestUtils {

    Charset UTF_8 = StandardCharsets.UTF_8;
    String SHA256 = "SHA-256";

    static MessageDigest getMessageDigest(String algo) {
        try {
            return MessageDigest.getInstance(algo);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    static byte[] digest(String algo, byte[] data) {
        return getMessageDigest(algo).digest(data);
    }

    static byte[] sha256(byte[] data) {
        return digest(SHA256, data);
    }

    static String sha256(String data) {
        return new String(Hex.encode(sha256(data.getBytes(UTF_8))));
    }
}
