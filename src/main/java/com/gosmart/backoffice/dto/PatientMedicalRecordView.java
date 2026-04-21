package com.gosmart.backoffice.dto;

import java.math.BigDecimal;

public final class PatientMedicalRecordView {
    private final Integer id;
    private final Integer medicalProviderId;
    private final Long patientId;
    private final String medicalProviderCode;
    private final String patientName;
    private final Integer systolic;
    private final Integer diastolic;
    private final Integer pulse;
    private final BigDecimal sugarLevel;
    private final String remark;
    private final String createdDateTime;
    private final String createdBy;
    private final String modifiedDateTime;
    private final String modifiedBy;

    public PatientMedicalRecordView(
            Integer id,
            Integer medicalProviderId,
            Long patientId,
            String medicalProviderCode,
            String patientName,
            Integer systolic,
            Integer diastolic,
            Integer pulse,
            BigDecimal sugarLevel,
            String remark,
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
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.pulse = pulse;
        this.sugarLevel = sugarLevel;
        this.remark = remark;
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

    public Long getPatientId() {
        return patientId;
    }

    public String getMedicalProviderCode() {
        return medicalProviderCode;
    }

    public String getPatientName() {
        return patientName;
    }

    public Integer getSystolic() {
        return systolic;
    }

    public Integer getDiastolic() {
        return diastolic;
    }

    public Integer getPulse() {
        return pulse;
    }

    public BigDecimal getSugarLevel() {
        return sugarLevel;
    }

    public String getRemark() {
        return remark;
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
