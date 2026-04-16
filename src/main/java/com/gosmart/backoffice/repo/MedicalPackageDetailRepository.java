package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.MedicalPackageDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalPackageDetailRepository extends JpaRepository<MedicalPackageDetail, Integer> {
    List<MedicalPackageDetail> findByMedicalPackageId(Integer medicalPackageId);
}
