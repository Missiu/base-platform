package com.example.template.util;

import com.example.template.common.base.CommonConstants;
import com.example.template.common.properties.EncryptionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author hzh
 * @data 2024/10/15 19:51
 */
@Slf4j
@Component
public class EncryptUtils {
    private static EncryptionProperties encryptionProperties;

    @Autowired
    public EncryptUtils(EncryptionProperties encryptionProperties) {
        EncryptUtils.encryptionProperties = encryptionProperties;
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
    public static String hashPasswordWithSalt(String password, String salt) {
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
     * 验证被哈希加密后的密码是否正确
     *
     * @param salt          盐值
     * @return 验证成功返回true，否则返回false
     */
    public static boolean verifyHashedPassword(String userPassword, String hashPassword, String salt){
        String hashedInput = hashPasswordWithSalt(userPassword, salt);
        return hashPassword.equals(hashedInput);
    }

    /**
     * 生成RSA密钥对
     */
    public static KeyPair generateKeyPair() {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance(CommonConstants.RSA);
        } catch (NoSuchAlgorithmException e) {
            log.error("generateKeyPair error, error:{}", e.getMessage());
            return null;
        }
        // 设置 RSA 密钥长度
        keyGen.initialize(encryptionProperties.getKeySize());
        return keyGen.generateKeyPair();
    }

    /**
     * 使用公钥加密
     *
     * @param data      待加密数据
     * @param publicKey 公钥
     */
    public static String encryptPasswordWithPublicKey(String data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(CommonConstants.RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("encryptWithPublicKey error, data:{}, publicKey:{}, error:{}", data, publicKey, e.getMessage());
            return null;
        }
    }

    /**
     * 从字符串获取私钥
     *
     * @param privateKey    私钥
     * @param encryptedData 加密数据
     */
    public static String decryptWithPrivateKey(String encryptedData, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(CommonConstants.RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error("decryptWithPrivateKey error, encryptedData:{}, privateKey:{}, error:{}", encryptedData, privateKey, e.getMessage());
            return null;
        }
    }

    /**
     * 将公钥转换为字符串
     *
     * @param publicKey 公钥
     * @return 公钥字符串
     */
    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 将私钥转换为字符串
     *
     * @param privateKey 私钥
     * @return 私钥字符串
     */
    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * 从字符串恢复公钥
     *
     * @param key 公钥字符串
     * @return 公钥对象
     * @throws Exception 异常
     */
    public static PublicKey stringToPublicKey(String key) {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(CommonConstants.RSA);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            log.error("stringToPublicKey error, key:{}, error:{}", key, e.getMessage());
            return null;
        }
    }

    /**
     * 从字符串恢复私钥
     *
     * @param key 私钥字符串
     * @return 私钥对象
     */
    public static PrivateKey stringToPrivateKey(String key) {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(CommonConstants.RSA);
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            log.error("stringToPublicKey error, key:{}, error:{}", key, e.getMessage());
            return null;
        }
    }

    /**
     * 自定义公钥加密
     *
     * @param plaintext    明文
     * @param publicKeyStr 公钥字符串
     */
    public static String encrypt(String plaintext, String publicKeyStr) {
        try {
            PublicKey publicKey = stringToPublicKey(publicKeyStr);
            Cipher cipher = Cipher.getInstance(CommonConstants.RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("encrypt error, plaintext:{}, publicKeyStr:{}, error:{}", plaintext, publicKeyStr, e.getMessage());
            return null;
        }
    }

    /**
     * 自定义私钥解密
     *
     * @param ciphertext    密文
     * @param privateKeyStr 私钥字符串
     */
    public static String decrypt(String ciphertext, String privateKeyStr) {
        try {
            PrivateKey privateKey = stringToPrivateKey(privateKeyStr);
            Cipher cipher = Cipher.getInstance(CommonConstants.RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error("decrypt error, ciphertext:{}, privateKeyStr:{}, error:{}", ciphertext, privateKeyStr, e.getMessage());
            return null;
        }
    }

    /**
     * 生成可逆hash值
     */
    public static String generateHash(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(CommonConstants.MD5);
            byte[] hashBytes = md.digest(data.getBytes(StandardCharsets.UTF_8));
            // 使用 Base64 编码，并截取前25个字符，得到更短的唯一值
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("No such algorithm: " + CommonConstants.MD5, e);
            return null;
        }
    }
}
