package com.starnil.ms.component.ssoauth;
/**
 * SSOUser实体。该实体对应返回给客户端ticket，用于校验ticket时返回给客户端所用。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class SSOUser {
	private String id;
	private String name;
	private String loginTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}
	
}
