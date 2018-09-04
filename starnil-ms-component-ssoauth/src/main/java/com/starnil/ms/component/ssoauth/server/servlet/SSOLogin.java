package com.starnil.ms.component.ssoauth.server.servlet;

import com.starnil.ms.component.ssoauth.SSOUser;
import com.starnil.ms.component.ssoauth.server.Login;
import com.starnil.ms.component.ssoauth.utils.GUIDUtil;

/**
 * 默认登录类。不包含数据库操作，该类只做示例。
 * 
 * 如需具体的登录验证等，请自行设计业务逻辑。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class SSOLogin implements Login {

	@Override
	public LoginStatus verify(String userName, String password, String imgCode) {
		LoginStatus ls = new LoginStatus();
		if("admin".equals(userName) && "admin".equals(password)) {
            String userId = GUIDUtil.uuid();
            SSOUser user = new SSOUser(); // 创建一个 user对象。
            user.setId(userId); // userId请确保唯一，否则SSO在创建用户缓存数据时因为id相同会导致数据冲突，引起不可预见性错误。
            user.setName(userName); // 必须，用户名将返回给客户端。
			ls.setState(1000); // 登录成功设置1000（必须）
			// 登录成功，必须给LoginStatus设置一个正确的SSOUser对象（包含用户ID和用户名），用于创建SSO系统的用户信息用。
			ls.setUser(user);
		} else {
			ls.setState(444);
		}
		return ls;
	}

}
