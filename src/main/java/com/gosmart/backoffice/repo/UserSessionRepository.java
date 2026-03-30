package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.UserSessionEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {
    Optional<UserSessionEntity> findBySessionId(String sessionId);

    @Modifying
    @Query("""
            update UserSessionEntity s
            set s.revokeDt = :revokeDt, s.revokeReason = :revokeReason
            where s.userId = :userId and s.revokeDt is null
            """)
    int revokeActiveSessionsByUserId(
            @Param("userId") String userId,
            @Param("revokeDt") LocalDateTime revokeDt,
            @Param("revokeReason") String revokeReason
    );
}
