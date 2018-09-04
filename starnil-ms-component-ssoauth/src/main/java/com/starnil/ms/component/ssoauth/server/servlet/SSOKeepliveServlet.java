package com.starnil.ms.component.ssoauth.server.servlet;
import java.io.IOException;
import java.io.PrintWriter;

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
import com.starnil.ms.component.ssoauth.server.Ticket;
import com.starnil.ms.component.ssoauth.utils.RSAUtil;

/**
 * 用户心跳激活。
 * 
 * @author starnil@139.com
 * @version 1.0
 */
public class SSOKeepliveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(SSOKeepliveServlet.class);
	private SSOCache cache;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    
    @Override
	public void init(ServletConfig config) throws ServletException {
		cache = SSOCacheManger.getCache();
	}

}