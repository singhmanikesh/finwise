package com.hackathon.finwise.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String name;
    private String email;
    @JsonIgnore
    private String pass;
    private int mobNumber;
    private double monthlyIncome;
    private double monthlyExpense;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel; // LOW, MEDIUM, HIGH
    private LocalDate lastInvestmentDate;
}
