package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.PackageSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageSubscriptionRepository extends JpaRepository<PackageSubscription, Integer> {
    List<PackageSubscription> findByStatus(String status);

    boolean existsByPatientIdAndStatusAndIdNot(Integer patientId, String status, Integer id);

    boolean existsByPatientIdAndStatus(Integer patientId, String status);
}
