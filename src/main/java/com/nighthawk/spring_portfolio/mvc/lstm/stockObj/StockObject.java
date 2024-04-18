package com.nighthawk.spring_portfolio.mvc.lstm.stockObj;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
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
@Convert(attributeName ="StockObject", converter = JsonType.class)
public class StockObject {


    private String sortingKey = "growth";

    // automatic unique identifier for Stock record
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // name, share cost, roles are key attributes to login and authentication
    @NotEmpty
    @Column(unique=true)
    private String ticker;

    // Next 7 days
    @NotEmpty
    @Column()
    private List<Double> predictions;

    @NotEmpty
    @Column()
    private Double predictionsPercentGrowth;

    @Column()
    private Double open;

    @Column()
    private Double high;

    @Column()
    private Double low;

    @Column()
    private Integer volume;

    // Are we selling or buying the stock?
    // @NotEmpty
    // private String operation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String,Map<String, Object>> stats = new HashMap<>(); 

    // Essentially, we record who buys the stock (id), what stock they bought (name), cost of the share (cost), amount of the shares (shares), time of the transaction (time), and whether it was bought or sold (operation)
    public StockObject(String ticker, List<Double> predictions, Double open, Double high, Double low, Integer volume) {
        this.ticker = ticker;
        this.predictionsPercentGrowth = 100*((predictions.get(predictions.size()-1)-predictions.get(0))/predictions.get(0));
        this.predictions = predictions;
        this.open = open;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }

    @Override
    public String toString() {
        if (this.sortingKey == "growth") {
            return ("("+ Double.toString(this.predictionsPercentGrowth) + ")");
        } else if (this.sortingKey == "open") {
            return ("("+ Double.toString(this.open) + ")");
        } else if (this.sortingKey == "high") {
            return "("+ Double.toString(this.high) + ")";
        } else if (this.sortingKey == "low") {
            return "("+ Double.toString(this.low) + ")";
        } else if (this.sortingKey == "volume") {
            return "("+ Integer.toString(this.volume) + ")";
        } else if (this.sortingKey == "ticker") {
            return "("+ this.ticker + ")";
        }
        return "Invalid Key";
    }

    // Initialize static test data 
    public static StockObject[] init() {

        // basics of class construction
        ArrayList<Double> s1Predictions = new ArrayList<Double>() {
            {
            add(142.99561458906226);
            add(142.3356784361008);
            add(141.50459141585998);
            add(140.79398885479813);
            add(140.47584765445805);
            add(140.23916870412728);
            add(140.11506605762875);
            add(140.0125428931567);
            add(139.9225949921048);
            add(139.32808085155676);
            add(138.7347217024006);
            }
        };
        ArrayList<Double> s2Predictions = new ArrayList<Double>() {
            {
            add(148.4352713167924);
            add(147.61149145957563);
            add(145.7654599777758);
            add(145.07767187569817);
            add(145.84473604843305);
            add(146.91743106217368);
            add(148.545180041023);
            add(149.84658193585875);
            add(150.23739673132195);
            add(149.77908850448154);
            add(148.89995122391826);
            }
        };
        ArrayList<Double> s3Predictions = new ArrayList<Double>() {
            {
            add(144.03847213115716);
            add(143.97275721147298);
            add(143.46220418652598);
            add(142.8551557002012);
            add(143.26608690917908);
            add(144.05321693272734);
            add(145.0575846909044);
            add(145.66746972928823);
            add(146.0591949340767);
            add(146.23636088218686);
            add(145.72402513257114);
            }
        };
        ArrayList<Double> s4Predictions = new ArrayList<Double>() {
            {
            add(420.57514359699985);
            add(424.45181401054697);
            add(424.708468663571);
            add(421.5919983479373);
            add(419.9645489769723);
            add(421.6644885934479);
            add(421.99125676262156);
            add(421.68257100737014);
            add(411.53663454488947);
            add(413.52836586737476);
            add(415.7230184885468);
            }
        };
        StockObject s1 = new StockObject("AAPL", s1Predictions, 189.3300018310547, 191.9499969482422, 188.82000732421875, 68741000);
        StockObject s2 = new StockObject("AMZN", s2Predictions, 187.42999267578125, 188.69000244140625, 183.0, 47982300);
        StockObject s3 = new StockObject("GOOGL", s3Predictions, 158.86000061035156, 159.24000549316406, 154.58999633789062, 27114700);
        StockObject s4 = new StockObject("UNH", s4Predictions, 442.0, 448.3500061035156, 441.989990234375, 5372400);
        StockObject stocks[] = {s1, s2, s3, s4};
        return stocks;
    }

    public static void main(String[] args) {
        // obtain Person from initializer
        StockObject stocks[] = init();

        // iterate using "enhanced for loop"
        for( StockObject stock : stocks) {
            System.out.println(stock);  // print object
        }
    }

}
