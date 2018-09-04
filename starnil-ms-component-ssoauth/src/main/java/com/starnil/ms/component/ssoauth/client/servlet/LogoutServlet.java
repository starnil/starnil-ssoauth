package com.starnil.ms.component.ssoauth.client.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.starnil.ms.component.ssoauth.SSOUser;
import com.starnil.ms.component.ssoauth.client.Constants;
import com.starnil.ms.component.ssoauth.utils.HttpUtil;
import com.starnil.ms.component.ssoauth.utils.RSAUtil;

public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(LogoutServlet.class);
    private String redirectPath; // 退出登录后转到的页面。
    
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		redirectPath = config.getInitParameter("redirectPath");
    	if(redirectPath == null && "".equals(redirectPath)) {
    		redirectPath = "/index.jsp";
    	}
	}
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String type = request.getHeader("LogoutType");
        HttpSession session = request.getSession();
    	if(type != null && type.equals(Constants.SSOAUTH_LOGOUT_TYPE)) { // 认证服务器发起的退出登录，直接移除session即可。
	    	session.removeAttribute(Constants.SSOAUTH_USER_SESSION);
	        PrintWriter writer = response.getWriter();
	        String result = "200 ok.";
	        writer.write(result);
    	} else {
	        SSOUser user = (SSOUser) session.getAttribute(Constants.SSOAUTH_USER_SESSION);
	        if(user != null) {
		        try {
			    	session.removeAttribute(Constants.SSOAUTH_USER_SESSION);
					String userId = RSAUtil.encrypt(user.getId() + user.getLoginTime(), RSAUtil.stringToKey(Constants.SSOAUTH_CLIENT_PUBLIC_KEY, true));
	        		Map<String, String> params = new HashMap<String, String>();
	        		params.put(Constants.SSOAUTH_TOKEN_NAME, userId);
	        		Map<String, String> headers = new HashMap<String, String>();
	        		headers.put("Referer", request.getRequestURL().toString());
	        		HttpUtil.post(Constants.SSOAUTH_LOGOUT_PATH, params, null, headers, Constants.SSOAUTH_HTTP_TIMEOUT);
		        } catch (Exception e) {
		        	log.error("退出登录异常。", e);
		        }
	        }
	        response.sendRedirect(redirectPath);
        }
    }
}
