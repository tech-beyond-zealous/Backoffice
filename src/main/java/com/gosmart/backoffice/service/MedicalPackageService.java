package com.gosmart.backoffice.service;

import com.gosmart.backoffice.domain.MedicalPackage;
import com.gosmart.backoffice.domain.MedicalPackageDetail;
import com.gosmart.backoffice.dto.SubscriptionPackage;
import com.gosmart.backoffice.repo.MedicalPackageDetailRepository;
import com.gosmart.backoffice.repo.MedicalPackageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MedicalPackageService {

    private final MedicalPackageRepository medicalPackageRepository;
    private final MedicalPackageDetailRepository medicalPackageDetailRepository;

    public MedicalPackageService(
            MedicalPackageRepository medicalPackageRepository,
            MedicalPackageDetailRepository medicalPackageDetailRepository
    ) {
        this.medicalPackageRepository = medicalPackageRepository;
        this.medicalPackageDetailRepository = medicalPackageDetailRepository;
    }

    public List<SubscriptionPackage> getAllPackages() {
        List<MedicalPackage> packages = medicalPackageRepository.findAll();

        return packages.stream().map(pkg -> {
            List<String> details = medicalPackageDetailRepository
                    .findByMedicalPackageId(pkg.getId())
                    .stream()
                    .map(MedicalPackageDetail::getDescription)
                    .toList();

            return new SubscriptionPackage(
                    pkg.getId(),
                    pkg.getCode(),
                    pkg.getName(),
                    details,
                    pkg.getAmountMonth(),
                    pkg.getAmountYear()
            );
        }).toList();
    }

    public Optional<MedicalPackage> findById(Integer id) {
        return medicalPackageRepository.findById(id);
    }
}
