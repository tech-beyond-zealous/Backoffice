package com.gosmart.backoffice.service;

import com.gosmart.backoffice.domain.CaregiverEntity;
import com.gosmart.backoffice.repo.CaregiverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CaregiverService {

    private final CaregiverRepository caregiverRepository;

    public CaregiverService(CaregiverRepository caregiverRepository) {
        this.caregiverRepository = caregiverRepository;
    }

    /**
     * Save or update a caregiver.
     */
    public CaregiverEntity save(CaregiverEntity caregiver) {
        return caregiverRepository.save(caregiver);
    }

    /**
     * Get all caregivers.
     */
    public List<CaregiverEntity> findAll() {
        return caregiverRepository.findAll();
    }

    /**
     * Get a caregiver by ID.
     */
    public Optional<CaregiverEntity> findById(Long id) {
        return caregiverRepository.findById(id);
    }

    /**
     * Find caregivers by name.
     */
    public List<CaregiverEntity> findByName(String name) {
        return caregiverRepository.findByName(name);
    }

    /**
     * Find a caregiver by email.
     */
    public Optional<CaregiverEntity> findByEmail(String email) {
        return caregiverRepository.findByEmail(email);
    }

    /**
     * Check if a caregiver email exists.
     */
    public boolean existsByEmail(String email) {
        return caregiverRepository.existsByEmail(email);
    }

    /**
     * Find a caregiver by mobile number.
     */
    public Optional<CaregiverEntity> findByMobileNumber(String mobileNumber) {
        return caregiverRepository.findByMobileNumber(mobileNumber);
    }

    /**
     * Check if a caregiver mobile number exists.
     */
    public boolean existsByMobileNumber(String mobileNumber) {
        return caregiverRepository.existsByMobileNumber(mobileNumber);
    }

    /**
     * Find a caregiver by system user ID.
     */
    public Optional<CaregiverEntity> findByUserId(String userId) {
        return caregiverRepository.findByUserId(userId);
    }

    /**
     * Delete a caregiver by ID.
     */
    public void deleteById(Long id) {
        caregiverRepository.deleteById(id);
    }
}