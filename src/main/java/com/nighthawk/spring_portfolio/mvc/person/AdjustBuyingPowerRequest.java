package com.nighthawk.spring_portfolio.mvc.person;

public class AdjustBuyingPowerRequest {
    private String email;
    private String classCode;
    private double newBuyingPower;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public double getNewBuyingPower() {
        return newBuyingPower;
    }

    public void setNewBuyingPower(double newBuyingPower) {
        this.newBuyingPower = newBuyingPower;
    }
}
