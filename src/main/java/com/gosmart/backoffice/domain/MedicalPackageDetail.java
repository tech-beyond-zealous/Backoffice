package com.gosmart.backoffice.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_package_detail")
public class MedicalPackageDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "medical_package_id", nullable = false)
    private Integer medicalPackageId;

    @Column(name = "description", length = 500)
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMedicalPackageId() {
        return medicalPackageId;
    }

    public void setMedicalPackageId(Integer medicalPackageId) {
        this.medicalPackageId = medicalPackageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
