package com.nighthawk.spring_portfolio.mvc.stock;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/*
Stock is a POJO, Plain Old Java Object.
First set of annotations add functionality to POJO
--- @Setter @Getter @ToString @NoArgsConstructor @RequiredArgsConstructor
The last annotation connect to database
--- @Entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Convert(attributeName ="stock", converter = JsonType.class)
public class Stock {

    // automatic unique identifier for Person record
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // name, share cost, roles are key attributes to login and authentication
    @NotEmpty
    private String name;

    @Email
    @NotEmpty
    private String email;

    // Are we selling or buying the stock?
    @NotEmpty
    private String operation;

    @NotNull
    @Positive
    private Double cost;

    @NotNull
    @Positive
    private Integer shares;

    @NotNull
    @Positive
    private Double totalCost;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime time;

    private Double percentChange;

    // Essentially, we record who buys the stock (id), what stock they bought (name), cost of the share (cost), amount of the shares (shares), time of the transaction (time), and whether it was bought or sold (operation)
    public Stock(String name, String email, String operation, Double cost, Integer shares, Double totalCost, Double percentChange, LocalDateTime time) {
        this.name = name;
        this.email = email;
        this.operation = operation;
        this.cost = cost;
        this.shares = shares;
        this.totalCost = totalCost;
        this.percentChange = percentChange;
        this.time = time;
    }

    // A custom getter to return cost of a transaction
    public double calculateTotalCost() {
        if (this.cost != null && this.shares != null) {
            return this.cost * this.shares;
        }
        return -1;
    }

    public Double calculatePercentChange(Double sellPrice) {
        // Calculate the percentage change
        Double buyPrice = this.cost;
        return ((sellPrice - buyPrice) / buyPrice) * 100.0;
    }

    // Initialize static test data 
    public static Stock[] init() {
        // Example of class construction
        Stock s1 = new Stock();
        s1.setName("AAPL");
        s1.setCost(188.91);
        s1.setShares(15);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy'T'HH:mm:ss");
        LocalDateTime d = LocalDateTime.parse("02-06-2024T12:00:00", formatter);
        s1.setTime(d);
        s1.setEmail("example@example.com");
        s1.setOperation("buy");
        s1.setTotalCost(s1.calculateTotalCost()); // Set total cost
        // Array definition and data initialization
        Stock[] stocks = { s1 };
        return stocks;
    }

    public static void main(String[] args) {
        // Obtain Stock objects from initializer
        Stock[] stocks = init();

        // Iterate through stocks
        for(Stock stock : stocks) {
            // Print stock details
            System.out.println(stock);
            // Print total cost
            System.out.println("Total Cost: $" + stock.getTotalCost());
        }
    }

}