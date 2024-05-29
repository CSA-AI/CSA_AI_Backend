package com.nighthawk.spring_portfolio.mvc.person;

public class AdjustAccountValueRequest {
    private String email;
    private String classCode;
    private double newAccountValue;

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

    public double getNewAccountValue() {
        return newAccountValue;
    }

    public void setNewAccountValue(double newAccountValue) {
        this.newAccountValue = newAccountValue;
    }
}
