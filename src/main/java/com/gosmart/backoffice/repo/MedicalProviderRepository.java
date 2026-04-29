package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.MedicalProviderEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicalProviderRepository extends JpaRepository<MedicalProviderEntity, Integer> {
    @Query(
            value = """
                    SELECT mp.*
                    FROM medical_provider mp
                    JOIN user_medical_provider ump
                      ON ump.medical_provider_id = mp.id
                    JOIN user_password up
                      ON up.id = ump.user_id
                    WHERE up.user_id = :userId
                      AND ump.status = 'A'
                      AND mp.status = 'A'
                    ORDER BY mp.name
                    """,
            nativeQuery = true
    )
    List<MedicalProviderEntity> findAccessibleActiveByUserId(@Param("userId") String userId);

    @Query(
            value = """
                    SELECT COUNT(1)
                    FROM medical_provider mp
                    JOIN user_medical_provider ump
                      ON ump.medical_provider_id = mp.id
                    JOIN user_password up
                      ON up.id = ump.user_id
                    WHERE up.user_id = :userId
                      AND ump.medical_provider_id = :medicalProviderId
                      AND ump.status = 'A'
                      AND mp.status = 'A'
                    """,
            nativeQuery = true
    )
    long countAccessibleActiveByUserIdAndMedicalProviderId(
            @Param("userId") String userId,
            @Param("medicalProviderId") Integer medicalProviderId
    );
}
