package com.nighthawk.spring_portfolio.mvc.stockObj;

import java.util.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(name = "json", typeClass = JsonType.class)
public class StockObject extends StockCollectable implements Iterable<StockObject> {
    public enum KeyType implements KeyTypes {ticker, growth, open, high, low, volume}
    public static KeyTypes key = KeyType.growth;

    public void setOrder(KeyTypes key) { StockObject.key = key; }

    private String sortingKey = "growth";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Column(unique=true)
    private String ticker;

    @NotEmpty
    @Column()
    private List<Double> predictions;

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

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private Map<String, Map<String, Object>> stats = new HashMap<>();

    public StockObject(String ticker, List<Double> predictions, Double open, Double high, Double low, Integer volume) {
        this.ticker = ticker;
        this.predictionsPercentGrowth = 100 * ((predictions.get(predictions.size()-1) - predictions.get(0)) / predictions.get(0));
        this.predictions = predictions;
        this.open = open;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }

    @Override
    public String toString() {
        switch (StockObject.key) {
            case growth:
                return "(" + this.predictionsPercentGrowth + ")";
            case open:
                return "(" + this.open + ")";
            case high:
                return "(" + this.high + ")";
            case low:
                return "(" + this.low + ")";
            case volume:
                return "(" + this.volume + ")";
            case ticker:
                return "(" + this.ticker + ")";
            default:
                return "Invalid Key";
        }
    }

    @Override
    public int compareTo(StockCollectable stockObj) {
        if (KeyType.growth.equals(StockObject.key)) {
            return Double.compare(this.predictionsPercentGrowth, stockObj.getPredictionsPercentGrowth());
        }
        return this.toString().compareTo(stockObj.toString());
    }

    @Override
    protected KeyTypes getKey() {
        return StockObject.key;
    }

    @Override
    public Iterator<StockObject> iterator() {
        List<StockObject> sortedList = new ArrayList<>(Collections.singletonList(this));
        sortedList.sort(Comparator.naturalOrder());
        return sortedList.iterator();
    }

    public static StockObjectIterator init() {
        List<StockObject> stocks = Arrays.asList(
            new StockObject("AAPL", Arrays.asList(142.99561458906226, 142.3356784361008, 141.50459141585998, 140.79398885479813, 140.47584765445805, 140.23916870412728, 140.11506605762875, 140.0125428931567, 139.9225949921048, 139.32808085155676, 138.7347217024006), 189.33, 191.95, 188.82, 68741000),
            new StockObject("AMZN", Arrays.asList(148.4352713167924, 147.61149145957563, 145.7654599777758, 145.07767187569817, 145.84473604843305, 146.91743106217368, 148.545180041023, 149.84658193585875, 150.23739673132195, 149.77908850448154, 148.89995122391826), 187.43, 188.69, 183.0, 47982300),
            new StockObject("GOOGL", Arrays.asList(144.03847213115716, 143.97275721147298, 143.46220418652598, 142.8551557002012, 143.26608690917908, 144.05321693272734, 145.0575846909044, 145.66746972928823, 146.0591949340767, 146.23636088218686, 145.72402513257114), 158.86, 159.24, 154.59, 27114700),
            new StockObject("UNH", Arrays.asList(420.57514359699985, 424.45181401054697, 424.708468663571, 421.5919983479373, 419.9645489769723, 421.6644885934479, 421.99125676262156, 421.68257100737014, 411.53663454488947, 413.52836586737476, 415.7230184885468), 442.0, 448.35, 441.99, 5372400)
        );

        return new StockObjectIterator(new ArrayList<>(stocks));
    }

    public static void main(String[] args) {
        StockObjectIterator stocks = init();
        for (StockObject stock : stocks) {
            System.out.println(stock);
        }

        stocks.mergeSort(0, stocks.size() - 1);
        System.out.println();
        for (StockObject stock : stocks) {
            System.out.println(stock);
        }

        stocks.setKeyType(KeyType.ticker);
        stocks.mergeSort(0, stocks.size() - 1);
        System.out.println();
        for (StockObject stock : stocks) {
            System.out.println(stock);
        }
    }
}
