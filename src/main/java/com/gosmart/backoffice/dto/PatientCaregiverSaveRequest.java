package com.gosmart.backoffice.dto;

import java.util.List;

public class PatientCaregiverSaveRequest {
    private Integer medicalProviderId;
    private Integer patientId;
    private List<Integer> caregiverIds;
    private String status;

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

    public List<Integer> getCaregiverIds() {
        return caregiverIds;
    }

    public void setCaregiverIds(List<Integer> caregiverIds) {
        this.caregiverIds = caregiverIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
