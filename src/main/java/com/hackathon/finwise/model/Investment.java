package com.hackathon.finwise.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Investment {
    private String name;
    private String category;
    private String description;
    private double averageReturn;
}
