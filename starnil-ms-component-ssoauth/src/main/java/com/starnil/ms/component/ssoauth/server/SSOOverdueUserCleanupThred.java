package com.starnil.ms.component.ssoauth.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.starnil.ms.component.ssoauth.cache.SSOCache;

/**
 * 单点登录用户session过期检测和清理线程。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class SSOOverdueUserCleanupThred implements Runnable {
	private static Log log = LogFactory.getLog(SSOOverdueUserCleanupThred.class);

	/** 存活标示 */
	private volatile boolean isExit = false;
	private long intervalTime;
	
	private SSOCache cache;
	
	public SSOOverdueUserCleanupThred(SSOCache cache, long intervalTime) {
		this.cache = cache;
		this.intervalTime = intervalTime;
	}

	/**
	 * 销毁线程。
	 * 
	 * @return
	 */
	public boolean destroy() {
		isExit = true;
		return true;
	}

    /**
     * 循环执行任务
     * 
     */
	final public void run() {
		while (!isExit) {
			try {
				Map<String, Object> users = cache.getAll();
				if(users != null) {
					Iterator<String> it = users.keySet().iterator();
					List<String> removeKeys = new ArrayList<String>();
					while(it.hasNext()) { // 找到所有过期的数据，记录过期的keys
						String key = it.next();
						if(key.indexOf(Constants.SSOAUTH_CACHE_TICKET_PREFIX) <= 0) {
							continue;
						}
						Ticket ticket = (Ticket) users.get(key);
						if(ticket != null && ticket.isExpired()) {
							// 使用remove删除数据会出错，修改了映射结构 影响了迭代器遍历
							// cache.remove(Constants.SSOAUTH_CACHE_TICKET_PREFIX + ticket.getUser().getId()); // 过期移除
							// it.remove(); // 用迭代器删除，不会出错。
							// 之所以这里不直接算出，考虑到分布式缓存下的可操作性，故只记录keys，然后单个删除。
							removeKeys.add(key);
						}
					}
					for(int i = 0; i < removeKeys.size(); i++) {
						cache.remove(removeKeys.get(i)); // 过期移除，这里就可以移除分布式缓存中数据了。
					}
				}
				Thread.sleep(intervalTime * 60 * 1000);
			} catch (Exception e) {
				log.error("SSO过期用户清理异常。", e);
			}
		}
	}
}
