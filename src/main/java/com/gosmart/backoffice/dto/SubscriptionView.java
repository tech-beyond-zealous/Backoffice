package com.gosmart.backoffice.dto;

public final class SubscriptionView {
    private final Integer id;
    private final Integer medicalProviderId;
    private final Long patientId;
    private final Integer medicalPackageId;
    private final String medicalProviderCode;
    private final String patientName;
    private final String packageName;
    private final String mode;
    private final String amount;
    private final String startDate;
    private final String expirationDate;
    private final String daysLeft;
    private final String subscriptionStatus;
    private final String createdDateTime;
    private final String createdBy;
    private final String modifiedDateTime;
    private final String modifiedBy;
    private final String remark;

    public SubscriptionView(
            Integer id,
            Integer medicalProviderId,
            Long patientId,
            Integer medicalPackageId,
            String medicalProviderCode,
            String patientName,
            String packageName,
            String mode,
            String amount,
            String startDate,
            String expirationDate,
            String daysLeft,
            String subscriptionStatus,
            String createdDateTime,
            String createdBy,
            String modifiedDateTime,
            String modifiedBy,
            String remark
    ) {
        this.id = id;
        this.medicalProviderId = medicalProviderId;
        this.patientId = patientId;
        this.medicalPackageId = medicalPackageId;
        this.medicalProviderCode = medicalProviderCode;
        this.patientName = patientName;
        this.packageName = packageName;
        this.mode = mode;
        this.amount = amount;
        this.startDate = startDate;
        this.expirationDate = expirationDate;
        this.daysLeft = daysLeft;
        this.subscriptionStatus = subscriptionStatus;
        this.createdDateTime = createdDateTime;
        this.createdBy = createdBy;
        this.modifiedDateTime = modifiedDateTime;
        this.modifiedBy = modifiedBy;
        this.remark = remark;
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

    public Integer getMedicalPackageId() {
        return medicalPackageId;
    }

    public String getMedicalProviderCode() {
        return medicalProviderCode;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getMode() {
        return mode;
    }

    public String getAmount() {
        return amount;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getDaysLeft() {
        return daysLeft;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
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

    public String getRemark() {
        return remark;
    }
}
