package com.hackathon.finwise.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InvestmentGoalPlan {
    private String goal;
    private double targetAmount;
    private int years;
    private double monthlyContribution;
    private RiskLevel recommendedRisk;
    private List<Investment> investmentSuggestions;
}
