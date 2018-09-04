package com.starnil.ms.component.ssoauth.server.servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.starnil.ms.component.ssoauth.SSOUser;
import com.starnil.ms.component.ssoauth.cache.SSOCache;
import com.starnil.ms.component.ssoauth.cache.SSOCacheManger;
import com.starnil.ms.component.ssoauth.server.Constants;
import com.starnil.ms.component.ssoauth.server.Ticket;
import com.starnil.ms.component.ssoauth.server.dto.SSOLogoutDto;
import com.starnil.ms.component.ssoauth.utils.DESUtil;
import com.starnil.ms.component.ssoauth.utils.RSAUtil;

/**
 * 验证token是否是有效
 * 
 * @author starnil@139.com
 * @version 1.0
 */
public class ValidateTokenServlet extends HttpServlet {
	private static Log log = LogFactory.getLog(ValidateTokenServlet.class);
	private static final long serialVersionUID = 1L;
	private SSOCache cache;

	public void init(ServletConfig config) throws ServletException {
		cache = SSOCacheManger.getCache();
	}
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }
	
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter(Constants.SSOAUTH_TOKEN_NAME);
        String logoutPath = request.getParameter(Constants.SSOAUTH_CLIENT_LOGOUT_PATH);
        String sessionIdName = request.getParameter(Constants.SSOAUTH_CLIENT_SESSIONID_NAME);
        String sessionId = request.getParameter(Constants.SSOAUTH_CLIENT_SESSIONID);
        PrintWriter writer = response.getWriter();
        String result = "404 error.";
        if(token != null && !"".equals(token)) {
	    	try {
				String tokenId = RSAUtil.decrypt(token, RSAUtil.stringToKey(Constants.SSOAUTH_SERVER_PRIVATE_KEY, false));
		        Object user = cache.get(Constants.SSOAUTH_CACHE_TOKEN_PREFIX + tokenId);
		        if(user != null) {
		        	SSOUser ssoUser = (SSOUser) user;
		        	// 获取用户对应的ticket，将验证通过的客户端退出登录回调地址写入ticket。
		        	String TCacheKey = Constants.SSOAUTH_CACHE_TICKET_PREFIX + ssoUser.getId() + ssoUser.getLoginTime();
		            Ticket ticket = (Ticket) cache.get(TCacheKey);
		            if(ticket != null) {
		            	SSOLogoutDto logout = new SSOLogoutDto(logoutPath, sessionIdName, sessionId);
		            	String jsonLogout = JSON.toJSONString(logout);
			            List<String> logoutPaths = ticket.getLogoutPaths();
			            if(logoutPaths != null) {
			            	if(!logoutPaths.contains(jsonLogout)) {
			            		logoutPaths.add(jsonLogout);
			            	}
			            } else {
			            	logoutPaths = new ArrayList<String>();
		            		logoutPaths.add(jsonLogout);
			            	ticket.setLogoutPaths(logoutPaths);
			            }
		            	cache.put(TCacheKey, ticket, ticket.getExpiredTime() / 60); // 覆盖更新缓存（分布式缓存可用）
			        	String[] temp = tokenId.split("_");
			        	cache.remove(Constants.SSOAUTH_CACHE_TOKEN_PREFIX + tokenId); // 删除token，确保只使用一次
				        String jsonuser = JSON.toJSONString(ssoUser);
				        result = DESUtil.encrypt(jsonuser, tokenId + temp[1] + temp[0]);
		            }
		        }
			} catch (Exception e) {
				log.error("验证token失败。", e);
			}
        } 
        writer.write(result); // 返回客户端结果数据
    }

}