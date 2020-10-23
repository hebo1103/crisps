package net.crisps.cloud.framework.cache.core.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Lock {

    private static Logger logger = LoggerFactory.getLogger(Lock.class);

    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 调用set后的返回值
     */
    private static final String OK = "OK";

    /**
     * 默认请求锁的超时时间(ms 毫秒)
     */
    private static final long TIME_OUT = 100;

    /**
     * 默认锁的有效时间(s)
     */
    private static final int EXPIRE = 60;

    /**
     * 解锁的lua脚本
     */
    private static final String UNLOCK_LUA;

    static {
        UNLOCK_LUA = "if redis.call(\"get\",KEYS[1]) == ARGV[1] "
                + "then "
                + "    return redis.call(\"del\",KEYS[1]) "
                + "else "
                + "    return 0 "
                + "end ";
    }

    /**
     * 锁标志对应的key
     */
    private String lockKey;

    /**
     * 记录到日志的锁标志对应的key
     */
    private String lockKeyLog = "";

    /**
     * 锁对应的值
     */
    private String lockValue;

    /**
     * 锁的有效时间(s)
     */
    private int expireTime = EXPIRE;

    /**
     * 请求锁的超时时间(ms)
     */
    private long timeOut = TIME_OUT;

    /**
     * 锁标记
     */
    private volatile boolean locked = false;

    private final Random random = new Random();

    /**
     * 使用默认的锁过期时间和请求锁的超时时间
     *
     * @param redisTemplate redis客户端
     * @param lockKey       锁的key（Redis的Key）
     */
    public Lock(RedisTemplate<String, Object> redisTemplate, String lockKey) {
        this.redisTemplate = redisTemplate;
        this.lockKey = lockKey + "_lock";
    }

    /**
     * 使用默认的请求锁的超时时间，指定锁的过期时间
     *
     * @param redisTemplate redis客户端
     * @param lockKey       锁的key（Redis的Key）
     * @param expireTime    锁的过期时间(单位：秒)
     */
    public Lock(RedisTemplate<String, Object> redisTemplate, String lockKey, int expireTime) {
        this(redisTemplate, lockKey);
        this.expireTime = expireTime;
    }

    /**
     * 使用默认的锁的过期时间，指定请求锁的超时时间
     *
     * @param redisTemplate redis客户端
     * @param lockKey       锁的key（Redis的Key）
     * @param timeOut       请求锁的超时时间(单位：毫秒)
     */
    public Lock(RedisTemplate<String, Object> redisTemplate, String lockKey, long timeOut) {
        this(redisTemplate, lockKey);
        this.timeOut = timeOut;
    }

    public Lock(RedisTemplate<String, Object> redisTemplate, String lockKey, int expireTime, long timeOut) {
        this(redisTemplate, lockKey, expireTime);
        this.timeOut = timeOut;
    }

    public boolean tryLock() {
        // 生成随机key
        this.lockValue = UUID.randomUUID().toString();
        // 请求锁超时时间，纳秒
        long timeout = timeOut * 1000000;
        // 系统当前时间，纳秒
        long nowTime = System.nanoTime();
        while ((System.nanoTime() - nowTime) < timeout) {
            if (this.set(lockKey, lockValue, expireTime)) {
                locked = true;
                // 上锁成功结束请求
                return locked;
            }

            // 每次请求等待一段时间
            seleep(10, 50000);
        }
        return locked;
    }

    public boolean lock() {
        this.lockValue = UUID.randomUUID().toString();
        //不存在则添加 且设置过期时间（单位ms）
        locked = set(lockKey, lockValue, expireTime);
        return locked;
    }

    public boolean lockBlock() {
        this.lockValue = UUID.randomUUID().toString();
        while (true) {
            //不存在则添加 且设置过期时间（单位ms）
            locked = set(lockKey, lockValue, expireTime);
            if (locked) {
                return locked;
            }
            // 每次请求等待一段时间
            seleep(10, 50000);
        }
    }

    public Boolean unlock() {
        // 只有加锁成功并且锁还有效才去释放锁
        if (locked) {
            try {
                RedisScript<Long> script = RedisScript.of(UNLOCK_LUA, Long.class);
                List<String> keys = new ArrayList<>();
                keys.add(lockKey);
                Long result = redisTemplate.execute(script, keys, lockValue);
                if (result == 0 && !StringUtils.isEmpty(lockKeyLog)) {
                    logger.debug("Redis分布式锁，解锁{}失败！解锁时间：{}", lockKeyLog, System.currentTimeMillis());
                }

                locked = result == 0;
                return result == 1;
            } catch (Throwable e) {
                logger.warn("Redis不支持EVAL命令，使用降级方式解锁：{}", e.getMessage());
                String value = this.get(lockKey, String.class);
                if (lockValue.equals(value)) {
                    redisTemplate.delete(lockKey);
                    return true;
                }
                return false;
            }
        }

        return true;
    }

    private boolean set(final String key, final String value, final long seconds) {
        Assert.isTrue(!StringUtils.isEmpty(key), "key不能为空");
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
        if (!StringUtils.isEmpty(lockKeyLog) && Objects.nonNull(success) && success) {
            logger.debug("获取锁{}的时间：{}", lockKeyLog, System.currentTimeMillis());
        }
        return Objects.nonNull(success) && success;
    }

    private <T> T get(final String key, Class<T> aClass) {
        Assert.isTrue(!StringUtils.isEmpty(key), "key不能为空");
        return (T) redisTemplate.opsForValue().get(key);
    }

    public boolean isLock() {

        return locked;
    }

    private void seleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, random.nextInt(nanos));
        } catch (Exception e) {
            logger.debug("获取分布式锁休眠被中断：", e);
        }
    }

    public String getLockKeyLog() {
        return lockKeyLog;
    }

    public void setLockKeyLog(String lockKeyLog) {
        this.lockKeyLog = lockKeyLog;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
}
