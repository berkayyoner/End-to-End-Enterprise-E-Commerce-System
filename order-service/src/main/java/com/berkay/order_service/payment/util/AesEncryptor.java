package com.berkay.order_service.payment.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AesEncryptor {

	private final String encryptionKey;
	private static final String ALGORITHM = "AES";

	public AesEncryptor(@Value("${berkay.encryption.key:0123456789abcdef0123456789abcdef}") String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public String encrypt(String plainText) {
		try {
			byte[] decodedKey = hexToBytes(encryptionKey);
			SecretKeySpec keySpec = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] encrypted = cipher.doFinal(plainText.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			throw new RuntimeException("Error encrypting card data", e);
		}
	}

	public String decrypt(String encryptedText) {
		try {
			byte[] decodedKey = hexToBytes(encryptionKey);
			SecretKeySpec keySpec = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
			byte[] decrypted = cipher.doFinal(decodedBytes);
			return new String(decrypted);
		} catch (Exception e) {
			throw new RuntimeException("Error decrypting card data", e);
		}
	}

	private static byte[] hexToBytes(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}
}
