package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.FunctionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FunctionRepository extends JpaRepository<FunctionEntity, Long> {
    Optional<FunctionEntity> findByFunctionCode(String functionCode);
}
