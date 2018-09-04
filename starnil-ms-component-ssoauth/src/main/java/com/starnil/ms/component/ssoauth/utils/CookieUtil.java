package com.starnil.ms.component.ssoauth.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 一个简单的cookie工具类。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class CookieUtil {
	
	/**
	 * 通过cookie名称移除cookie。
	 * 
	 * @param response
	 * @param cookieName
	 */
    public static void removeCookie(HttpServletResponse response, String cookieName) {
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);
    }
    
	/**
	 * 添加cookie。
	 * 
	 * @param response
	 * @param cookieName
	 * @param cookieValue
	 */
    public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
	
    /**
     * 从cookie中获取一个数据，通过cookieName进行指定。
     * 
     * @param request
     * @param cookieName
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
    	String value = "";
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                	value = cookie.getValue();
                    break;
                }
            }
        }
        return value;
    }
}
