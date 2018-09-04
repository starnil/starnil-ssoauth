package com.starnil.ms.component.ssoauth.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.starnil.ms.component.ssoauth.cache.SSOCache;
import com.starnil.ms.component.ssoauth.cache.SSOCacheImpl;
import com.starnil.ms.component.ssoauth.cache.SSOCacheManger;

/**
 * 单点登录用户session监听器，用于初始化系统缓存类以及过期用户清理线程。
 * 
 * 如果需要使用自定义缓存（分布式等），这可以通过监听进行配置指定。
 * 
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class SSOListener implements ServletContextListener {
	private static Log log = LogFactory.getLog(SSOListener.class);
	
	private long intervalTime = 3;
	
	private SSOOverdueUserCleanupThred userClearThread = null;

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if(userClearThread != null) {
			userClearThread.destroy();
		}
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext sc = event.getServletContext();
        String clearIntervalTime = sc.getInitParameter("clearIntervalTime");
        if(clearIntervalTime != null && !"".equals(clearIntervalTime)) {
        	intervalTime = Long.parseLong(clearIntervalTime);
        }
		String cacheClass = sc.getInitParameter("cacheClass");
		if(cacheClass.equals("") || cacheClass == null) {
			SSOCache cache = new SSOCacheImpl();
			SSOCacheManger.setCache(cache);
		} else {
			try {
				SSOCache cache = (SSOCache) Class.forName(cacheClass).newInstance();
				SSOCacheManger.setCache(cache);
				userClearThread = new SSOOverdueUserCleanupThred(cache, intervalTime);
				(new Thread(userClearThread)).start();
			} catch (Exception e) {
				log.error("SSOListener SSOCache 实例化失败。", e);
				e.printStackTrace();
			}
		}
	}

}
