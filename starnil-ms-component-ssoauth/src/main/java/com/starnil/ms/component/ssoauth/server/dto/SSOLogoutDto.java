package com.starnil.ms.component.ssoauth.server.dto;

/**
 * SSO 退出登录数据传输对象。
 * 对象存储退出登录地址、SESSIONID名称以及SESSIONID。
 * 
 * @author starnil@139.com
 * @version 1.0
 */
public class SSOLogoutDto {

	private String logoutPath;
	private String sessionIdName;
	private String sessionId;
	
	public SSOLogoutDto(String logoutPath, String sessionIdName, String sessionId) {
		this.logoutPath = logoutPath;
		this.sessionIdName = sessionIdName;
		this.sessionId = sessionId;
	}
	
	public String getLogoutPath() {
		return logoutPath;
	}
	public void setLogoutPath(String logoutPath) {
		this.logoutPath = logoutPath;
	}
	public String getSessionIdName() {
		return sessionIdName;
	}
	public void setSessionIdName(String sessionIdName) {
		this.sessionIdName = sessionIdName;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
}
