package com.starnil.ms.component.ssoauth.utils;

import java.util.UUID;

/**
 * 一个UUID生成工具。
 * 
 * 注：GUID 是微软对散列码的称呼。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class GUIDUtil {
	
	/**
	 * 生成UUID并返回，通过@UUID 完成。
	 * 
	 * 返回字符串中字母为小写。
	 * 
	 * @return
	 */
	public static String uuid() {
		String s = UUID.randomUUID().toString(); 
        //去掉“-”符号 
        return s.replaceAll("-", ""); 
	}

	/**
	 * 生成UUID并返回，通过@UUID 完成。
	 * 
	 * 返回字符串中字母为大写。
	 * 
	 * @return
	 */
	public static String UUID() {
        return uuid().toUpperCase(); 
	}
}
