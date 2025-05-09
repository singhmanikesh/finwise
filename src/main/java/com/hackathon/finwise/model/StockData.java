package com.hackathon.finwise.model;

import lombok.Data;

@Data
public class StockData {
    private String ticker;
    private double currentPrice;
    private double averagePrice;
}
