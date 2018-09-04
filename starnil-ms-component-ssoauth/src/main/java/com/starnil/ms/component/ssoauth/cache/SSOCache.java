package com.starnil.ms.component.ssoauth.cache;

import java.util.Map;

public interface SSOCache {
    
    public Object get(String key);
    
    /**
     * 写入缓存
     * 
     * @param key
     * @param value
     * @param seconds 过期时间（秒）
     */
    public void put(String key, Object value, long seconds);
    
    public void put(String key, Object value);
    
    public void remove(String key);
    
    public Map<String, Object> getAll();
}
