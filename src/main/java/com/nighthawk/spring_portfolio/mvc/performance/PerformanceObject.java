package com.nighthawk.spring_portfolio.mvc.performance;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
        List<PerformanceObject> sortedList = new ArrayList<>(Collections.singletonList(this));
        sortedList.sort(Comparator.naturalOrder());
        return sortedList.iterator();
    }

    public static PerformanceIterator init() {
        PerformanceObject p1 = new PerformanceObject("Alice", 1, 10000.0, 5.0, "A");
        PerformanceObject p2 = new PerformanceObject("Bob", 2, 8000.0, 3.0, "B");
        PerformanceObject[] performances = {p1, p2};
        ArrayList<PerformanceObject> performanceList = new ArrayList<>(Arrays.asList(performances));
        return new PerformanceIterator(performanceList);
    }

    public static void main(String[] args) {
        PerformanceIterator performances = init();

        for (PerformanceObject performance : performances) {
            System.out.println(performance);
        }

        performances.setKeyType(KeyType.rankNumber);
        performances.mergeSort(0, performances.size() - 1);
        System.out.println();

        for (PerformanceObject performance : performances) {
            System.out.println(performance);
        }
    }
}
