package com.example.template;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

@SpringBootTest
class TemplateApplicationTests {

	@Autowired
	private DruidDataSource dataSource;


	@Autowired
	private RedissonClient singleClient;

	@Autowired
	private Cache<String, Object> localCache;

	// 生成 RSA 密钥对（公钥和私钥）
	public static KeyPair generateRSAKeyPair() throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(512); // 设置 RSA 密钥长度为 2048 位
		return keyGen.generateKeyPair();
	}

	// 使用 SHA-256 对密码进行哈希
	public static String hashPassword(String password) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(password.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(hash); // 将哈希后的字节数组转为 Base64 字符串
	}

	// 使用 RSA 公钥对哈希后的密码进行加密
	public static String encryptWithRSA(String hashedPassword, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedPassword = cipher.doFinal(hashedPassword.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(encryptedPassword); // 将加密后的字节数组转为 Base64 字符串
	}

	// 使用 RSA 私钥对加密后的密码进行解密
	public static String decryptWithRSA(String encryptedPassword, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedPassword = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
		return new String(decryptedPassword, "UTF-8");
	}

	@SneakyThrows
	@Test
	void contextLoads() {

	}


}
