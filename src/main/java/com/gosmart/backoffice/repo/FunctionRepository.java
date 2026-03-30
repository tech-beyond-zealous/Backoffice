package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.FunctionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FunctionRepository extends JpaRepository<FunctionEntity, Long> {
    Optional<FunctionEntity> findByFunctionCode(String functionCode);

    interface MenuRow {
        Long getGroupId();

        String getGroupName();

        Integer getGroupSortOrder();

        Long getFunctionId();

        String getFunctionName();

        String getPath();

        Integer getFunctionSortOrder();
    }

    @Query(value = """
            SELECT DISTINCT
                fg.group_id AS groupId,
                fg.group_name AS groupName,
                COALESCE(fg.sort_order, 0) AS groupSortOrder,
                f.function_id AS functionId,
                f.function_name AS functionName,
                f.path AS path,
                COALESCE(f.sort_order, 0) AS functionSortOrder
            FROM user_role ur
            JOIN role_function rf
                ON rf.role_function_id = ur.role_function_id
                AND rf.status = 'A'
            JOIN `function` f
                ON f.function_id = rf.function_id
                AND f.status = 'A'
            JOIN function_group fg
                ON fg.group_id = f.group_id
                AND fg.status = 'A'
            WHERE ur.user_id = :userId
                AND ur.status = 'A'
            ORDER BY groupSortOrder, functionSortOrder, functionName
            """, nativeQuery = true)
    List<MenuRow> findMenuRowsByUserId(@Param("userId") String userId);
}
