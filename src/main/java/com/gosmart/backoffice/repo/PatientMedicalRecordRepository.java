package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.PatientMedicalRecord;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientMedicalRecordRepository extends JpaRepository<PatientMedicalRecord, Integer> {
    List<PatientMedicalRecord> findByStatus(String status);

    Optional<PatientMedicalRecord> findByIdAndStatus(Integer id, String status);
}
