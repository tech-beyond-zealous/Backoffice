package com.gosmart.backoffice.dto;

import java.util.List;

public final class SubscriptionPackage {
    private final Integer id;
    private final String code;
    private final String name;
    private final List<String> description;
    private final Double monthlyAmount;
    private final Double yearlyAmount;

    public SubscriptionPackage(
            Integer id,
            String code,
            String name,
            List<String> description,
            Double monthlyAmount,
            Double yearlyAmount
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.monthlyAmount = monthlyAmount;
        this.yearlyAmount = yearlyAmount;
    }

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }

    public Double getMonthlyAmount() {
        return monthlyAmount;
    }

    public Double getYearlyAmount() {
        return yearlyAmount;
    }
}
