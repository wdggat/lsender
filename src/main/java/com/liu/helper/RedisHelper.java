package com.liu.helper;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.*;

public class RedisHelper {
    private static Logger logger = Logger.getLogger(RedisHelper.class);
    private static final int version = 1;

    private static final int REDIS_DEFAULT_PORT = 6379;

    private static JedisPool masterPool;
    private static JedisPool slavePool;

    public static final int REDIS_SERVER_ERROR = -1;
    public static final int REDIS_KEY_NOT_EXISTS = 0;
    public static final int REDIS_KEY_EXISTS = 1;

    private static final int REDIS_MSG_KEY_EXPIRE = 600;
    private static final int MIN_UID = 10001;

    private static final String maxUidCachePrefix = "maxuid"; 
    private static final String uinfoCachePrefix = "u:";
    private static final String baiduUinfoPrefixEmail = "be:";
    private static final String baiduUinfoPrefixUid = "bu:";

    public static void init(String redisMaster, String redisSlave) {
        init(redisMaster, REDIS_DEFAULT_PORT, redisSlave, REDIS_DEFAULT_PORT);
    }

    public static void init(String redisMaster, int masterPort,
                            String redisSlave, int slavePort) {
        masterPool = new JedisPool(new JedisPoolConfig(), redisMaster, masterPort);
        slavePool = new JedisPool(new JedisPoolConfig(), redisSlave, slavePort);
    }

    public static void destroy() {
        masterPool.destroy();
        slavePool.destroy();
    }

    private static Jedis returnBrokenResource(JedisPool pool, Jedis jedis) {
        if (jedis != null) {
            pool.returnBrokenResource(jedis);
            jedis = null;
        }
        return jedis;
    }

    private static void returnResource(JedisPool pool, Jedis jedis) {
        if (jedis != null) {
            pool.returnResource(jedis);
        }
    }
    
    public static boolean setUinfoCache(String username, String uinfoJson) {
    	return set(uinfoCachePrefix + username, uinfoJson);
    }
    
    public static String getUinfoCache(String username) {
    	return get(uinfoCachePrefix + username);
    }
    
    public static int existUinfoCache(String username) {
    	return exists(uinfoCachePrefix + username);
    }
    
    public static boolean setBaiduUserCacheUname(String username, List<String> baiduUinfo) {
    	return setCache(baiduUinfoPrefixEmail, username, baiduUinfo);
    }
    
    public static List<String> getBaiduUserCacheUname(String username, int valueLength) {
    	return getCache(baiduUinfoPrefixEmail, username, valueLength);
    }
    
    public static boolean delBaiduCacheUname(String username) {
    	return delCache(baiduUinfoPrefixEmail, username);
    }
    
    public static boolean setBaiduUserCacheUid(String uid, List<String> baiduUinfo) {
    	return setCache(baiduUinfoPrefixUid, uid, baiduUinfo);
    }
    
    public static List<String> getBaiduUserCacheUid(String uid, int valueLength) {
    	return getCache(baiduUinfoPrefixUid, uid, valueLength);
    }
    
    public static boolean delBaiduCacheUid(String uid) {
    	return delCache(baiduUinfoPrefixUid, uid);
    }
    
    public static long incrUid() {
    	long nextUid = incr(maxUidCachePrefix, 1);
    	if(nextUid < MIN_UID) {
    		return incr(maxUidCachePrefix, MIN_UID - nextUid);
    	}
    	return nextUid;
    }
    
    private static long incr(String key, long increment) {
    	Jedis masterJedis = null;
        Jedis slaveJedis = null;
        try {
            masterJedis = masterPool.getResource();
            return masterJedis.incrBy(key, increment);
        } catch (JedisConnectionException je) {
            logger.error("Connect to Redis master failed, lookup from Redis slave", je);
            try {
                slaveJedis = slavePool.getResource();
                return slaveJedis.incrBy(key, increment);
            } catch (Exception e) {
                logger.error("Error occurred during hset", e);
                returnBrokenResource(slavePool, slaveJedis);
                return -1;
            }
        } catch (Exception e) {
            logger.error("Error occurred during hset", e);
            returnBrokenResource(masterPool, masterJedis);
            return -1;
        } finally {
            returnResource(masterPool, masterJedis);
            returnResource(slavePool, slaveJedis);
        }
    }
    
    private static Set<String> hkeys(String realKey) {
        Jedis masterJedis = null;
        Jedis slaveJedis = null;
        try {
            masterJedis = masterPool.getResource();
            Set<String> keys = masterJedis.hkeys(realKey);
            return keys;
        } catch (JedisConnectionException je) {
            logger.error("Connect to Redis master failed, lookup from Redis slave", je);
            try {
                slaveJedis = slavePool.getResource();
                Set<String> keys = slaveJedis.hkeys(realKey);
                return keys;
            } catch (Exception e) {
                logger.error("Error occurred during hkeys", e);
                slaveJedis = returnBrokenResource(slavePool, slaveJedis);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error occurred during hkeys", e);
            masterJedis = returnBrokenResource(masterPool, masterJedis);
            return null;
        } finally {
            returnResource(masterPool, masterJedis);
            returnResource(slavePool, slaveJedis);
        }
    }

    private static boolean set(String realKey, String value) {
        Jedis masterJedis = null;
        try {
            masterJedis = masterPool.getResource();
            masterJedis.set(realKey, value);
            return true;
        } catch (Exception e) {
            logger.error("Error occurred during hset", e);
            masterJedis = returnBrokenResource(masterPool, masterJedis);
            return false;
        } finally {
            returnResource(masterPool, masterJedis);
        }
    }

    private static String get(String realKey) {
        Jedis masterJedis = null;
        Jedis slaveJedis = null;
        try {
            masterJedis = masterPool.getResource();
            return masterJedis.get(realKey);
        } catch (JedisConnectionException je) {
            logger.error("Connect to Redis master failed, lookup from Redis slave", je);
            try {
                slaveJedis = slavePool.getResource();
                return slaveJedis.get(realKey);
            } catch (Exception e) {
                logger.error("Error occurred during hset", e);
                returnBrokenResource(slavePool, slaveJedis);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error occurred during hset", e);
            returnBrokenResource(masterPool, masterJedis);
            return null;
        } finally {
            returnResource(masterPool, masterJedis);
            returnResource(slavePool, slaveJedis);
        }
    }

    private static int exists(String realKey) {
        Jedis masterJedis = null;
        Jedis slaveJedis = null;
        try {
            masterJedis = masterPool.getResource();
            boolean exists = masterJedis.exists(realKey);
            return exists ? REDIS_KEY_EXISTS : REDIS_KEY_NOT_EXISTS;
        } catch (JedisConnectionException je) {
            logger.error("Connect to Redis master failed, lookup from Redis slave", je);
            try {
                slaveJedis = slavePool.getResource();
                boolean exists = slaveJedis.exists(realKey);
                return exists ? REDIS_KEY_EXISTS : REDIS_KEY_NOT_EXISTS;
            } catch (Exception e) {
                logger.error("Error occurred during hexists", e);
                returnBrokenResource(slavePool, slaveJedis);
                return REDIS_SERVER_ERROR;
            }
        } catch (Exception e) {
            logger.error("Error occurred during hexists", e);
            returnBrokenResource(masterPool, masterJedis);
            return REDIS_SERVER_ERROR;
        } finally {
            returnResource(masterPool, masterJedis);
            returnResource(slavePool, slaveJedis);
        }
    }

    private static boolean setCache(String cachePrefix, String key, List<String> values) {
        return setCache(cachePrefix, key, values, 0);
    }

    private static boolean setCache(String cachePrefix, String key, List<String> values, int expireSeconds) {
    	// Reset cache values
        if (!delCache(cachePrefix, key)) return false;

        String realKey = cachePrefix + key;
        Jedis masterJedis = null;
        try {
            masterJedis = masterPool.getResource();
            for (String value : values) {
                masterJedis.rpush(realKey, value);
            }
            if(expireSeconds != 0)
            	masterJedis.expire(realKey, expireSeconds);
            return true;
        } catch (Exception e) {
            logger.error("Error occurred during setCache", e);
            masterJedis = returnBrokenResource(masterPool, masterJedis);
            return false;
        } finally {
            returnResource(masterPool, masterJedis);
        }
    }

    private static boolean delCache(String cachePrefix, String key) {
        String realKey = cachePrefix + key;

        Jedis masterJedis = null;
        try {
            masterJedis = masterPool.getResource();
            masterJedis.del(realKey);
            return true;
        } catch (Exception e) {
            logger.error("Error occurred during delCache", e);
            masterJedis = returnBrokenResource(masterPool, masterJedis);
            return false;
        } finally {
            returnResource(masterPool, masterJedis);
        }
    }

    private static List<String> getCache(String cachePrefix, String key, int valueLength) {
        String realKey = cachePrefix + key;

        List<String> cacheValues = new ArrayList<String>();

        Jedis masterJedis = null;
        Jedis slaveJedis = null;
        try {
            masterJedis = masterPool.getResource();
            long length = masterJedis.llen(realKey);
            for (int i = 0; i < length; i++) {
                cacheValues.add(masterJedis.lindex(realKey, i));
            }
            return cacheValues.size() == valueLength ? cacheValues : null;
        } catch (JedisConnectionException je) {
            logger.error("Connect to Redis master failed, lookup from Redis slave", je);
            try {
                // reset cacheValues
                cacheValues.clear();
                slaveJedis = slavePool.getResource();
                long length = slaveJedis.llen(realKey);
                for (int i = 0; i < length; i++) {
                    cacheValues.add(slaveJedis.lindex(realKey, i));
                }
                return cacheValues.size() == valueLength ? cacheValues : null;
            } catch (Exception e) {
                logger.error("Error occurred during getCache", e);
                returnBrokenResource(slavePool, slaveJedis);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error occurred during getCache", e);
            returnBrokenResource(masterPool, masterJedis);
            return null;
        } finally {
            returnResource(masterPool, masterJedis);
            returnResource(slavePool, slaveJedis);
        }
    }
}
