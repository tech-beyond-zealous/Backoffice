package com.gosmart.backoffice.dto;

public class SubscriptionSaveRequest {
    private Integer id;
    private Integer medicalProviderId;
    private Integer medicalPackageId;
    private Long patientId;
    private String mode;
    private String expirationDate;
    private String remark;

    public SubscriptionSaveRequest() {
    }

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

    public Integer getMedicalPackageId() {
        return medicalPackageId;
    }

    public void setMedicalPackageId(Integer medicalPackageId) {
        this.medicalPackageId = medicalPackageId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
