package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.UserFunctionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserFunctionRepository extends JpaRepository<UserFunctionEntity, Long> {
    interface ApplicationSystemRow {
        Long getApplicationSystemId();

        String getSystemCode();

        String getSystemName();
    }

    interface MenuRow {
        Long getGroupId();

        String getGroupName();

        Integer getGroupSortOrder();

        Long getFunctionId();

        String getFunctionCode();

        String getFunctionName();

        String getPath();

        Integer getFunctionSortOrder();
    }

    interface FunctionPermissionRow {
        Long getFunctionId();

        String getFunctionCode();

        String getCanCreate();

        String getCanEdit();

        String getCanDelete();

        String getCanView();
    }

    @Query(value = ""
            + "SELECT DISTINCT\n"
            + "    app.application_system_id AS applicationSystemId,\n"
            + "    app.system_code AS systemCode,\n"
            + "    app.system_name AS systemName\n"
            + "FROM user_function uf\n"
            + "JOIN `function` f\n"
            + "    ON f.function_id = uf.function_id\n"
            + "    AND f.status = 'A'\n"
            + "JOIN function_group fg\n"
            + "    ON fg.group_id = f.group_id\n"
            + "    AND fg.status = 'A'\n"
            + "JOIN application_system app\n"
            + "    ON app.application_system_id = fg.application_system_id\n"
            + "    AND app.status = 'A'\n"
            + "WHERE uf.user_id = :userId\n"
            + "    AND uf.status = 'A'\n"
            + "    AND uf.view = 'Y'\n"
            + "ORDER BY systemName, systemCode\n"
            + "", nativeQuery = true)
    List<ApplicationSystemRow> findApplicationSystemsByUserId(@Param("userId") String userId);

    @Query(value = ""
            + "SELECT DISTINCT\n"
            + "    fg.group_id AS groupId,\n"
            + "    fg.group_name AS groupName,\n"
            + "    COALESCE(fg.sort_order, 0) AS groupSortOrder,\n"
            + "    f.function_id AS functionId,\n"
            + "    f.function_code AS functionCode,\n"
            + "    f.function_name AS functionName,\n"
            + "    f.path AS path,\n"
            + "    COALESCE(f.sort_order, 0) AS functionSortOrder\n"
            + "FROM user_function uf\n"
            + "JOIN `function` f\n"
            + "    ON f.function_id = uf.function_id\n"
            + "    AND f.status = 'A'\n"
            + "JOIN function_group fg\n"
            + "    ON fg.group_id = f.group_id\n"
            + "    AND fg.status = 'A'\n"
            + "WHERE uf.user_id = :userId\n"
            + "    AND uf.status = 'A'\n"
            + "    AND uf.view = 'Y'\n"
            + "    AND (:applicationSystemId IS NULL OR fg.application_system_id = :applicationSystemId)\n"
            + "ORDER BY groupSortOrder, functionSortOrder, functionName\n"
            + "", nativeQuery = true)
    List<MenuRow> findMenuRowsByUserId(
            @Param("userId") String userId,
            @Param("applicationSystemId") Long applicationSystemId
    );

    @Query(value = ""
            + "SELECT\n"
            + "    f.function_id AS functionId,\n"
            + "    f.function_code AS functionCode,\n"
            + "    uf.`create` AS canCreate,\n"
            + "    uf.edit AS canEdit,\n"
            + "    uf.`delete` AS canDelete,\n"
            + "    uf.view AS canView\n"
            + "FROM `function` f\n"
            + "LEFT JOIN user_function uf\n"
            + "    ON uf.function_id = f.function_id\n"
            + "    AND uf.user_id = :userId\n"
            + "    AND uf.status = 'A'\n"
            + "WHERE f.path = :path\n"
            + "    AND f.status = 'A'\n"
            + "ORDER BY f.function_id\n"
            + "LIMIT 1\n"
            + "", nativeQuery = true)
    Optional<FunctionPermissionRow> findFunctionPermissionRow(
            @Param("userId") String userId,
            @Param("path") String path
    );

    @Query(value = ""
            + "SELECT\n"
            + "    f.function_id AS functionId,\n"
            + "    f.function_code AS functionCode,\n"
            + "    uf.`create` AS canCreate,\n"
            + "    uf.edit AS canEdit,\n"
            + "    uf.`delete` AS canDelete,\n"
            + "    uf.view AS canView\n"
            + "FROM `function` f\n"
            + "LEFT JOIN user_function uf\n"
            + "    ON uf.function_id = f.function_id\n"
            + "    AND uf.user_id = :userId\n"
            + "    AND uf.status = 'A'\n"
            + "WHERE f.function_id = :functionId\n"
            + "    AND f.status = 'A'\n"
            + "LIMIT 1\n"
            + "", nativeQuery = true)
    Optional<FunctionPermissionRow> findFunctionPermissionRowByFunctionId(
            @Param("userId") String userId,
            @Param("functionId") Long functionId
    );
}
