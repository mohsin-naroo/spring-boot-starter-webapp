package io.github.meritepk.webapp.user;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String userName);

    @Transactional
    @Modifying
    @Query("update User u set u.loginAt = :loginAt, u.sessionId = :sessionId where u.id = :userId")
    int updateLoginSession(Long userId, LocalDateTime loginAt, String sessionId);
}
