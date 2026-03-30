package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
}
