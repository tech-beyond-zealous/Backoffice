package com.gosmart.backoffice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "sos_alert")
public class SosAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "create_dt", nullable = false)
    private LocalDateTime createDt;

    @Column(name = "create_by", nullable = false, length = 100)
    private String createBy;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;

    @Column(name = "modfy_by", length = 100)
    private String modifyBy;

    @Column(name = "uuid", nullable = false, length = 36)
    private String uuid;

    @Column(name = "patient_caregiver_id")
    private Integer patientCaregiverId;

    @Column(name = "sos_contact_id")
    private Integer sosContactId;

    @Column(name = "latitude", length = 20)
    private String latitude;

    @Column(name = "longitude", length = 20)
    private String longitude;

    @Column(name = "remark", length = 500)
    private String remark;

    @Column(name = "status", nullable = false, length = 1)
    private String status;

    public Integer getId() {
        return id;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public LocalDateTime getModifyDt() {
        return modifyDt;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public String getUuid() {
        return uuid;
    }

    public Integer getPatientCaregiverId() {
        return patientCaregiverId;
    }

    public Integer getSosContactId() {
        return sosContactId;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getRemark() {
        return remark;
    }

    public String getStatus() {
        return status;
    }
}
