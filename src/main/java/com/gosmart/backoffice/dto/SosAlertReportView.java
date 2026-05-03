package com.gosmart.backoffice.dto;

public final class SosAlertReportView {
    private final Integer id;
    private final Integer medicalProviderId;
    private final String medicalProviderCode;
    private final String medicalProviderName;
    private final Long patientId;
    private final String patientName;
    private final Long caregiverId;
    private final String caregiverName;
    private final String uuid;
    private final String latitude;
    private final String longitude;
    private final String locationImageUrl;
    private final String remark;
    private final String status;
    private final String alertDateTime;
    private final Integer pushAttempts;
    private final String ackDateTime;

    public SosAlertReportView(
            Integer id,
            Integer medicalProviderId,
            String medicalProviderCode,
            String medicalProviderName,
            Long patientId,
            String patientName,
            Long caregiverId,
            String caregiverName,
            String uuid,
            String latitude,
            String longitude,
            String locationImageUrl,
            String remark,
            String status,
            String alertDateTime,
            Integer pushAttempts,
            String ackDateTime
    ) {
        this.id = id;
        this.medicalProviderId = medicalProviderId;
        this.medicalProviderCode = medicalProviderCode;
        this.medicalProviderName = medicalProviderName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.caregiverId = caregiverId;
        this.caregiverName = caregiverName;
        this.uuid = uuid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationImageUrl = locationImageUrl;
        this.remark = remark;
        this.status = status;
        this.alertDateTime = alertDateTime;
        this.pushAttempts = pushAttempts;
        this.ackDateTime = ackDateTime;
    }

    public Integer getId() {
        return id;
    }

    public Integer getMedicalProviderId() {
        return medicalProviderId;
    }

    public String getMedicalProviderCode() {
        return medicalProviderCode;
    }

    public String getMedicalProviderName() {
        return medicalProviderName;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public Long getCaregiverId() {
        return caregiverId;
    }

    public String getCaregiverName() {
        return caregiverName;
    }

    public String getUuid() {
        return uuid;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLocationImageUrl() {
        return locationImageUrl;
    }

    public String getRemark() {
        return remark;
    }

    public String getStatus() {
        return status;
    }

    public String getAlertDateTime() {
        return alertDateTime;
    }

    public Integer getPushAttempts() {
        return pushAttempts;
    }

    public String getAckDateTime() {
        return ackDateTime;
    }
}
