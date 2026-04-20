package com.gosmart.backoffice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_caregiver")
public class PatientCaregiverEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "create_by", nullable = false, length = 100)
    private String createBy;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Column(name = "modify_by", length = 100)
    private String modifyBy;

    @Column(name = "medical_provider_id", nullable = false)
    private Integer medicalProviderId;

    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "caregiver_id", nullable = false)
    private Integer caregiverId;

    @Column(name = "status", nullable = false, length = 1)
    private String status;

    @PrePersist
    public void onCreate() {
        this.createDt = LocalDateTime.now();
        this.modifyDt = LocalDateTime.now();
        if (this.status == null || this.status.isBlank()) {
            this.status = "A";
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.modifyDt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
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

    public Integer getMedicalProviderId() {
        return medicalProviderId;
    }

    public void setMedicalProviderId(Integer medicalProviderId) {
        this.medicalProviderId = medicalProviderId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public Integer getCaregiverId() {
        return caregiverId;
    }

    public void setCaregiverId(Integer caregiverId) {
        this.caregiverId = caregiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
