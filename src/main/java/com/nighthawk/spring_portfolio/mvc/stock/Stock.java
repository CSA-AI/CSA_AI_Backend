package com.nighthawk.spring_portfolio.mvc.stock;

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
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(unique=true)
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd") //should probably change to time to be more accurate
    private Date time;

    @NotEmpty
    private Double cost;
    @NotEmpty
    private Integer shares;

    // Are we selling or buying the stock?
    // @NotEmpty
    // private String operation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String,Map<String, Object>> stats = new HashMap<>(); 

    // Essentially, we record who buys the stock (id), what stock they bought (name), cost of the share (cost), amount of the shares (shares), time of the transaction (time), and whether it was bought or sold (operation)
    public Stock(String name, Double cost, Integer shares, String operation, Date time) {
        this.name = name;
        this.cost = cost;
        this.shares = shares;
        this.time = time;
        // this.operation = operation;
    }

    // A custom getter to return cost of a transaction
    public double getTotalCost() {
        if (this.cost != null && this.shares != null) {
            return cost*(float)shares;
        }
        return -1;
    }

    // A custom getter to update total number of shares owned
    public double getTotalShares() {
        if (this.name != null && this.shares != null) {
            // this is placeholder for now, we should get existing shares and subtract
            return cost*(float)shares;
        }
        return -1;
    }

    // Initialize static test data 
    public static Stock[] init() {

        // basics of class construction
        Stock s1 = new Stock();
        s1.setName("AAPL");
        s1.setCost(188.91);
        s1.setShares(15);
        Date d;
        try {
            d = new SimpleDateFormat("MM-dd-yyyy").parse("02-06-2024");
            s1.setTime(d);
        } catch (ParseException e) {
            System.out.println("Date parse exception ======================");
            e.printStackTrace();
        }
        // p1.setOperation("buy"); <-- we can set this on frontend and adjust shares to + or -. If shares is 0 we assume update

        // Array definition and data initialization
        Stock stocks[] = {s1};
        return(stocks);
    }

    public static void main(String[] args) {
        // obtain Person from initializer
        Stock stocks[] = init();

        // iterate using "enhanced for loop"
        for( Stock stock : stocks) {
            System.out.println(stock);  // print object
        }
    }

}