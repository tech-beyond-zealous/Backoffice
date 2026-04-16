package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.MedicalPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalPackageRepository extends JpaRepository<MedicalPackage, Integer> {
}
