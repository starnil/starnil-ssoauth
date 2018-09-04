package com.starnil.ms.component.ssoauth.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.starnil.ms.component.ssoauth.server.Constants;
import com.starnil.ms.component.ssoauth.server.dto.SSOLogoutDto;
import com.starnil.ms.component.ssoauth.utils.HttpUtil;

/**
 * 
 * 单点登录用户退出登录工作线程。
 * 
 * 该线程主要负责用户登录多个业务系统时（系统A、B、C），当用户从系统A退出登录后，
 * 线程将通知其他已知的已登录系统B、C同时退出登录（根据用户登录成功是提供的系统退出登录回调地址）。
 * 
 * @author starnil@139.com
 * @version 1.0
 */
public class SSOLogoutWorker implements Runnable {
	private static Log log = LogFactory.getLog(SSOLogoutWorker.class);
	private List<String> logoutUrls;
	private String sourceAddress;
	
	public SSOLogoutWorker(List<String> logoutUrls, String sourceAddress) {
		this.logoutUrls = logoutUrls;
		this.sourceAddress = sourceAddress;
	}
	
	/**
	 * 退出登录，该方法向需要退出的系统发起一个HTTP请求。
	 * 请求中包含SESSIONID信息。否则无法实现退出。
	 * 
	 * @param logout
	 */
	private void logout(SSOLogoutDto logout) {
        try {
    		Map<String, String> cookies = new HashMap<String, String>();
    		cookies.put(logout.getSessionIdName(), logout.getSessionId());
    		Map<String, String> headers = new HashMap<String, String>();
    		headers.put("LogoutType", Constants.SSOAUTH_LOGOUT_TYPE);
    		String resp = HttpUtil.post(logout.getLogoutPath(), null, cookies, headers, Constants.SSOAUTH_HTTP_TIMEOUT);
	        System.out.println("退出回调：" + logout.getLogoutPath() + "，结果：" + resp);
		} catch (Exception e) {
			log.error("退出登录失败，请求地址：" + logout.getLogoutPath(), e);
		}
	}
	
	@Override
	public void run() {
		if(logoutUrls != null) {
			for(int i = 0; i < logoutUrls.size(); i++) {
				String tempLogout = logoutUrls.get(i);
				SSOLogoutDto logout  = JSON.parseObject(tempLogout, SSOLogoutDto.class);
				if(!logout.getLogoutPath().equals(sourceAddress)) {
					logout(logout);
				}
			}
		}
	}

}
