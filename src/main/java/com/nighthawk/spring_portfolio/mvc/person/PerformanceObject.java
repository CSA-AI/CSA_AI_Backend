package com.nighthawk.spring_portfolio.mvc.person;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = false)
public class PerformanceObject extends PerformanceCollectable implements Iterable<PerformanceObject> {
    public enum KeyType implements KeyTypes {rankNumber, accountValue, accountGrowth}
    public static KeyTypes key = KeyType.accountGrowth;


    public void setOrder(KeyTypes key) {PerformanceObject.key = key;}

    private String sortingKey = "accountGrowth";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Column(unique = true)
    private String username;

    @Column
    private int rankNumber;

    @Column
    private Double accountValue;

    @Column
    private Double accountGrowth;

    @Column
    private String rating;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Map<String, Object>> stats = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public PerformanceObject(String username, int rankNumber, Double accountValue, Double accountGrowth, String rating) {
        this.username = username;
        this.rankNumber = rankNumber;
        this.accountValue = accountValue;
        this.accountGrowth = accountGrowth;
        this.rating = rating;
    }

    
    @Override
    public String toString() {
        if (KeyType.accountGrowth.equals(PerformanceObject.key)) {
            return "(" + Double.toString(this.accountGrowth) + ")";
        } else if (KeyType.accountValue.equals(PerformanceObject.key)) {
            return "(" + Double.toString(this.accountValue) + ")";
        } else if (KeyType.rankNumber.equals(PerformanceObject.key)) {
            return "(" + Integer.toString(this.rankNumber) + ")";
        }
        return "Invalid Key";
    }

    @Override
    public int compareTo(PerformanceCollectable performanceObj) {
        if (KeyType.accountGrowth.equals(PerformanceObject.key)) {
            return Double.compare(this.accountGrowth, performanceObj.getAccountGrowth());
        } 
        // if (KeyType.accountValue.equals(PerformanceObject.key)) {
        //     return Double.compare(this.accountValue, performanceObj.getAccountValue());
        // }
        return this.toString().compareTo(performanceObj.toString());
    }

    @Override
    protected KeyTypes getKey() {
        return PerformanceObject.key;
    }

    @Override
    public Iterator<PerformanceObject> iterator() {
        return null; // Implementation here
    }
    public static void main(String[] args) {
        
    }
}
