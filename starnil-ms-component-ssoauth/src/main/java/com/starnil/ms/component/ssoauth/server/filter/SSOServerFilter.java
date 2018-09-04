package com.starnil.ms.component.ssoauth.server.filter;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.starnil.ms.component.ssoauth.SSOUser;
import com.starnil.ms.component.ssoauth.cache.SSOCache;
import com.starnil.ms.component.ssoauth.cache.SSOCacheManger;
import com.starnil.ms.component.ssoauth.server.Constants;
import com.starnil.ms.component.ssoauth.server.Ticket;
import com.starnil.ms.component.ssoauth.utils.CookieUtil;
import com.starnil.ms.component.ssoauth.utils.DESUtil;

/**
 * SSO系统拦截器，用于验证客户端是否登录，如果未登录则转到登录页面。如果已经登录，则返回一个token给客户端。
 * 
 * 1、验证cookie是否存在登录用户，如果存在表示已有用户登录（进入下一步，检查ticket是否过期），反之进入登录页面。
 * 2、通过cookie中用户ID获取缓存中的user信息，同时对user进行过期检测（如果已过期重新登录，反之则生成一个新的token并返回给客户端）。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class SSOServerFilter implements Filter {
	
	private String[] excludeAbsPath = null; // 排除的资源决对路径/目录
	private SSOCache cache;
	private static Log log = LogFactory.getLog(SSOServerFilter.class);

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
		String requestURI = request.getRequestURI();
		 // 1、验证资源绝对路径
		if(excludeAbsPath != null) {
			for(int i = 0; i < excludeAbsPath.length; i++) { // 在排除的绝对资源路径中则继续向下执行
				if(requestURI.equals(excludeAbsPath[i].trim())) {
					filterChain.doFilter(request, servletResponse);
		            return;
				}
			}
		}
		
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String serveurl = request.getParameter(Constants.SSOAUTH_CLIENT_SERVICE_URL_NAME);
        if(serveurl != null && !"".equals(serveurl)) {
        	serveurl = URLDecoder.decode(serveurl, "UTF-8");
        }
        String userId = CookieUtil.getCookieValue(request, Constants.SSOAUTH_COOKIE_NAME);
        String time = CookieUtil.getCookieValue(request, Constants.SSOAUTH_COOKIE_TIME);
        if (null != userId && !"".equals(userId)) {
            Ticket ticket = (Ticket) cache.get(Constants.SSOAUTH_CACHE_TICKET_PREFIX + userId + time);
            if(ticket != null && !ticket.isExpired()) {
                String token = userId + "_" + System.currentTimeMillis(); // 返回给客户端的token，生成规则：用户ID+下划线"_"+时间
                SSOUser user = ticket.getUser();
                String dn = getUrlDomainName(serveurl);
                String[] temp = dn.split("\\.");
                String enToken = null;
    			try {
	                enToken = DESUtil.encrypt(token, dn + temp[1] + temp[0]);
	                cache.put(Constants.SSOAUTH_CACHE_TOKEN_PREFIX + token, user);
	                String clientUrl = getClientUrl(serveurl, enToken);
	                response.sendRedirect(clientUrl);
    			} catch (Exception e) {
    				log.error("返回客户端token失败，加密异常。", e);
    			}
            } else {
            	if(ticket != null && ticket.isExpired()) cache.remove(Constants.SSOAUTH_CACHE_TICKET_PREFIX + userId + time); // 过期移除
				CookieUtil.removeCookie(response, Constants.SSOAUTH_COOKIE_NAME);
				CookieUtil.removeCookie(response, Constants.SSOAUTH_COOKIE_TIME);
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
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
    
    @Override
	public void init(FilterConfig fc) throws ServletException {
		String temp = fc.getInitParameter("excludeAbsPath");
		excludeAbsPath = (temp == null || temp.equals("")) ? null : temp.split(",");
		cache = SSOCacheManger.getCache();
	}

}