package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.PatientCaregiverEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientCaregiverRepository extends JpaRepository<PatientCaregiverEntity, Integer> {
    List<PatientCaregiverEntity> findByPatientId(Integer patientId);

    List<PatientCaregiverEntity> findByMedicalProviderIdAndPatientId(Integer medicalProviderId, Integer patientId);
}
