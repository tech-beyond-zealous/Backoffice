package com.gosmart.backoffice.service;

import com.gosmart.backoffice.domain.PatientRegistration;
import com.gosmart.backoffice.repo.PatientRegistrationRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientRegistrationService {
    private final PatientRegistrationRepository patientRegistrationRepository;

    public PatientRegistrationService(PatientRegistrationRepository patientRegistrationRepository) {
        this.patientRegistrationRepository = patientRegistrationRepository;
    }

    /**
     * Save or update a patient registration.
     */
    public PatientRegistration save(PatientRegistration patient) {
        return patientRegistrationRepository.save(patient);
    }

    /**
     * Get all active patient registrations.
     */
    public List<PatientRegistration> findAll() {
        return patientRegistrationRepository.findByStatus("A");
    }

    /**
     * Get an active patient registration by ID.
     */
    public Optional<PatientRegistration> findById(Long id) {
        return patientRegistrationRepository.findByIdAndStatus(id, "A");
    }

    /**
     * Find a patient by IC/Passport number.
     */
    public Optional<PatientRegistration> findByIcPassportNo(String icPassportNo) {
        return patientRegistrationRepository.findByIcPassportNo(icPassportNo);
    }

    /**
     * Check if IC/Passport number exists.
     */
    public boolean existsByIcPassportNo(String icPassportNo) {
        return patientRegistrationRepository.existsByIcPassportNo(icPassportNo);
    }

    /**
     * Soft delete a patient registration by ID (set status to 'D' and update modify_by and modify_dt).
     */
    public void deleteById(Long id, String currentUser) {
    Optional<PatientRegistration> optionalPatient = patientRegistrationRepository.findById(id);

        if (optionalPatient.isPresent()) {
            PatientRegistration patient = optionalPatient.get();
            patient.setStatus("D");
            patient.setModifyBy(currentUser);
            patient.setModifyDt(java.time.LocalDateTime.now());

            patientRegistrationRepository.save(patient);
        }
    }

}
