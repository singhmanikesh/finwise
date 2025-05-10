package com.hackathon.finwise.model;

import lombok.Data;

@Data
public class InvestmentGoalRequest {
    private String goal;
    private double targetAmount;
    private int years;
}
