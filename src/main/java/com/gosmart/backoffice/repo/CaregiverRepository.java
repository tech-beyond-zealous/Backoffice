package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.CaregiverEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface CaregiverRepository extends JpaRepository<CaregiverEntity, Long> {

    Optional<CaregiverEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<CaregiverEntity> findByMobileNumber(String mobileNumber);

    boolean existsByMobileNumber(String mobileNumber);

    Optional<CaregiverEntity> findByUserId(String userId);

    List<CaregiverEntity> findByName(String name);
}