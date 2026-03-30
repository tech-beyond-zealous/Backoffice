package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.UserPasswordEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPasswordRepository extends JpaRepository<UserPasswordEntity, Integer> {
    Optional<UserPasswordEntity> findByUserId(String userId);
}
