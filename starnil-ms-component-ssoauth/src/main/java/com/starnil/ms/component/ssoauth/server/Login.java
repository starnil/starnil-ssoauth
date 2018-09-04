package com.starnil.ms.component.ssoauth.server;

import com.starnil.ms.component.ssoauth.server.servlet.LoginStatus;

/**
 * 登录接口类。
 * 
 * 可执行实现该接口，并注入到LoginServlet中实现具体业务逻辑。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public interface Login {
	public LoginStatus verify(String userName, String password, String imgCode);
}
