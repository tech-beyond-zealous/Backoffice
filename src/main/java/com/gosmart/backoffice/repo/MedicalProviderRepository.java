package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.MedicalProviderEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalProviderRepository extends JpaRepository<MedicalProviderEntity, Integer> {
}