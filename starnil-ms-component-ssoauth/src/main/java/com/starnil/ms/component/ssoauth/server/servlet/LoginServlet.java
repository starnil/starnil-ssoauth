package com.starnil.ms.component.ssoauth.server.servlet;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.starnil.ms.component.ssoauth.RespPostRedirect;
import com.starnil.ms.component.ssoauth.SSOUser;
import com.starnil.ms.component.ssoauth.cache.SSOCache;
import com.starnil.ms.component.ssoauth.cache.SSOCacheManger;
import com.starnil.ms.component.ssoauth.server.Constants;
import com.starnil.ms.component.ssoauth.server.Login;
import com.starnil.ms.component.ssoauth.server.Ticket;
import com.starnil.ms.component.ssoauth.utils.CookieUtil;
import com.starnil.ms.component.ssoauth.utils.DESUtil;

/**
 * 用户登录。
 * 登录成功创建cookie以及用户缓存ticket数据，同时返回给客户端一张唯一token。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(LoginServlet.class);

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    
    private static String getUrlDomainName(String url) {
        //切分
        String regex = "/";
        String[] strings = url.split(regex);
        //输出结果
        return strings[2];
    }
    
    /**
     * 获取一个客户端服务地址，用于重定向至客户端。
     * 
     * @param serveurl 客户端服务地址
     * @param token 用户票据
     * @return
     */
    private String getClientUrl(String serveurl, String token) {
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

}