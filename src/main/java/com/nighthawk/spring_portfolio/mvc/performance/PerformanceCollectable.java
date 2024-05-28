package com.nighthawk.spring_portfolio.mvc.performance;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class PerformanceCollectable implements Comparable<PerformanceCollectable> {
    public final String masterType = "PerformanceCollectable";
    private int rankNumber;
    private String username;
    private Double accountValue;
    private Double accountGrowth;
    private String rating;

    public interface KeyTypes {
        String name();
    }

    protected abstract KeyTypes getKey();

    public String getMasterType() {
        return masterType;
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    @Override
    public abstract String toString();

    @Override
    public int compareTo(PerformanceCollectable obj) {
        return this.toString().compareTo(obj.toString());
    }

    public PerformanceCollectable(int rankNumber, String username, Double accountValue, Double accountGrowth, String rating) {
        this.rankNumber = rankNumber;
        this.username = username;
        this.accountValue = accountValue;
        this.accountGrowth = accountGrowth;
        this.rating = rating;
    }
}
