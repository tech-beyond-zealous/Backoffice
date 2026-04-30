package com.gosmart.backoffice.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_registration")
public class PatientRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Info
    @Column(nullable = false)
    private String name;

    private Integer age;
    private String gender;
    private String race;

    // Identity
    @Column(name = "ic_passport_no", nullable = false, unique = true)
    private String icPassportNo;

    // Contact
    private String mobileNo;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactNo;
    private String relationship;

    // Address
    private String address;
    private String area;
    private String postcode;
    private String state;
    private String city;

    // Medical
    private String hasChronicDisease; // Y / N
    @Column(length = 500)
    private String chronicDisease;
    @Column(name = "medicine_taken_now", length = 500)
    private String medicineTakenNow;
    @Column(name = "has_allergies", columnDefinition = "char(1)", length = 1)
    private String hasAllergies;
    @Column(name = "allergy_details", length = 500)
    private String allergyDetails;
    @Column(name = "remark")
    private String remark;

    // System Link
    private String gosmartUserId;
    @Column(name = "medical_provider_id")
    private Long medicalProviderId;

    // Audit
    private LocalDateTime createDt;
    private LocalDateTime modifyDt;
    @Column(name = "create_by", nullable = false)
    private String createBy;

    @Column(name = "modify_by")
    private String modifyBy;

    private String status;

    // ========================
    // Auto Set Dates
    // ========================
    @PrePersist
    public void onCreate() {
        this.createDt = LocalDateTime.now();
        this.modifyDt = LocalDateTime.now();
        this.status = "A";
    }

    @PreUpdate
    public void onUpdate() {
        this.modifyDt = LocalDateTime.now();
    }

    // ========================
    // Getters & Setters
    // ========================
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getIcPassportNo() {
        return icPassportNo;
    }

    public void setIcPassportNo(String icPassportNo) {
        this.icPassportNo = icPassportNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactNo() {
        return emergencyContactNo;
    }

    public void setEmergencyContactNo(String emergencyContactNo) {
        this.emergencyContactNo = emergencyContactNo;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHasChronicDisease() {
        return hasChronicDisease;
    }

    public void setHasChronicDisease(String hasChronicDisease) {
        this.hasChronicDisease = hasChronicDisease;
    }

    public String getChronicDisease() {
        return chronicDisease;
    }

    public void setChronicDisease(String chronicDisease) {
        this.chronicDisease = chronicDisease;
    }

    public String getMedicineTakenNow() {
        return medicineTakenNow;
    }

    public void setMedicineTakenNow(String medicineTakenNow) {
        this.medicineTakenNow = medicineTakenNow;
    }

    public String getHasAllergies() {
        return hasAllergies;
    }

    public void setHasAllergies(String hasAllergies) {
        this.hasAllergies = hasAllergies;
    }

    public String getAllergyDetails() {
        return allergyDetails;
    }

    public void setAllergyDetails(String allergyDetails) {
        this.allergyDetails = allergyDetails;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGosmartUserId() {
        return gosmartUserId;
    }

    public void setGosmartUserId(String gosmartUserId) {
        this.gosmartUserId = gosmartUserId;
    }

    public Long getMedicalProviderId() {
        return medicalProviderId;
    }

    public void setMedicalProviderId(Long medicalProviderId) {
        this.medicalProviderId = medicalProviderId;
    }

    public LocalDateTime getCreateDt() {
        return createDt;
    }

    public LocalDateTime getModifyDt() {
        return modifyDt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public void setModifyDt(LocalDateTime modifyDt) {
        this.modifyDt = modifyDt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
   
}
