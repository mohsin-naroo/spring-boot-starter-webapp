package io.github.meritepk.webapp.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public static final String USERS = "users";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private CacheManager cacheManager;

    private UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = USERS)
    public User findByUserName(String userName) {
        return repository.findByUserName(userName);
    }

    public User findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @CacheEvict(cacheNames = USERS, key = "#user.userName")
    public void updateLoginSession(User user) {
        repository.updateLoginSession(user.getId(), user.getLoginAt(), user.getSessionId());
    }

    @Async
    @Scheduled(cron = "${webapp.cache.users.evict.schedule:* */30 * * * *}")
    public void cacheEvict() {
        logger.info("CacheEvict: {}", USERS);
        if (cacheManager != null) {
            Cache cache = cacheManager.getCache(USERS);
            if (cache != null) {
                cache.clear();
            }
        }
    }
}
