package com.starnil.ms.component.ssoauth.server.servlet;

import com.starnil.ms.component.ssoauth.SSOUser;

/**
 * 用户登录状态信息/登录信息的一个封装。
 * 
 * @author starnil@139.com
 * @version 1.0
 * 
 */
public class LoginStatus {

	/**
	 * 登录状态，用于返回客户端作为登录依据
	 * 状态代码说明：
	 * 0 异常退出
	 * 1 验证码错误
	 * 2 用户不存在
	 * 3 登录IP受限
	 * 4 用户已被锁定
	 * 44 用户已到期被锁定
	 * 444 登录失败，请检查输入是否正确
	 * 443 验证失败，请检查输入是否正确
	 * 4444 登录失败
	 * 5 用户名、密码不一致
	 * 6 需短信验证
	 * 7 短信验证码失效
	 * 8 短信验证码错误
	 * 9 登录时间受限
	 * 10 用户未生效
	 * 1000 登录成功
	 * -1000 退出登录
	 * -2000 重复登录
	 */
	private int status;
	
	/** 登录状态说明 */
	private String text;
	
	private SSOUser user; // 用户信息，json格式字符串
	
	public LoginStatus() {
		
	}
	
	public LoginStatus(int loginStatus) {
		setState(loginStatus);
	}
	
	public void setState(int status) {
		this.status = status;
		switch (status) {
			case 0:
				text = "异常退出";
			break;
			case 1:
				text = "验证码错误";
			break;
			case 2:
				text = "用户不存在";
			break;
			case 3:
				text = "登录IP受限";
			break;
			case 4:
				text = "用户已被锁定";
			break;
			case 44:
				text = "用户已到期被锁定";
			break;
			case 444:
				text = "登录失败，请检查输入是否正确";
			break;
			case 443:
				text = "验证失败，请检查输入是否正确";
			break;
			case 4444:
				text = "登录失败";
			break;
			case 5:
				text = "用户名、密码不一致";
			break;
			case 6:
				text = "需短信验证";
			break;
			case 7:
				text = "短信验证码失效";
			break;
			case 8:
				text = "短信验证码错误";
			break;
			case 9:
				text = "登录时间受限";
			break;
			case 10:
				text = "用户未生效";
			break;
			case 1000:
				text = "登录成功";
			break;
			case -1000:
				text = "退出登录";
			break;
			case -2000:
				text = "重复登录";
			break;
			default :
			break;
		}
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public SSOUser getUser() {
		return user;
	}

	public void setUser(SSOUser user) {
		this.user = user;
	}
}
