package com.hackathon.finwise.model;

import lombok.Data;
import java.util.List;

@Data
public class MarketauxResponse {
    private List<MarketauxArticle> data;
}
