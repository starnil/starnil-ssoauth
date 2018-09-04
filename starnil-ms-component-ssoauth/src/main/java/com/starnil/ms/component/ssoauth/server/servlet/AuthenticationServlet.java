package com.starnil.ms.component.ssoauth.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
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

import com.alibaba.fastjson.JSON;
import com.starnil.ms.component.ssoauth.RespPostRedirect;
import com.starnil.ms.component.ssoauth.SSOUser;
import com.starnil.ms.component.ssoauth.cache.SSOCache;
import com.starnil.ms.component.ssoauth.cache.SSOCacheManger;
import com.starnil.ms.component.ssoauth.server.Constants;
import com.starnil.ms.component.ssoauth.server.Login;
import com.starnil.ms.component.ssoauth.server.SSOLogoutWorker;
import com.starnil.ms.component.ssoauth.server.Ticket;
import com.starnil.ms.component.ssoauth.server.dto.SSOLogoutDto;
import com.starnil.ms.component.ssoauth.utils.CookieUtil;
import com.starnil.ms.component.ssoauth.utils.DESUtil;
import com.starnil.ms.component.ssoauth.utils.RSAUtil;

/**
 * SSO认证servlet，该方法包含登录、退出、token认证、心跳激活等方法。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class AuthenticationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(AuthenticationServlet.class);
	private static Executor threadPool = Executors.newFixedThreadPool(4);

	private long expiredTime = 15; // 分钟，不活动状态下过期时间
    private String loginPath;
    private Login login;
	private SSOCache cache;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
        String userExpiredTime = config.getInitParameter("userExpiredTime");
        if(userExpiredTime != null && !"".equals(userExpiredTime)) {
        	expiredTime = Long.parseLong(userExpiredTime);
        }
    	loginPath = config.getInitParameter("loginPath");
    	if(loginPath == null && "".equals(loginPath)) {
    		loginPath = "/ssoauth/login.jsp";
    	}
		String loginClass = config.getInitParameter("loginClass");
		if(loginClass.equals("") || loginClass == null) {
			login = new SSOLogin();
		}
		cache = SSOCacheManger.getCache();
		try {
			login = (SSOLogin) Class.forName(loginClass).newInstance();
		} catch (Exception e) {
			log.error("初始化LoginServlet失败，实例化 " + loginClass + " 失败。", e);
			return ;
		}
	}
	
    @Override
    final protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    final protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// 获取请求URI地址
        String url = request.getRequestURI();
        // URI地址中获取方法名称
        int index = url.lastIndexOf(".");
        String methodName = url.substring(url.lastIndexOf("/") + 1, index <= 0 ? url.length() : index);
        Method method = null;
        try {
            // 通过反射，根据请求方法名获取当前类中声明的方法
            method = getClass().getDeclaredMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            // 执行方法
            method.invoke(this, request, response);
        } catch (Exception e) {
            log.error("方法调用异常。", e);
        }
    }
    
    /**
     * 接口方式登录。适用第三发系统调用。
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void intfLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    }

    /**
     * ajax方式登录。适用当前系统调用（ajax）。
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void ajaxLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    }

    /**
     * jsonp方式登录。适用第三发系统方式调用（ajax）。
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void jsonpLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    }
    
    /**
     * 登录。适用第三方系统登录拦截后重定向到认证系统登录页面进行的登录动作。
     * 
     * 当认证通过后，认证系统将请求重定向到第三方系统。
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        String serveurl = request.getParameter(Constants.SSOAUTH_CLIENT_SERVICE_URL_NAME);
        if(serveurl != null && !"".equals(serveurl)) {
        	serveurl = URLDecoder.decode(serveurl, "UTF-8");
        } else { // 无service 回到登录页面。
        	response.sendRedirect(loginPath);
        }
        LoginStatus ls = login.verify(userName, password, "");
        if (ls.getStatus() == 1000) {
            SSOUser user = ls.getUser();
            String userId = user.getId();
            user.setLoginTime(System.currentTimeMillis() + "");
            CookieUtil.addCookie(response, Constants.SSOAUTH_COOKIE_NAME, userId);
            CookieUtil.addCookie(response, Constants.SSOAUTH_COOKIE_TIME, user.getLoginTime());
            // 创建一个用户票据（该票据存储服务器端缓存中，非返回给客户端票据）
            Ticket ticket = new Ticket();
            ticket.setExpiredTime(expiredTime * 60); // 设置过期
            ticket.setUser(user);
            cache.put(Constants.SSOAUTH_CACHE_TICKET_PREFIX + userId + user.getLoginTime(), ticket, expiredTime);
            // 生成认证凭据token，返回给客户端的token，生成规则：用户ID+下划线"_"+时间
            String token = userId + "_" + System.currentTimeMillis();
            String dn = getUrlDomainName(serveurl);
            String[] temp = dn.split("\\.");
            String enToken = null;
			try {
				enToken = DESUtil.encrypt(token, dn + temp[1] + temp[0]);
	            cache.put(Constants.SSOAUTH_CACHE_TOKEN_PREFIX + token, user);
	            String clientUrl = getClientUrl(serveurl, enToken);
	            response.sendRedirect(clientUrl);
			} catch (Exception e) {
				log.error("登录失败，token加密异常。", e);
			}
        } else {
        	RespPostRedirect respPRt = new RespPostRedirect(response);
        	respPRt.setParameter(Constants.SSOAUTH_CLIENT_SERVICE_URL_NAME, serveurl);
        	respPRt.sendRedirect(loginPath);
        }
    }
    
    /**
     * 退出登录。退出登录时，需要通知子业务系统（已登录状态）同时退出。
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    
    /**
     * 用户心跳激活。
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void keeplive(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = request.getParameter(Constants.SSOAUTH_TOKEN_NAME);
        PrintWriter writer = response.getWriter();
        String result = "404 error.";
        if (null != userId && !"".equals(userId)) {
        	try {
        		userId = RSAUtil.decrypt(userId, RSAUtil.stringToKey(Constants.SSOAUTH_SERVER_PRIVATE_KEY, false));
                Ticket ticket = (Ticket) cache.get(Constants.SSOAUTH_CACHE_TICKET_PREFIX + userId);
                if(ticket != null && !ticket.isExpired()) {
                	ticket.setLastAccessTime(System.currentTimeMillis());
                	cache.put(Constants.SSOAUTH_CACHE_TICKET_PREFIX + userId, ticket, ticket.getExpiredTime() / 60); // 覆盖一下（分布式缓存可用）
                	result = "200 ok.";
                } else {
                	if(ticket != null && ticket.isExpired()) cache.remove(Constants.SSOAUTH_CACHE_TICKET_PREFIX + userId); // 过期移除
                }
    		} catch (Exception e) {
    			log.error("解密异常。", e);
    		}
        }
        writer.write(result);
    }
    
    /**
     * 验证token是否是有效。
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void validateToken(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    
    /**
     * 获取一个客户端服务地址，用于重定向至客户端。
     * 
     * @param serveurl 客户端服务地址
     * @param token 用户票据
     * @return
     */
    protected String getClientUrl(String serveurl, String token) {
        StringBuilder clientUrl = new StringBuilder();
        clientUrl.append(serveurl);
        if (serveurl.indexOf("?") >= 0) {
        	clientUrl.append("&");
        } else {
        	clientUrl.append("?");
        }
        // 返回认证的凭据token给客户端
        clientUrl.append(Constants.SSOAUTH_TOKEN_NAME + "=").append(token);
        return clientUrl.toString();
    }
    
    /**
     * 获取url域名部分。
     * 
     * @param url
     * @return
     */
    final protected static String getUrlDomainName(String url) {
        String regex = "/";
        String[] strings = url.split(regex);
        return strings[2];
    }

}
