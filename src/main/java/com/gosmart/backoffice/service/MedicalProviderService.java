package com.gosmart.backoffice.service;

import com.gosmart.backoffice.domain.MedicalProviderEntity;
import com.gosmart.backoffice.repo.MedicalProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedicalProviderService {

    private final MedicalProviderRepository medicalProviderRepository;

    public MedicalProviderService(MedicalProviderRepository medicalProviderRepository) {
        this.medicalProviderRepository = medicalProviderRepository;
    }

    /**
     * Save or update a medical provider.
     */
    public MedicalProviderEntity save(MedicalProviderEntity medicalProvider) {
        return medicalProviderRepository.save(medicalProvider);
    }

    /**
     * Get all medical providers.
     */
    public List<MedicalProviderEntity> findAll() {
        return medicalProviderRepository.findAll();
    }

    /**
     * Get active medical providers assigned to the logged-in user.
     */
    public List<MedicalProviderEntity> findAccessibleByUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }
        return medicalProviderRepository.findAccessibleActiveByUserId(userId);
    }

    public boolean hasAccessibleProvider(String userId, Integer medicalProviderId) {
        if (userId == null || userId.isBlank() || medicalProviderId == null) {
            return false;
        }
        return medicalProviderRepository.countAccessibleActiveByUserIdAndMedicalProviderId(
                userId,
                medicalProviderId
        ) > 0;
    }

    /**
     * Get a medical provider by ID.
     */
    public Optional<MedicalProviderEntity> findById(Integer id) {
        return medicalProviderRepository.findById(id);
    }

    /**
     * Soft delete a medical provider by ID.
     */
    public void softDelete(Integer id) {
        medicalProviderRepository.findById(id).ifPresent(provider -> {
            provider.setStatus("D");
            medicalProviderRepository.save(provider);
        });
    }
}
