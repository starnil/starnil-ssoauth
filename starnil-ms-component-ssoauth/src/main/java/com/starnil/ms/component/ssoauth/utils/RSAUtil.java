package com.starnil.ms.component.ssoauth.utils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * RSA机密算法功能类。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class RSAUtil {
	
	/**
	 * 生成密钥对
	 * 
	 * @param keySize 密钥位数，最小512，默认1024
	 * @return
	 * @throws Exception
	 */
	public static KeyPair generateKey(int keySize) throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(keySize);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		return keyPair;
	}

	/**
	 * 公钥加密。
	 * 
	 * @param source 源数据
	 * @param publicKey 公钥
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String source, Key publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] b = source.getBytes();
		byte[] b1 = cipher.doFinal(b);
		return Base64.encodeBase64String(b1);
	}

	/**
	 * 私钥解密。
	 * 
	 * @param cryptograph 源数据（加密后的数据）
	 * @param privateKey 私钥
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String cryptograph, Key privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] b1 = Base64.decodeBase64(cryptograph);
		byte[] b = cipher.doFinal(b1);
		return new String(b);
	}
	
	/**
	 * String类型秘钥转Key类型。
	 * 
	 * @param strKey
	 * @param isPublKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static Key stringToKey(String strKey, boolean isPublKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		Key key = null;
		KeyFactory kf = KeyFactory.getInstance("RSA");
		byte[] btKey = Base64.decodeBase64(strKey);
		if(isPublKey) {
			X509EncodedKeySpec encodedPrivateKey = new X509EncodedKeySpec(btKey);
			key = kf.generatePublic(encodedPrivateKey);
		} else {
			PKCS8EncodedKeySpec encodedPrivateKey = new PKCS8EncodedKeySpec(btKey);
			key = kf.generatePrivate(encodedPrivateKey);
		}
		return key;
	}
	
	/**
	 * 获取字符串类型秘钥，返回一个长度为2的字符串数组（第一个为公钥，第二个为私钥）
	 * 
	 * @param keySize 密钥位数，最小512，默认1024
	 * @return
	 * @throws Exception
	 */
	public static String[] generateStringKey(int keySize) throws Exception {
		String[] keys = new String[2];
		KeyPair keyPair = generateKey(keySize);
		// 私钥
		PrivateKey privateKey = keyPair.getPrivate();
		byte[] privateBT = privateKey.getEncoded();
		String pek = Base64.encodeBase64String(privateBT);
		// 公钥
		PublicKey publicKey = keyPair.getPublic();
		byte[] publicBT = publicKey.getEncoded();
		String pck = Base64.encodeBase64String(publicBT);
		keys[1] = pek;
		keys[0] = pck;
		return keys;
	}
}

