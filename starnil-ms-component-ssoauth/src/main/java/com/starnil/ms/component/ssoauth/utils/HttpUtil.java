package com.starnil.ms.component.ssoauth.utils;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

/**
 * 一个简单的http工具类。
 * 该类基于HttpClient3.1，其中封装http post方法。
 * 
 * @author starnil
 *
 */
public class HttpUtil {
	/**
	 * http post请求方法。可设置请求路径、参数、cookies、Header以及超时时间。
	 * 
	 * @param url 请求路径
	 * @param params 参数
	 * @param cookies cookies
	 * @param headers Headers
	 * @param timeout 超时时间
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static String post(String url, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers, int timeout) throws HttpException, IOException {
    	PostMethod postMethod = new PostMethod(url);
		try {
			String resp = null;
	    	postMethod.setRequestHeader("Connection", "close");
			if(cookies != null) {
				StringBuffer tempcookies = new StringBuffer();
				for(String key : cookies.keySet()) {
	    			String value = cookies.get(key);
	    			tempcookies.append(key + "=" + value + ";");
	    		}
	    		postMethod.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
	    		postMethod.setRequestHeader("Cookie", tempcookies.toString());
			}
	    	if(headers != null) {
	    		for(String key : headers.keySet()) {
	    			String value = headers.get(key);
	    	        postMethod.addRequestHeader(key, value);
	    		}
	    	}
	    	if(params != null) {
	    		for(String key : params.keySet()) {
	    			String value = params.get(key);
	    	        postMethod.addParameter(key, value);
	    		}
	    	}
	        HttpClient httpClient = new HttpClient();
	        HttpConnectionManagerParams hcmp = httpClient.getHttpConnectionManager().getParams();
	        // 设置连接超时时间(单位毫秒)
	        hcmp.setConnectionTimeout(timeout);
	        // 设置读数据超时时间(单位毫秒)
	        hcmp.setSoTimeout(timeout);
	        httpClient.executeMethod(postMethod);
	        resp = postMethod.getResponseBodyAsString();
			return resp;
		} finally {
	        postMethod.releaseConnection();
		}
	}

	public static String post(String url, Map<String, String> params, Map<String, String> cookies, Map<String, String> headers) throws HttpException, IOException {
		return post(url, params, cookies, headers, 10000);
	}
	
	public static String post(String url, Map<String, String> params, Map<String, String> cookies) throws HttpException, IOException {
		return post(url, params, cookies, null, 10000);
	}
	
	public static String post(String url, Map<String, String> params) throws HttpException, IOException {
		return post(url, params, null, null, 10000);
	}
	
	public static String post(String url, Map<String, String> params, int timeout) throws HttpException, IOException {
		return post(url, params, null, null, timeout);
	}
	
	public static String post(String url) throws HttpException, IOException {
		return post(url, null, null, null, 10000);
	}
}
