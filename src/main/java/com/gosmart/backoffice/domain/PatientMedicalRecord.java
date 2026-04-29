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

    @Column(name = "bp_recorded_at")
    private LocalDateTime bpRecordedAt;

    private Integer pulse;

    @Column(name = "pulse_recorded_at")
    private LocalDateTime pulseRecordedAt;

    @Column(name = "sugar_level")
    private BigDecimal sugarLevel;

    @Column(name = "sugar_test_date")
    private LocalDateTime sugarTestDate;

    private Integer spo2;

    @Column(name = "spo2_recorded_at")
    private LocalDateTime spo2RecordedAt;

    private BigDecimal temperature;

    @Column(name = "temperature_recorded_at")
    private LocalDateTime temperatureRecordedAt;

    @Column(name = "pain_score")
    private Integer painScore;

    @Column(name = "pain_score_recorded_at")
    private LocalDateTime painScoreRecordedAt;

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

    public LocalDateTime getBpRecordedAt() {
        return bpRecordedAt;
    }

    public void setBpRecordedAt(LocalDateTime bpRecordedAt) {
        this.bpRecordedAt = bpRecordedAt;
    }

    public Integer getPulse() {
        return pulse;
    }

    public void setPulse(Integer pulse) {
        this.pulse = pulse;
    }

    public LocalDateTime getPulseRecordedAt() {
        return pulseRecordedAt;
    }

    public void setPulseRecordedAt(LocalDateTime pulseRecordedAt) {
        this.pulseRecordedAt = pulseRecordedAt;
    }

    public BigDecimal getSugarLevel() {
        return sugarLevel;
    }

    public void setSugarLevel(BigDecimal sugarLevel) {
        this.sugarLevel = sugarLevel;
    }

    public LocalDateTime getSugarTestDate() {
        return sugarTestDate;
    }

    public void setSugarTestDate(LocalDateTime sugarTestDate) {
        this.sugarTestDate = sugarTestDate;
    }

    public Integer getSpo2() {
        return spo2;
    }

    public void setSpo2(Integer spo2) {
        this.spo2 = spo2;
    }

    public LocalDateTime getSpo2RecordedAt() {
        return spo2RecordedAt;
    }

    public void setSpo2RecordedAt(LocalDateTime spo2RecordedAt) {
        this.spo2RecordedAt = spo2RecordedAt;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public LocalDateTime getTemperatureRecordedAt() {
        return temperatureRecordedAt;
    }

    public void setTemperatureRecordedAt(LocalDateTime temperatureRecordedAt) {
        this.temperatureRecordedAt = temperatureRecordedAt;
    }

    public Integer getPainScore() {
        return painScore;
    }

    public void setPainScore(Integer painScore) {
        this.painScore = painScore;
    }

    public LocalDateTime getPainScoreRecordedAt() {
        return painScoreRecordedAt;
    }

    public void setPainScoreRecordedAt(LocalDateTime painScoreRecordedAt) {
        this.painScoreRecordedAt = painScoreRecordedAt;
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
