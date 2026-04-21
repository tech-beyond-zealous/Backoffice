package com.gosmart.backoffice.dto;

import java.math.BigDecimal;

public class PatientMedicalRecordSaveRequest {
    private Integer id;
    private Integer medicalProviderId;
    private Long patientId;
    private Integer systolic;
    private Integer diastolic;
    private Integer pulse;
    private BigDecimal sugarLevel;
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMedicalProviderId() {
        return medicalProviderId;
    }

    public void setMedicalProviderId(Integer medicalProviderId) {
        this.medicalProviderId = medicalProviderId;
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
}
