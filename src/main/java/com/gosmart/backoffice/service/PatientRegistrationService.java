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
     * Get all patient registrations.
     */
    public List<PatientRegistration> findAll() {
        return patientRegistrationRepository.findAll();
    }

    /**
     * Get a patient registration by ID.
     */
    public Optional<PatientRegistration> findById(Long id) {
        return patientRegistrationRepository.findById(id);
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
     * Delete a patient registration by ID.
     */
    public void deleteById(Long id) {
        patientRegistrationRepository.deleteById(id);
    }

    /**
     * Delete a patient registration.
     */
    public void delete(PatientRegistration patient) {
        patientRegistrationRepository.delete(patient);
    }
}
