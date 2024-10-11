package com.example.template;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.template.util.encrypt.EncryptSHAWithSaltUtils;
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
		// 获取Druid的配置信息
//		System.out.println("Druid URL: " + dataSource.getUrl());
//		System.out.println("Druid Username: " + dataSource.getUsername());
//		System.out.println("Druid Initial Size: " + dataSource.getInitialSize());
//		System.out.println("Druid Max Active: " + dataSource.getMaxActive());
//		System.out.println("Druid Min Idle: " + dataSource.getMinIdle());
//		System.out.println("Druid Max Wait: " + dataSource.getMaxWait());
//		System.out.println("Druid Time Between Eviction Runs Millis: " + dataSource.getTimeBetweenEvictionRunsMillis());
//		System.out.println("Druid Min Evictable Idle Time Millis: " + dataSource.getMinEvictableIdleTimeMillis());

//		singleClient.getBucket("test").set("testValue");

		// 1. 生成 RSA 密钥对
		KeyPair keyPair = generateRSAKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		System.out.println("公钥: " + publicKey.toString());
		System.out.println("私钥: " + privateKey.toString());

		// 2. 输入密码
		String password = "MySecurePassword123!";
		System.out.println("原始密码: " + password);

		// 3. 对密码进行 SHA-256 哈希
		String hashedPassword = hashPassword(password);
		System.out.println("哈希后的密码 (SHA-256): " + hashedPassword);

		// 4. 使用 RSA 公钥对哈希后的密码进行加密
		String encryptedPassword = encryptWithRSA(hashedPassword, publicKey);
		System.out.println("加密后的密码 (RSA): " + encryptedPassword);

		// 5. 使用 RSA 私钥对加密后的密码进行解密
		String decryptedPassword = decryptWithRSA(encryptedPassword, privateKey);
		System.out.println("解密后的哈希密码: " + decryptedPassword);

		// 验证解密后的哈希是否与原哈希匹配
		System.out.println("哈希匹配: " + hashedPassword.equals(decryptedPassword));


		// 1. 生成随机盐
		String salt = EncryptSHAWithSaltUtils.generateSalt();
		System.out.println("Generated Salt: " + salt);

		// 2. 对密码进行哈希
		String hashedPassword1 = EncryptSHAWithSaltUtils.hashPassword(password, salt);
		System.out.println("Hashed Password: " + hashedPassword1);

		// 3. 验证密码
		boolean isPasswordValid = EncryptSHAWithSaltUtils.verifyPassword(password, salt, hashedPassword1);
		System.out.println("Password is valid: " + isPasswordValid);
	}


}
