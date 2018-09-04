package com.starnil.ms.component.ssoauth.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.starnil.ms.component.ssoauth.cache.SSOCache;
import com.starnil.ms.component.ssoauth.cache.SSOCacheManger;
import com.starnil.ms.component.ssoauth.server.Constants;
import com.starnil.ms.component.ssoauth.server.SSOLogoutWorker;
import com.starnil.ms.component.ssoauth.server.Ticket;
import com.starnil.ms.component.ssoauth.utils.RSAUtil;

public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(LogoutServlet.class);
	private static Executor threadPool = Executors.newFixedThreadPool(4);
	private SSOCache cache;
    
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		cache = SSOCacheManger.getCache();
	}
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter(Constants.SSOAUTH_TOKEN_NAME);
        PrintWriter writer = response.getWriter();
        String result = "404 error.";
        if (null != userId && !"".equals(userId)) {
	    	try {
	    		userId = RSAUtil.decrypt(userId, RSAUtil.stringToKey(Constants.SSOAUTH_SERVER_PRIVATE_KEY, false));
	    		String TCacheKey = Constants.SSOAUTH_CACHE_TICKET_PREFIX + userId;
	            Ticket ticket = (Ticket) cache.get(TCacheKey);
	            if(ticket != null) {
		            List<String> logoutPaths = ticket.getLogoutPaths();
		        	cache.remove(TCacheKey); // 过期移除
		        	String  sourceAddress = request.getHeader("Referer");
		        	// 退出登录，通知其他业务系统同时退出登录。
		        	threadPool.execute(new SSOLogoutWorker(logoutPaths, sourceAddress));
	            }
	        	result = "200 ok.";
			} catch (Exception e) {
				log.error("验证token失败。", e);
			}
        }
        writer.write(result);
    }
}
