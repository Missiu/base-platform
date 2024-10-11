package com.example.template.util.encrypt;

import com.example.template.common.base.CommonConstants;
import com.example.template.common.properties.EncryptionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * hash密码工具类，使用SHA256算法，多次迭代哈希，并使用盐值进行加密
 */
@Slf4j
@Component
public class EncryptSHAWithSaltUtils {

    private static EncryptionProperties encryptionProperties;

    // 自动注入 EncryptionProperties (因为要使用static方法)
    @Autowired
    public EncryptSHAWithSaltUtils(EncryptionProperties encryptionProperties) {
        EncryptSHAWithSaltUtils.encryptionProperties = encryptionProperties;
    }


    /**
     * 生产随机盐值
     *
     * @return 加密后的字符串
     */
    public static String generateSalt() {
        byte[] salt = new byte[encryptionProperties.getSaltLength()];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 将密码和盐一起进行多次哈希
     *
     * @param password 密码
     * @param salt     盐值
     * @return 加密后的字符串
     */
    public static String hashPassword(String password, String salt) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(CommonConstants.SHA256);
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm: " + CommonConstants.SHA256, e);
            return null;
        }
        byte[] hashedBytes = (password + salt).getBytes();

        // 多次迭代哈希
        for (int i = 0; i < encryptionProperties.getHashIterations(); i++) {
            md.update(hashedBytes);
            hashedBytes = md.digest();
        }

        return Base64.getEncoder().encodeToString(hashedBytes);
    }


    /**
     * 验证密码是否正确
     *
     * @param inputPassword 密码
     * @param storedHash    存储的哈希值
     * @param salt          盐值
     * @return 验证成功返回true，否则返回false
     */
    public static boolean verifyPassword(String inputPassword, String storedHash, String salt){
        String hashedInput = hashPassword(inputPassword, salt);
        return hashedInput.equals(storedHash);
    }

}
