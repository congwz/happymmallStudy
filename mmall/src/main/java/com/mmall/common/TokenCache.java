package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/6/12 0012.
 */
public class TokenCache {

    //声明日志
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX = "token_";

    //声明静态的内存块
    //LoadingCache是guva本地缓存
    //CacheBuilder调用链模式，超过maximumSize，则guva遵循LRU算法(最少使用算法)来移除缓存项
    private  static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS)
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get获取值的时候，如果key没有对应的值，就调用这个方法进行加载。
                @Override
                public String load(String s) throws Exception {
                    return "null" ; // key.equal(null.equal就会报空指针异常)，所以改成”null“。forgetToken.equal(key);
                }
            });
    public static void setKey(String key, String value){
        localCache.put(key,value);
    }
    public  static String getKey(String key){
        String value = null;
        try{
            value = localCache.get(key);
            if("null".equals(value)){
                return null;
            }
            return value;
        }catch(Exception e){
            logger.error("localCache get error", e);

        }
        return null;
    }
}
