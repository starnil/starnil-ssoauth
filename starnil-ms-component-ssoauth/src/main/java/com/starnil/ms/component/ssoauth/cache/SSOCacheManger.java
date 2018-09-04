package com.starnil.ms.component.ssoauth.cache;

public class SSOCacheManger {
	private static SSOCache cache = null;
	
	synchronized public static SSOCache getCache() {
		if(cache == null) {
			cache = new SSOCacheImpl();
		}
		return cache;
	}
	
	synchronized public static void setCache(SSOCache ssocache) {
		if(cache == null) {
			cache = ssocache;
		}
	}
}
