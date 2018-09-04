package com.starnil.ms.component.ssoauth.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES加密介绍
 * DES是一种对称加密算法，所谓对称加密算法即：加密和解密使用相同密钥的算法。DES加密算法出自IBM的研究，
 * 后来被美国政府正式采用，之后开始广泛流传，但是近些年使用越来越少，因为DES使用56位密钥，以现代计算能力，
 * 24小时内即可被破解。虽然如此，在某些简单应用中，我们还是可以使用DES加密算法，本文简单讲解DES的JAVA实现。
 * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数。
 * 
 */
@SuppressWarnings({"unused"})
public class DESUtil {
    /** 
     * 加密 
     * @param data 数据源 
     * @param 密钥，长度任意 
     * @return 返回加密后的数据 
     */  
    public static String encrypt(String data, String password) throws Exception {  
            return byte2hex(encrypt(data.getBytes(), password));  
    }  
    
    /** 
     * 解密 
     * @param data 
     * @param 密钥，长度任意 
     * @return 返回加密后的数据 
     */  
    public static String decrypt(String data, String password) throws Exception {  
            return new String(decrypt(hex2byte(data), password));  
    }  
    
	/**
	 * 加密
	 * 
	 * @param datasource
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 */
	public static byte[] encrypt(byte[] datasource, String password) throws Exception {
			SecureRandom random = new SecureRandom();
			DESKeySpec desKey = new DESKeySpec(password.getBytes());
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey securekey = keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher = Cipher.getInstance("DES");
			// 用密匙初始化Cipher对象
			cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
	}

	/**
	 * 解密
	 * 
	 * @param src
	 *            byte[]
	 * @param password
	 *            String
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, String password) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作
		return cipher.doFinal(src);
	}
	
    /** 
     * 将二进制转化为16进制字符串 
     *  
     * @param b 
     *            二进制字节数组 
     * @return String 
     */  
    private static String byte2hex(byte[] b) {  
        String hs = "";  
        String stmp = "";  
        for (int n = 0; n < b.length; n++) {  
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));  
            if (stmp.length() == 1) {  
                hs = hs + "0" + stmp;  
            } else {  
                hs = hs + stmp;  
            }  
        }  
        return hs.toUpperCase();  
    }  
  
    /** 
     * 十六进制字符串转化为2进制
     *  
     * @param hex 
     * @return 
     */  
    private static byte[] hex2byte(String hex) {  
        byte[] tmp = hex.getBytes();  
        byte[] ret = new byte[tmp.length / 2];  
        for (int i = 0; i < ret.length; i++) {  
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);  
        }  
        return ret;  
    }  
      
    /**   
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF   
	 *    
	 * @param src0   
	 *            byte   
	 * @param src1   
	 *            byte   
	 * @return byte   
	 */
	private static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	} 
	
	private static String byteToHexString(byte[] bytes) {  
        StringBuffer sb = new StringBuffer(bytes.length);  
        String sTemp;  
        for (int i = 0; i < bytes.length; i++) {  
            sTemp = Integer.toHexString(0xFF & bytes[i]);  
            if (sTemp.length() < 2)  
                sb.append(0);  
            sb.append(sTemp.toUpperCase());  
        }  
        return sb.toString();  
    } 
}
