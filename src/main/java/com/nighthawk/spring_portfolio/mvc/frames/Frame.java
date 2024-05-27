package com.nighthawk.spring_portfolio.mvc.frames;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Frame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mnistData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMnistData() {
        return mnistData;
    }

    public void setMnistData(String mnistData) {
        this.mnistData = mnistData;
    }
}