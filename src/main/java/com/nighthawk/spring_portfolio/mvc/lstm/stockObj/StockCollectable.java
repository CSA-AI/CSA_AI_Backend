package com.nighthawk.spring_portfolio.mvc.lstm.stockObj;

import java.util.List;
import java.util.ArrayList;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import com.vladmihalcea.hibernate.type.json.JsonType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Convert(attributeName ="StockObject", converter = JsonType.class)
public abstract class StockCollectable implements Comparable<StockCollectable> {
    public final String masterType = "StockCollectable";
	private String type;

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

    public interface KeyTypes {
		String name();
	}
	protected abstract KeyTypes getKey();

    public String getMasterType() {
		return masterType;
	}

	// getter
	public String getType() {
		return type;
	}

	// setter
	public void setType(String type) {
		this.type = type;
    }

    // this method is used to establish key order
	public abstract String toString();

	// this method is used to compare toString of objects
	public int compareTo(StockCollectable obj) {
		return this.toString().compareTo(obj.toString());
	}

    // Essentially, we record who buys the stock (id), what stock they bought (name), cost of the share (cost), amount of the shares (shares), time of the transaction (time), and whether it was bought or sold (operation)
    public StockCollectable(String ticker, List<Double> predictions, Double open, Double high, Double low, Integer volume) {
        this.ticker = ticker;
        this.predictionsPercentGrowth = 100*((predictions.get(predictions.size()-1)-predictions.get(0))/predictions.get(0));
        this.predictions = predictions;
        this.open = open;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }
}

