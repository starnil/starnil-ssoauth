package com.starnil.ms.component.ssoauth.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地缓存类，简单的采用HashMap实现数据存储（非线程安全）
 * 
 * @author starnil@139.com
 * @version 1.0
 *
 * @param <K>
 * @param <V>
 */
public class LocalMemoryCache<K, V> {
	private Map<K, V> cache = new HashMap<K, V>();
	
	public V get(K key) {
		return cache.get(key);
	}
	
	public void clear(K key) {
		cache.remove(key);
	}
	
	public void put(K key, V obj) {
		cache.put(key, obj);
	}
	
	public Map<K, V> getCache() {
		return cache;
	}
}
