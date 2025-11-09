package com.example.prn392_project.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    /**
     * Băm mật khẩu bằng SHA-256.
     * @param password Mật khẩu gốc
     * @return Chuỗi băm (hashed)
     */
    public static String hashPassword(String password) {
        // Trong ứng dụng thực tế, BẠN NÊN DÙNG Bcrypt hoặc Argon2 thay vì SHA-256
        // SHA-256 là đủ cho mục đích demo, nhưng dễ bị tấn công rainbow table.
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Chuyển đổi mảng byte sang chuỗi Hex.
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
