package com.nighthawk.spring_portfolio.mvc.aslAI;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String predictionData;
    private LocalDateTime timestamp;

    // Constructors, Getters, and Setters
    public Prediction() {}

    public Prediction(String predictionData, LocalDateTime timestamp) {
        this.predictionData = predictionData;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPredictionData() { return predictionData; }
    public void setPredictionData(String predictionData) { this.predictionData = predictionData; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
