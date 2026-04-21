package com.gosmart.backoffice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_medical_record")
public class PatientMedicalRecord {

    @Id
    private Integer id;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "bp_systolic")
    private Integer systolic;

    @Column(name = "bp_diastolic")
    private Integer diastolic;

    private Integer pulse;

    @Column(name = "sugar_level")
    private BigDecimal sugarLevel;

    private String remark;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Column(name = "modify_by")
    private String modifyBy;

    private String status;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createDt == null) {
            createDt = now;
        }
        modifyDt = now;
        if (status == null) {
            status = "A";
        }
    }

    @PreUpdate
    public void preUpdate() {
        modifyDt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Integer getSystolic() {
        return systolic;
    }

    public void setSystolic(Integer systolic) {
        this.systolic = systolic;
    }

    public Integer getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(Integer diastolic) {
        this.diastolic = diastolic;
    }

    public Integer getPulse() {
        return pulse;
    }

    public void setPulse(Integer pulse) {
        this.pulse = pulse;
    }

    public BigDecimal getSugarLevel() {
        return sugarLevel;
    }

    public void setSugarLevel(BigDecimal sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
