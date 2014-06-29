package com.liu.helper;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.*;

public class RedisHelper {
    private static Logger logger = Logger.getLogger(RedisHelper.class);
    private static final int version = 3;

    private static final int REDIS_DEFAULT_PORT = 6379;

    private static JedisPool masterPool;
    private static JedisPool slavePool;

    public static final int REDIS_CONNECTION_FAILED = -1;
    public static final int REDIS_KEY_NOT_EXISTS = 0;
    public static final int REDIS_KEY_EXISTS = 1;

    private static final int REDIS_MSG_KEY_EXPIRE = 600;

    private static final String uinfoCachePrefix = "u:";
    private static final String baiduUinfoPrefix = "bd:u:";

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
    
    public static boolean setBaiduUserCache(String username, List<String> baiduUinfo) {
    	return setCache(baiduUinfoPrefix, username, baiduUinfo);
    }
    
    public static List<String> getBaiduUserCache(String username, int valueLength) {
    	return getCache(baiduUinfoPrefix, username, valueLength);
    }
    
    public static boolean delBaiduCache(String username) {
    	return delCache(baiduUinfoPrefix, username);
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
                return REDIS_CONNECTION_FAILED;
            }
        } catch (Exception e) {
            logger.error("Error occurred during hexists", e);
            returnBrokenResource(masterPool, masterJedis);
            return REDIS_CONNECTION_FAILED;
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
