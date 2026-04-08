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
     * Delete a caregiver by ID.
     */
    public void softDelete(Long id, String userId) {
        caregiverRepository.findById(id).ifPresent(caregiver -> {
            caregiver.setStatus("D");
            caregiver.setModifyBy(userId);
            caregiverRepository.save(caregiver);
        });
    }
}