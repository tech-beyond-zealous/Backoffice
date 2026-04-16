package com.gosmart.backoffice.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "medical_package")
public class MedicalPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "amount_month")
    private Double amountMonth;

    @Column(name = "amount_year")
    private Double amountYear;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmountMonth() {
        return amountMonth;
    }

    public void setAmountMonth(Double amountMonth) {
        this.amountMonth = amountMonth;
    }

    public Double getAmountYear() {
        return amountYear;
    }

    public void setAmountYear(Double amountYear) {
        this.amountYear = amountYear;
    }
}
