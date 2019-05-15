package net.idt.psp.screenpop.service.calldata;

import java.time.Duration;

import net.idt.psp.screenpop.service.calldata.StringCrudRepository;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;

// Modeled after the UserDAO.java example in 
// https://www.concretepage.com/spring-4/spring-data-redis-example

@Repository
@Slf4j
@RequiredArgsConstructor
public class CalldataRepository implements StringCrudRepository {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    /**
     * Find the string-based calldata by key
     * @param key
     * @return the calldata 
     */
    public String findByKey (String key) {
        return stringRedisTemplate.opsForValue().get (key);
    }

    /**
     * Create a new (or overwrite an existing) string-based calldata by key. 
     * @param key
     * @param calldata 
     * @param ttl time to live
     */
    @Override
    public void saveByKey (String key, String calldata, Duration ttl) {
        stringRedisTemplate.opsForValue().set (key, calldata, ttl);
    }

    /**
     * Delete the calldata by key
     * @param key
     */
    @Override
    public void deleteByKey (String key) {
        stringRedisTemplate.delete (key);
        return;
    }

    /**
     * Is redis up? 
     * @param key
     */
    @Override
    public Boolean isAvailable() {
       try{
           return stringRedisTemplate.getConnectionFactory().getConnection().ping() != null;
       } catch (Exception e) {
           log.warn("Redis server is not available at the moment.");
       }
       return false;
   }
}
