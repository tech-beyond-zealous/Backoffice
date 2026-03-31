package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.FunctionGroupEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FunctionGroupRepository extends JpaRepository<FunctionGroupEntity, Long> {
    Optional<FunctionGroupEntity> findByGroupCode(String groupCode);

    Optional<FunctionGroupEntity> findByApplicationSystemIdAndGroupCode(Long applicationSystemId, String groupCode);
}
