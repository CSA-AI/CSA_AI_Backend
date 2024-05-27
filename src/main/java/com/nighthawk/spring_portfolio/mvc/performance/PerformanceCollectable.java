package com.nighthawk.spring_portfolio.mvc.performance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public abstract class PerformanceCollectable implements Comparable<PerformanceCollectable> {
    public final String masterType = "PerformanceCollectable";
    private int rankNumber;
    private String username;
    private Double accountValue;
    private Double accountGrowth;
    private String rating;

  


    public String getMasterType() {
        return masterType;
    }

    // getter
    public String getType() {
        return getClass().getSimpleName();
    }

    // this method is used to establish key order
    @Override
    public abstract String toString();

    // this method is used to compare toString of objects
    @Override
    public int compareTo(PerformanceCollectable obj) {
        return this.toString().compareTo(obj.toString());
    }

    // Constructor
    public PerformanceCollectable(int rankNumber, String username, Double accountValue, Double accountGrowth, String rating) {
        this.rankNumber = rankNumber;
        this.username = username;
        this.accountValue = accountValue;
        this.accountGrowth = accountGrowth;
        this.rating = rating;
    }
}