package com.starnil.ms.component.ssoauth.client.filter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.starnil.ms.component.ssoauth.RespPostRedirect;
import com.starnil.ms.component.ssoauth.SSOUser;
import com.starnil.ms.component.ssoauth.client.Constants;
import com.starnil.ms.component.ssoauth.utils.DESUtil;
import com.starnil.ms.component.ssoauth.utils.HttpUtil;
import com.starnil.ms.component.ssoauth.utils.RSAUtil;

/**
 * 单点登录客户端过滤器，用于验证用户token，如果验证失败则将重定向到登录页面进行验证。
 * 
 * 
 * Create Date: 2018-08-10 09:59:12
 * 
 * Modified By: <修改人中文名或拼音缩写> Modified Date: <修改日期，格式:YYYY-MM-DD> Why & What is
 * modified: <修改原因描述>
 * 
 * @author starnil@139.com
 * @version 1.0
 */
public class SSOClientFilter implements Filter {
	private static Log log = LogFactory.getLog(SSOClientFilter.class);
	
	private static long lastAccessTime; // 最后访问时间
	
	private static int keepliveIntervalTime = 3; // 心跳激活时间（单位分），3分钟
	
	private String loginPath = ""; // 登录页面
	private String logoutPath = ""; // 登录页面
	
	private String clientKeysPath = "clientKeys"; // 获取客户端密匙对地址
	
	private String checkTokenPath = "validateToken"; // 验证token地址
	
	private String keeplivePath = "keeplive"; // 心跳激活地址

	private String sessionIdName = "JSESSIONID"; // 心跳激活地址
	
	private String[] excludeAbsPath = null; // 排除的资源决对路径/目录
	
	private boolean disabled = false; // 无效的，如果为true则filter无效;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		// 1、验证filter有效性
		if(disabled) { // filter无效
			filterChain.doFilter(servletRequest, servletResponse);
            return;
		}
        HttpServletRequest request = (HttpServletRequest) servletRequest;
		String requestURI = request.getRequestURI();
		// 2、验证资源绝对路径
		if(excludeAbsPath != null) {
			for(int i = 0; i < excludeAbsPath.length; i++) { // 在排除的绝对资源路径中则继续向下执行
				if(requestURI.equals(excludeAbsPath[i].trim())) {
					filterChain.doFilter(request, servletResponse);
		            return;
				}
			}
		}
		
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        SSOUser user = (SSOUser) session.getAttribute(Constants.SSOAUTH_USER_SESSION);
        String reqUrl = request.getRequestURL().toString();
        String serveurl = URLEncoder.encode(reqUrl, "UTF-8");
        // 3、验证user以及token
        if (null == user) { // 未登录
            String token = request.getParameter(Constants.SSOAUTH_TOKEN_NAME);
            if (null != token && !"".equals(token)) { // 存在token，说明有服务器端或其他请求该页面时传递了token（排除非法请求，为登录服务器发起）
        		// 验证token，验证通过返回一个user对象
                try {
                    String dn = getUrlDomainName(reqUrl);
                    String[] temp = dn.split("\\.");
                	token = DESUtil.decrypt(token, dn + temp[1] + temp[0]);
					String tokenId = RSAUtil.encrypt(token, RSAUtil.stringToKey(Constants.SSOAUTH_CLIENT_PUBLIC_KEY, true));

	                // 发起验证，需要传递退出登录地址与sessionid（用于退出登录时服务器端通知客户端退出）
	        		Map<String, String> params = new HashMap<String, String>();
	        		params.put(Constants.SSOAUTH_TOKEN_NAME, tokenId);
	        		params.put(Constants.SSOAUTH_CLIENT_LOGOUT_PATH, logoutPath);
	        		params.put(Constants.SSOAUTH_CLIENT_SESSIONID_NAME, sessionIdName);
	        		params.put(Constants.SSOAUTH_CLIENT_SESSIONID, request.getSession().getId());
					// 服务器端异常或数据验证未通过返回：404 error.
	                String resp = HttpUtil.post(checkTokenPath, params, Constants.SSOAUTH_HTTP_TIMEOUT);
	                
	                if (null != resp && !"".equals(resp) && !"404 error.".equals(resp)) {
	                	temp = token.split("_");
	                	String deuserStr = DESUtil.decrypt(resp, token + temp[1] + temp[0]);
	                    user = JSON.parseObject(deuserStr, SSOUser.class);
	                    session.setAttribute(Constants.SSOAUTH_USER_SESSION, user);
	                    filterChain.doFilter(request, response);
	                    lastAccessTime = System.currentTimeMillis(); // 设置一个最后访问服务器时间
	                } else {
	    				toLogin(response, serveurl); // token验证失败，重新定位到登录页面。
	                }
				} catch (Exception e) {
					log.error("验证token失败。", e);
    				toLogin(response, serveurl); // token验证失败，重新定位到登录页面。
				}
            } else {
				toLogin(response, serveurl); // 无token，重新定位到登录页面。
            }
        } else {
    		// 当访问时间超过心跳激活时间，发起心跳检测包。
    		if(System.currentTimeMillis() - lastAccessTime > keepliveIntervalTime * 60 * 1000) {
    	        try {
    	        	String userId = RSAUtil.encrypt(user.getId() + user.getLoginTime(), RSAUtil.stringToKey(Constants.SSOAUTH_CLIENT_PUBLIC_KEY, true));
	        		Map<String, String> params = new HashMap<String, String>();
	        		params.put(Constants.SSOAUTH_TOKEN_NAME, userId);
	        		// 服务器端异常或数据验证未通过返回：404 error.
	                String resp = HttpUtil.post(keeplivePath, params, Constants.SSOAUTH_HTTP_TIMEOUT);
					lastAccessTime = System.currentTimeMillis();
	    			if("404 error.".equals(resp)) { // 已过期
	    				session.removeAttribute(Constants.SSOAUTH_USER_SESSION);
	    				toLogin(response, serveurl); // 到登录页面
	    			} else {
	    				filterChain.doFilter(request, response);
	    			}
    	        } catch (Exception e) {
    	        	log.error("数据加密异常。", e);
    				session.removeAttribute(Constants.SSOAUTH_USER_SESSION);
    				toLogin(response, serveurl); // 到登录页面
    	        }
    		} else {
                filterChain.doFilter(request, response);
    		}
        }
    }
    
    private static String getUrlDomainName(String url) {
        //切分
        String regex = "/";
        String[] strings = url.split(regex);
        //输出结果
        return strings[2];
    }
    
    private void toLogin(HttpServletResponse response, String serveurl) throws IOException {
    	RespPostRedirect respPRt = new RespPostRedirect(response);
    	respPRt.setParameter(Constants.SSOAUTH_CLIENT_SERVICE_URL_NAME, serveurl);
    	respPRt.sendRedirect(loginPath);
    }
    
    @Override
    public void init(FilterConfig fc) throws ServletException {
		String temp = fc.getInitParameter("disabled");
		disabled = (temp == null || temp.equals("")) ? false : temp.trim().toLowerCase().equals("true") ? true : false;
		temp = fc.getInitParameter("excludeAbsPath");
		excludeAbsPath = (temp == null || temp.equals("")) ? null : temp.split(",");
		temp = fc.getInitParameter("loginPath");
		loginPath = (temp == null || temp.equals("")) ? null : temp;
		if(loginPath != null) {
			checkTokenPath = loginPath.substring(0, loginPath.lastIndexOf("/") + 1) + checkTokenPath;
		}
		if(loginPath != null) {
			clientKeysPath = loginPath.substring(0, loginPath.lastIndexOf("/") + 1) + clientKeysPath;
		}
		if(loginPath != null) {
			keeplivePath = loginPath.substring(0, loginPath.lastIndexOf("/") + 1) + keeplivePath;
		}
		if(loginPath != null) {
			// 设置一个全局退出登录页面
			if(Constants.SSOAUTH_LOGOUT_PATH == null) {
				Constants.SSOAUTH_LOGOUT_PATH = loginPath.substring(0, loginPath.lastIndexOf("/") + 1) + "logout";
			}
		}
		temp = fc.getInitParameter("logoutPath");
		logoutPath = (temp == null || temp.equals("")) ? null : temp;
		temp = fc.getInitParameter("sessionIdName");
		if(temp != null && !temp.equals("")) {
			sessionIdName = temp;
		}
    }

}