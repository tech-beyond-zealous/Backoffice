package com.gosmart.backoffice.dto;

import java.math.BigDecimal;

public final class PatientMedicalRecordView {
    private final Integer id;
    private final Integer medicalProviderId;
    private final Long patientId;
    private final String medicalProviderCode;
    private final String patientName;
    private final String patientIcPassportNo;
    private final Integer systolic;
    private final Integer diastolic;
    private final String bpRecordedAt;
    private final Integer pulse;
    private final String pulseRecordedAt;
    private final BigDecimal sugarLevel;
    private final String sugarTestDate;
    private final Integer spo2;
    private final String spo2RecordedAt;
    private final BigDecimal temperature;
    private final String temperatureRecordedAt;
    private final Integer painScore;
    private final String painScoreRecordedAt;
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
            String patientIcPassportNo,
            Integer systolic,
            Integer diastolic,
            String bpRecordedAt,
            Integer pulse,
            String pulseRecordedAt,
            BigDecimal sugarLevel,
            String sugarTestDate,
            Integer spo2,
            String spo2RecordedAt,
            BigDecimal temperature,
            String temperatureRecordedAt,
            Integer painScore,
            String painScoreRecordedAt,
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
        this.patientIcPassportNo = patientIcPassportNo;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.bpRecordedAt = bpRecordedAt;
        this.pulse = pulse;
        this.pulseRecordedAt = pulseRecordedAt;
        this.sugarLevel = sugarLevel;
        this.sugarTestDate = sugarTestDate;
        this.spo2 = spo2;
        this.spo2RecordedAt = spo2RecordedAt;
        this.temperature = temperature;
        this.temperatureRecordedAt = temperatureRecordedAt;
        this.painScore = painScore;
        this.painScoreRecordedAt = painScoreRecordedAt;
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

    public String getPatientIcPassportNo() {
        return patientIcPassportNo;
    }

    public Integer getSystolic() {
        return systolic;
    }

    public Integer getDiastolic() {
        return diastolic;
    }

    public String getBpRecordedAt() {
        return bpRecordedAt;
    }

    public Integer getPulse() {
        return pulse;
    }

    public String getPulseRecordedAt() {
        return pulseRecordedAt;
    }

    public BigDecimal getSugarLevel() {
        return sugarLevel;
    }

    public String getSugarTestDate() {
        return sugarTestDate;
    }

    public Integer getSpo2() {
        return spo2;
    }

    public String getSpo2RecordedAt() {
        return spo2RecordedAt;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public String getTemperatureRecordedAt() {
        return temperatureRecordedAt;
    }

    public Integer getPainScore() {
        return painScore;
    }

    public String getPainScoreRecordedAt() {
        return painScoreRecordedAt;
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
