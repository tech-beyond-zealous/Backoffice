package com.gosmart.backoffice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "user_password")
public class UserPasswordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "create_dt")
    private Instant createDt;

    @Column(name = "modify_dt")
    private Instant modifyDt;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "hashed_pwd")
    private String hashedPwd;

    public Integer getId() {
        return id;
    }

    public Instant getCreateDt() {
        return createDt;
    }

    public Instant getModifyDt() {
        return modifyDt;
    }

    public String getUserId() {
        return userId;
    }

    public String getHashedPwd() {
        return hashedPwd;
    }
}
