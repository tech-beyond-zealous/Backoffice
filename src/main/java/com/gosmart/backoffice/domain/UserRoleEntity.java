package com.gosmart.backoffice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_role")
public class UserRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long userRoleId;

    @Column(name = "role_function_id")
    private Long roleFunctionId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "status")
    private String status;

    public Long getUserRoleId() {
        return userRoleId;
    }

    public Long getRoleFunctionId() {
        return roleFunctionId;
    }

    public void setRoleFunctionId(Long roleFunctionId) {
        this.roleFunctionId = roleFunctionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
