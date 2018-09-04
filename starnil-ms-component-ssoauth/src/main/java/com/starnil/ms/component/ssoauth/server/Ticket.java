package com.starnil.ms.component.ssoauth.server;
import java.util.List;

import com.starnil.ms.component.ssoauth.SSOUser;

/**
 * 用户ticket实体，包含用户ID、用户名、以及校验时间和密匙对信息。
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 */
public class Ticket {
	private long creationTime; // 创建时间
	private long lastAccessTime; // 最后访问时间
	private long expiredTime; // 过期时间，单位秒
	private SSOUser user;
	private List<String> logoutPaths;

	public Ticket() {
		creationTime = System.currentTimeMillis();
		lastAccessTime = creationTime;
		expiredTime = 15 * 60;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	synchronized public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public long getExpiredTime() {
		return expiredTime;
	}

	synchronized public void setExpiredTime(long expiredTime) {
		this.expiredTime = expiredTime;
	}

	public SSOUser getUser() {
		return user;
	}

	public void setUser(SSOUser user) {
		this.user = user;
	}

	public List<String> getLogoutPaths() {
		return logoutPaths;
	}

	public void setLogoutPaths(List<String> logoutPaths) {
		this.logoutPaths = logoutPaths;
	}

	/**
	 * 是否过期。当前时间减最有一次使用时间如果大于过期时间则表示该票据已过期。
	 * 
	 * @return
	 */
	synchronized public final boolean isExpired() {
		return System.currentTimeMillis() - lastAccessTime > expiredTime * 1000 ? true
				: false;
	}

}
