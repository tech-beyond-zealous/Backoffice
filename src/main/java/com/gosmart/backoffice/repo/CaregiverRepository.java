package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.CaregiverEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaregiverRepository extends JpaRepository<CaregiverEntity, Long> {
}