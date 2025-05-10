package com.hackathon.finwise.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.finwise.model.*;
import com.hackathon.finwise.repository.UserProfileRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserProfileRepository userRepo;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${marketaux.api.key}")
    private String marketauxApiKey;

/*    public List<Investment> getFinancialNews() {
        String url = "https://api.marketaux.com/v1/news/all?categories=business,finance,markets&language=en&api_token=" + marketauxApiKey;

        ResponseEntity<MarketauxResponse> response = restTemplate.getForEntity(url, MarketauxResponse.class);

        if (response.getBody() != null && response.getBody().getData() != null) {
            return response.getBody().getData().stream()
                    .map(article -> new Investment(
                            article.getTitle(),
                            "Financial News",
                            article.getDescription(),
                            0.0
                    ))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }*/

    public InvestmentGoalPlan calculateGoalPlan(InvestmentGoalRequest req, String username) {
        UserProfile user = getUserByUsername(username);

        double monthlyIncome = user.getMonthlyIncome();
        double monthlySavingsNeeded = req.getTargetAmount() / (req.getYears() * 12);
        RiskLevel risk = monthlySavingsNeeded < (monthlyIncome * 0.1) ? RiskLevel.LOW :
                monthlySavingsNeeded < (monthlyIncome * 0.3) ? RiskLevel.MEDIUM : RiskLevel.HIGH;

        List<Investment> suggestions = getInvestmentSuggestionsByRisk(risk);

        return new InvestmentGoalPlan(req.getGoal(), req.getTargetAmount(), req.getYears(), monthlySavingsNeeded, risk, suggestions);
    }

    public List<Investment> getFinancialNews() {
    String url = "https://api.marketaux.com/v1/news/all?categories=business,finance,markets&language=en&api_token=" + marketauxApiKey;

    ResponseEntity<MarketauxResponse> response = restTemplate.getForEntity(url, MarketauxResponse.class);

    if (response.getBody() != null && response.getBody().getData() != null) {
        List<MarketauxArticle> articles = response.getBody().getData();

        // Shuffle the articles to give variety
        Collections.shuffle(articles);

        // Limit to top 5 (optional)
        List<MarketauxArticle> limitedArticles = articles.stream()
                .limit(5)
                .collect(Collectors.toList());

        return limitedArticles.stream()
                .map(article -> new Investment(
                        article.getTitle(),
                        "Financial News",
                        article.getDescription(),
                        4
                ))
                .collect(Collectors.toList());
    }
    return new ArrayList<>();
}




    public UserProfile createUser(UserProfile user){
        if (user.getLastInvestmentDate() == null) {
            user.setLastInvestmentDate(LocalDate.now());
        }
        return userRepo.save(user);
    }



    public UserProfile getUser(Long id){
        Optional<UserProfile> user = userRepo.findById(id);
        return user.orElseThrow(()-> new RuntimeException("User Not Found"));
    }

    public UserProfile updateUser(Long id, UserProfile updated) {
        UserProfile user = getUser(id); // Fetch user
        user.setMonthlyIncome(updated.getMonthlyIncome());
        user.setMonthlyExpense(updated.getMonthlyExpense());
        user.setRiskLevel(updated.getRiskLevel());
        return userRepo.save(user);
    }

    public List<Investment> getInvestmentSuggestions(Long userId) {
        UserProfile userProfile = getUser(userId); // Fetch user
        RiskLevel riskLevel = userProfile.getRiskLevel(); // Get user's risk level

        return getInvestmentSuggestionsByRisk(riskLevel);
    }

    // Method to return suggestions based on risk level
    private List<Investment> getInvestmentSuggestionsByRisk(RiskLevel riskLevel) {
        List<Investment> investments = new ArrayList<>();

        switch (riskLevel) {
            case LOW:
                investments.add(new Investment("Government Bonds", "Low Risk", "Government-backed bonds with low returns.", 3.0));
                investments.add(new Investment("Blue-Chip Stocks", "Low Risk", "Stable companies with consistent returns.", 5.0));
                break;

            case MEDIUM:
                investments.add(new Investment("Index Funds", "Medium Risk", "Diversified stock market index funds.", 7.0));
                investments.add(new Investment("Dividend Stocks", "Medium Risk", "Stocks that pay regular dividends.", 6.0));
                break;

            case HIGH:
                investments.add(new Investment("Tech Stocks", "High Risk", "High-growth technology stocks.", 15.0));
                investments.add(new Investment("Cryptocurrency", "High Risk", "Investing in volatile cryptocurrencies like Bitcoin or Ethereum.", 20.0));
                break;

            default:
                throw new IllegalArgumentException("Invalid risk level: " + riskLevel);
        }

        return investments;
    }

    public List<Investment> getDipSuggestions(Long userId) {
        UserProfile user = getUser(userId);
        List<StockData> stocks = loadStockDataFromJson();

        // DIP = currentPrice < averagePrice
        List<StockData> dipped = stocks.stream()
                .filter(stock -> stock.getCurrentPrice() < stock.getAveragePrice())
                .collect(Collectors.toList());

        // Filter based on risk level (just an example logic)
        RiskLevel risk = user.getRiskLevel();
        double threshold;
        switch (risk) {
            case LOW: threshold = 0.05; break;   // max 5% dip
            case MEDIUM: threshold = 0.15; break; // max 15% dip
            case HIGH: threshold = 1.0; break;    // no limit
            default: threshold = 0.1;
        }

        List<Investment> dipInvestments = dipped.stream()
                .filter(stock -> {
                    double dipPercent = (stock.getAveragePrice() - stock.getCurrentPrice()) / stock.getAveragePrice();
                    return dipPercent <= threshold;
                })
                .map(stock -> new Investment(
                        stock.getTicker(),
                        "Dip Opportunity",
                        "Current price below average. Good time to buy.",
                        stock.getCurrentPrice()
                ))
                .collect(Collectors.toList());

        return dipInvestments;
    }

    private List<StockData> loadStockDataFromJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(
                    new ClassPathResource("mock_stock_data.json").getInputStream(),
                    new TypeReference<List<StockData>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to load stock data", e);
        }
    }
    public UserProfile getUserByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found by username: " + username));
    }
    public List<Investment> getInvestmentSuggestionsByUsername(String username) {
        UserProfile user = getUserByUsername(username);
        return getInvestmentSuggestionsByRisk(user.getRiskLevel());
    }
    public List<Investment> getDipSuggestionsByUsername(String username) {
        UserProfile user = getUserByUsername(username);
        return getDipSuggestions(user.getId());
    }
}
