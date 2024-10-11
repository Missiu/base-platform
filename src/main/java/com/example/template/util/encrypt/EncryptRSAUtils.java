package com.example.template.util.encrypt;

import com.example.template.common.base.CommonConstants;
import com.example.template.common.properties.EncryptionProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 非对称加密工具类
 * 使用前需要确保自己有RSA算法公钥和私钥，可以通过generateKeyPair方法生成密钥对
 * 然后使用getPublic和getPrivate方法获取公钥和私钥字符串，
 */
@Slf4j
@Component
public class EncryptRSAUtils {
    private static EncryptionProperties encryptionProperties;


    /**
     * 自动注入 EncryptionProperties (因为要使用static方法)
     *
     * @param encryptionProperties 加密配置
     */
    @Autowired
    public EncryptRSAUtils(EncryptionProperties encryptionProperties) {
        EncryptRSAUtils.encryptionProperties = encryptionProperties;
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
    public static String encryptWithPublicKey(String data, PublicKey publicKey) {
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

}
