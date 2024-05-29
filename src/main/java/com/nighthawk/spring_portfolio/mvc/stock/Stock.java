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

import com.nighthawk.spring_portfolio.mvc.person.ClassCode;
import com.vladmihalcea.hibernate.type.json.JsonType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

    @NotEmpty
    private String classCode;

    // Essentially, we record who buys the stock (id), what stock they bought (name), cost of the share (cost), amount of the shares (shares), time of the transaction (time), and whether it was bought or sold (operation)
    public Stock(String name, String email, String operation, Double cost, Integer shares, Double totalCost, Double percentChange, LocalDateTime time, String classCode) {
        this.name = name;
        this.email = email;
        this.operation = operation;
        this.cost = cost;
        this.shares = shares;
        this.totalCost = totalCost;
        this.percentChange = percentChange;
        this.time = time;
        this.classCode = classCode;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy'T'HH:mm:ss");

        // Example of class construction
        Stock s1 = new Stock();
        s1.setName("AAPL");
        s1.setCost(188.91);
        s1.setShares(15);
        LocalDateTime d1 = LocalDateTime.parse("02-06-2024T12:00:00", formatter);
        s1.setTime(d1);
        s1.setEmail("test@gmail.com");
        s1.setOperation("buy");
        s1.setTotalCost(s1.calculateTotalCost());
        s1.setClassCode("CSA-AI");

        Stock s2 = new Stock();
        s2.setName("GOOGL");
        s2.setCost(2800.50);
        s2.setShares(5);
        LocalDateTime d2 = LocalDateTime.parse("03-01-2024T14:30:00", formatter);
        s2.setTime(d2);
        s2.setEmail("test@gmail.com");
        s2.setOperation("buy");
        s2.setTotalCost(s2.calculateTotalCost());
        s2.setClassCode("CSA-AI");

        Stock s3 = new Stock();
        s3.setName("MSFT");
        s3.setCost(320.45);
        s3.setShares(10);
        LocalDateTime d3 = LocalDateTime.parse("03-15-2024T10:15:00", formatter);
        s3.setTime(d3);
        s3.setEmail("test@gmail.com");
        s3.setOperation("buy");
        s3.setTotalCost(s3.calculateTotalCost());
        s3.setClassCode("CSA-AI");

        Stock s4 = new Stock();
        s4.setName("AMZN");
        s4.setCost(3450.75);
        s4.setShares(2);
        LocalDateTime d4 = LocalDateTime.parse("04-20-2024T16:45:00", formatter);
        s4.setTime(d4);
        s4.setEmail("test@gmail.com");
        s4.setOperation("buy");
        s4.setTotalCost(s4.calculateTotalCost());
        s4.setClassCode("CSA-AI");

        Stock s5 = new Stock();
        s5.setName("AAPL");
        s5.setCost(200.00);
        s5.setShares(5);
        LocalDateTime d5 = LocalDateTime.parse("05-01-2024T11:30:00", formatter);
        s5.setTime(d5);
        s5.setEmail("test@gmail.com");
        s5.setOperation("sell");
        s5.setTotalCost(s5.calculateTotalCost());
        s5.setClassCode("CSA-AI");

        Stock s6 = new Stock();
        s6.setName("NFLX");
        s6.setCost(540.30);
        s6.setShares(7);
        LocalDateTime d6 = LocalDateTime.parse("06-10-2024T09:00:00", formatter);
        s6.setTime(d6);
        s6.setEmail("test@gmail.com");
        s6.setOperation("buy");
        s6.setTotalCost(s6.calculateTotalCost());
        s6.setClassCode("CSA-AI");

        Stock s7 = new Stock();
        s7.setName("FB");
        s7.setCost(370.15);
        s7.setShares(18);
        LocalDateTime d7 = LocalDateTime.parse("07-25-2024T13:45:00", formatter);
        s7.setTime(d7);
        s7.setEmail("test@gmail.com");
        s7.setOperation("buy");
        s7.setTotalCost(s7.calculateTotalCost());
        s7.setClassCode("CSA-AI");

        Stock s8 = new Stock();
        s8.setName("NFLX");
        s8.setCost(550.00);
        s8.setShares(4);
        LocalDateTime d8 = LocalDateTime.parse("08-01-2024T10:00:00", formatter);
        s8.setTime(d8);
        s8.setEmail("test@gmail.com");
        s8.setOperation("sell");
        s8.setTotalCost(s8.calculateTotalCost());
        s8.setClassCode("CSA-AI");

        Stock s9 = new Stock();
        s9.setName("NVDA");
        s9.setCost(600.75);
        s9.setShares(6);
        LocalDateTime d9 = LocalDateTime.parse("09-15-2024T14:00:00", formatter);
        s9.setTime(d9);
        s9.setEmail("test@gmail.com");
        s9.setOperation("buy");
        s9.setTotalCost(s9.calculateTotalCost());
        s9.setClassCode("CSA-AI");

        Stock s10 = new Stock();
        s10.setName("TSLA");
        s10.setCost(700.50);
        s10.setShares(10);
        LocalDateTime d10 = LocalDateTime.parse("10-10-2024T11:45:00", formatter);
        s10.setTime(d10);
        s10.setEmail("test@gmail.com");
        s10.setOperation("buy");
        s10.setTotalCost(s10.calculateTotalCost());
        s10.setClassCode("CSA-AI");

        Stock s11 = new Stock();
        s11.setName("NVDA");
        s11.setCost(610.00);
        s11.setShares(3);
        LocalDateTime d11 = LocalDateTime.parse("11-20-2024T16:30:00", formatter);
        s11.setTime(d11);
        s11.setEmail("test@gmail.com");
        s11.setOperation("sell");
        s11.setTotalCost(s11.calculateTotalCost());
        s11.setClassCode("CSA-AI");

        Stock s12 = new Stock();
        s12.setName("IBM");
        s12.setCost(125.50);
        s12.setShares(20);
        LocalDateTime d12 = LocalDateTime.parse("01-10-2024T10:00:00", formatter);
        s12.setTime(d12);
        s12.setEmail("test@gmail.com");
        s12.setOperation("buy");
        s12.setTotalCost(s12.calculateTotalCost());
        s12.setClassCode("AGY3T7");

        Stock s13 = new Stock();
        s13.setName("ORCL");
        s13.setCost(75.30);
        s13.setShares(25);
        LocalDateTime d13 = LocalDateTime.parse("02-15-2024T09:30:00", formatter);
        s13.setTime(d13);
        s13.setEmail("test@gmail.com");
        s13.setOperation("buy");
        s13.setTotalCost(s13.calculateTotalCost());
        s13.setClassCode("AGY3T7");

        Stock s14 = new Stock();
        s14.setName("AAPL");
        s14.setCost(195.00);
        s14.setShares(10);
        LocalDateTime d14 = LocalDateTime.parse("03-20-2024T12:00:00", formatter);
        s14.setTime(d14);
        s14.setEmail("test@gmail.com");
        s14.setOperation("sell");
        s14.setTotalCost(s14.calculateTotalCost());
        s14.setClassCode("AGY3T7");

        Stock s15 = new Stock();
        s15.setName("GOOGL");
        s15.setCost(2750.00);
        s15.setShares(3);
        LocalDateTime d15 = LocalDateTime.parse("04-25-2024T14:00:00", formatter);
        s15.setTime(d15);
        s15.setEmail("test@gmail.com");
        s15.setOperation("buy");
        s15.setTotalCost(s15.calculateTotalCost());
        s15.setClassCode("AGY3T7");

        Stock s16 = new Stock();
        s16.setName("MSFT");
        s16.setCost(310.00);
        s16.setShares(8);
        LocalDateTime d16 = LocalDateTime.parse("05-30-2024T11:30:00", formatter);
        s16.setTime(d16);
        s16.setEmail("test@gmail.com");
        s16.setOperation("buy");
        s16.setTotalCost(s16.calculateTotalCost());
        s16.setClassCode("AGY3T7");

        Stock s17 = new Stock();
        s17.setName("ORCL");
        s17.setCost(80.00);
        s17.setShares(10);
        LocalDateTime d17 = LocalDateTime.parse("06-20-2024T09:45:00", formatter);
        s17.setTime(d17);
        s17.setEmail("test@gmail.com");
        s17.setOperation("sell");
        s17.setTotalCost(s17.calculateTotalCost());
        s17.setClassCode("AGY3T7");

        Stock s18 = new Stock();
        s18.setName("IBM");
        s18.setCost(130.00);
        s18.setShares(15);
        LocalDateTime d18 = LocalDateTime.parse("07-15-2024T10:30:00", formatter);
        s18.setTime(d18);
        s18.setEmail("test@gmail.com");
        s18.setOperation("sell");
        s18.setTotalCost(s18.calculateTotalCost());
        s18.setClassCode("AGY3T7");

        Stock s19 = new Stock();
        s19.setName("TSLA");
        s19.setCost(710.00);
        s19.setShares(5);
        LocalDateTime d19 = LocalDateTime.parse("08-10-2024T13:00:00", formatter);
        s19.setTime(d19);
        s19.setEmail("test@gmail.com");
        s19.setOperation("buy");
        s19.setTotalCost(s19.calculateTotalCost());
        s19.setClassCode("AGY3T7");

        Stock s20 = new Stock();
        s20.setName("NFLX");
        s20.setCost(555.00);
        s20.setShares(7);
        LocalDateTime d20 = LocalDateTime.parse("09-05-2024T11:00:00", formatter);
        s20.setTime(d20);
        s20.setEmail("test@gmail.com");
        s20.setOperation("buy");
        s20.setTotalCost(s20.calculateTotalCost());
        s20.setClassCode("AGY3T7");

        Stock s21 = new Stock();
        s21.setName("FB");
        s21.setCost(375.00);
        s21.setShares(20);
        LocalDateTime d21 = LocalDateTime.parse("10-01-2024T09:15:00", formatter);
        s21.setTime(d21);
        s21.setEmail("test@gmail.com");
        s21.setOperation("sell");
        s21.setTotalCost(s21.calculateTotalCost());
        s21.setClassCode("AGY3T7");

        Stock s22 = new Stock();
        s22.setName("NVDA");
        s22.setCost(620.00);
        s22.setShares(8);
        LocalDateTime d22 = LocalDateTime.parse("11-01-2024T16:00:00", formatter);
        s22.setTime(d22);
        s22.setEmail("test@gmail.com");
        s22.setOperation("sell");
        s22.setTotalCost(s22.calculateTotalCost());
        s22.setClassCode("AGY3T7");

        Stock[] stocks = { s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12, s13, s14, s15, s16, s17, s18, s19, s20, s21, s22 };
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