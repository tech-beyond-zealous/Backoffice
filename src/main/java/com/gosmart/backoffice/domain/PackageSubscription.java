package com.gosmart.backoffice.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "package_subscription")
public class PackageSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Column(name = "modify_by")
    private String modifyBy;

    @Column(name = "patient_id")
    private Integer patientId;

    @Column(name = "medical_provider_id")
    private Integer medicalProviderId;

    @Column(name = "medical_package_id")
    private Integer medicalPackageId;

    private BigDecimal amount;
    private String mode;

    @Column(name = "expiration_dt")
    private LocalDateTime expirationDt;

    private String remark;
    private String status;

    @PrePersist
    public void prePersist() {
        if (this.createDt == null) {
            this.createDt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "A";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.modifyDt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public void setCreateDt(LocalDateTime createDt) {
        this.createDt = createDt;
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

    public void setModifyDt(LocalDateTime modifyDt) {
        this.modifyDt = modifyDt;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getMedicalProviderId() {
        return medicalProviderId;
    }

    public void setMedicalProviderId(Integer medicalProviderId) {
        this.medicalProviderId = medicalProviderId;
    }

    public Integer getMedicalPackageId() {
        return medicalPackageId;
    }

    public void setMedicalPackageId(Integer medicalPackageId) {
        this.medicalPackageId = medicalPackageId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public LocalDateTime getExpirationDt() {
        return expirationDt;
    }

    public void setExpirationDt(LocalDateTime expirationDt) {
        this.expirationDt = expirationDt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
