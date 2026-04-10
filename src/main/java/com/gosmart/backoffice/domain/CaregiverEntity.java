package com.gosmart.backoffice.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "caregiver")
public class CaregiverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medical_provider_id", nullable = false)
    private Integer medicalProviderId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "mobile_number", nullable = false, length = 20)
    private String mobileNumber;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "create_by", length = 100)
    private String createBy;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Column(name = "modify_by", length = 100)
    private String modifyBy;

    @Column(name = "status", length = 1)
    private String status;

    @PrePersist
    public void onCreate() {
        this.createDt = LocalDateTime.now();
        this.modifyDt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "A";
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.modifyDt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Integer getMedicalProviderId() {
        return medicalProviderId;
    }

    public void setMedicalProviderId(Integer medicalProviderId) {
        this.medicalProviderId = medicalProviderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getModifyDt() {
        return modifyDt;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}