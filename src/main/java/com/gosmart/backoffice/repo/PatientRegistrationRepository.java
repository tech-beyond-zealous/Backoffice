package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.PatientRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRegistrationRepository extends JpaRepository<PatientRegistration, Long> {

    Optional<PatientRegistration> findByIcPassportNo(String icPassportNo);

    boolean existsByIcPassportNo(String icPassportNo);

    List<PatientRegistration> findByStatus(String string);

    Optional<PatientRegistration> findByIdAndStatus(Long id, String status);
}
