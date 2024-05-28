package com.nighthawk.spring_portfolio.mvc.performance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Iterator;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

//@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Convert(attributeName ="PerformanceObject", converter = JsonType.class)
public class PerformanceObject extends PerformanceCollectable implements Iterable<PerformanceObject> {
    public enum KeyType implements KeyTypes { rankNumber, accountValue, accountGrowth }
   public static KeyTypes key = KeyType.accountGrowth;

    private String sortingKey = "accountGrowth";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Column(unique=true)
    private String username;

    @Column()
    private int rankNumber;

    @Column()
    private Double accountValue;

    @Column()
    private Double accountGrowth;

    @Column()
    private String rating;

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
        return this.toString().compareTo(performanceObj.toString());
    }

    @Override
    protected KeyTypes getKey() {
        return PerformanceObject.key;
    }

    @Override
    public Iterator<PerformanceObject> iterator() {
        List<PerformanceObject> sortedList = new ArrayList<>(Arrays.asList(this));
        sortedList.sort(Comparator.naturalOrder());
        return sortedList.iterator();
    }

    // Initialize static test data
    public static PerformanceObjectIterator init() {
        PerformanceObject p1 = new PerformanceObject("Alice", 1, 10000.0, 5.0, "HIGHER");
        PerformanceObject p2 = new PerformanceObject("Bob", 2, 8000.0, 3.0, "LOWER");
        PerformanceObject[] performances = { p1, p2 };
        ArrayList<PerformanceObject> performanceList = new ArrayList<>(Arrays.asList(performances));
        return new PerformanceObjectIterator(performanceList);
    }

    public static void main(String[] args) {
        PerformanceObjectIterator performances = init();

        for (PerformanceObject performance : performances) {
            System.out.println(performance);
        }

        performances.setOrder(KeyType.rankNumber);
        performances.mergeSort(0, performances.size() - 1);
        System.out.println();

        for (PerformanceObject performance : performances) {
            System.out.println(performance);
        }
    }

    public enum key {
    }
}
