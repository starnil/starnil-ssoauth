package com.starnil.ms.component.ssoauth.cache;

import java.util.Map;

import com.starnil.ms.component.ssoauth.server.LocalMemoryCache;

public class SSOCacheImpl implements SSOCache {

	/** 这里你可以换成分布式缓存redis、memcached等 */
    private static LocalMemoryCache<String, Object> CACHE = new LocalMemoryCache<String, Object>();
    
	@Override
	public Object get(String key) {
		return CACHE.get(key);
	}

    /**
     * 设置Ticket缓存，这里可采用redis、memcached等。
     * 
     * @param key 缓存key
     * @param value 缓存值
     * @param seconds 过期时间（分）
     */
	@Override
	public void put(String key, Object value, long seconds) {
		CACHE.put(key, value);
	}

	@Override
	public void put(String key, Object value) {
		CACHE.put(key, value);
	}

	@Override
	public void remove(String key) {
		CACHE.clear(key);
	}

	/**
	 * 该方法由SSOListener中使用，获取所有Ticket，并清理过期Ticket。
	 * 如果缓存服务器采用redis, 这里可以通过jedis.keys(Constants.SSOAUTH_CACHE_TICKET_PREFIX + "*")获取keys列表，
	 * 通过jedis获取所有Ticket，用于监听清除过期Ticket。
	 * 
	 * 如果采用redis等缓存服务器时，在保存和更新缓存的时候已经给缓存设置了过期时间，这里就直接返回null即可，无需通过监听线程进行再次清理。
	 * 
	 */
	@Override
	public Map<String, Object> getAll() {
		return CACHE.getCache();
	}

}
