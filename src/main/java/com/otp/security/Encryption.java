package com.otp.security;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.otp.props.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Encryption {

	@Autowired
	private Properties properties;
	
	/**
	 * 
	 * @return
	 */
	private IvParameterSpec generateIvSpec() {
		byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		return new IvParameterSpec(iv);
	}
	
	/**
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private SecretKeySpec generateSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(properties.getSecretKey().toCharArray(), properties.getSalt().getBytes(),
				65536, 256);
		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
		return secretKeySpec;
	}
	
	
	
	/**
	 * 
	 * @param encryptString
	 * @return
	 */
	public String encrypt(String encryptString) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey(), generateIvSpec());
			return Base64.getEncoder().encodeToString(cipher.doFinal(encryptString.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
			return null;
		}
	}
	
	/**
	 * 
	 * @param decryptString
	 * @return
	 */
	public String decrypt(String decryptString) {
		try {	
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, generateSecretKey(), generateIvSpec());
			return new String(cipher.doFinal(Base64.getDecoder().decode(decryptString)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
			return null;
		}
	}
	
}
