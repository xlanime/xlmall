package com.xlmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenCache {

    public static final String TOKEN_PREFIX = "token_";

    private static LoadingCache<String,String> localCache =
            CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000)
                .expireAfterAccess(12, TimeUnit.HOURS)
                    .build(new CacheLoader<String, String>() {
                        //默认的数据加载实现。
                        // 如果getKey的时候没有对应的key，则调用这个加载。
                        @Override
                        public String load(String key) throws Exception {
                            return "null";
                        }
                    });

    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        try{
            String value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            log.error("local cache get error",e);
        }
        return null;
    }
}
