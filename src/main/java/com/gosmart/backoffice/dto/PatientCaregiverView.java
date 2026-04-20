package com.gosmart.backoffice.dto;

import java.util.List;

public final class PatientCaregiverView {
    private final Integer id;
    private final Integer medicalProviderId;
    private final Integer patientId;
    private final String medicalProviderCode;
    private final String patientName;
    private final List<Integer> caregiverIds;
    private final String caregiverNames;
    private final String status;
    private final String createdDateTime;
    private final String createdBy;
    private final String modifiedDateTime;
    private final String modifiedBy;

    public PatientCaregiverView(
            Integer id,
            Integer medicalProviderId,
            Integer patientId,
            String medicalProviderCode,
            String patientName,
            List<Integer> caregiverIds,
            String caregiverNames,
            String status,
            String createdDateTime,
            String createdBy,
            String modifiedDateTime,
            String modifiedBy
    ) {
        this.id = id;
        this.medicalProviderId = medicalProviderId;
        this.patientId = patientId;
        this.medicalProviderCode = medicalProviderCode;
        this.patientName = patientName;
        this.caregiverIds = caregiverIds;
        this.caregiverNames = caregiverNames;
        this.status = status;
        this.createdDateTime = createdDateTime;
        this.createdBy = createdBy;
        this.modifiedDateTime = modifiedDateTime;
        this.modifiedBy = modifiedBy;
    }

    public Integer getId() {
        return id;
    }

    public Integer getMedicalProviderId() {
        return medicalProviderId;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public String getMedicalProviderCode() {
        return medicalProviderCode;
    }

    public String getPatientName() {
        return patientName;
    }

    public List<Integer> getCaregiverIds() {
        return caregiverIds;
    }

    public String getCaregiverNames() {
        return caregiverNames;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getModifiedDateTime() {
        return modifiedDateTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }
}
