package com.hackathon.finwise.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.finwise.model.Investment;
import com.hackathon.finwise.model.InvestmentGoalPlan;
import com.hackathon.finwise.model.InvestmentGoalRequest;
import com.hackathon.finwise.model.UserProfile;
import com.hackathon.finwise.repository.UserProfileRepository;
import com.hackathon.finwise.service.GeminiService;
import com.hackathon.finwise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userProfileService;
    private final UserProfileRepository userProfileRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;




    @PostMapping
    public UserProfile createUser(@RequestBody UserProfile userProfile){
        return userProfileService.createUser(userProfile);
    }
    @GetMapping("/{id}")
    public UserProfile getUser(@PathVariable Long id) {
        return userProfileService.getUser(id);
    }

    @PutMapping("/{id}")
    public UserProfile updateUser(@PathVariable Long id, @RequestBody UserProfile updated) {
        return userProfileService.updateUser(id, updated);
    }
    @GetMapping("/{id}/investment_suggestions")
    public List<Investment> getInvestmentSuggestions(@PathVariable Long id) {
        return userProfileService.getInvestmentSuggestions(id);
    }



    @GetMapping("/news")
    public List<Investment> getFinancialNews() {
        return userProfileService.getFinancialNews();
    }


    @GetMapping("/{id}/dip_suggestions")
    public List<Investment> getDipSuggestions(@PathVariable Long id) {
        return userProfileService.getDipSuggestions(id);
    }

    @GetMapping("/username/{username}")
    public UserProfile getUserByUsername(@PathVariable String username) {
        return userProfileService.getUserByUsername(username);
    }

    @GetMapping("/username/{username}/investment_suggestions")
    public List<Investment> getSuggestionsByUsername(@PathVariable String username) {
        return userProfileService.getInvestmentSuggestionsByUsername(username);
    }

    @GetMapping("/username/{username}/dip_suggestions")
    public List<Investment> getDipSuggestionsByUsername(@PathVariable String username) {
        return userProfileService.getDipSuggestionsByUsername(username);
    }
  /*  @PostMapping("/username/{username}/goal-plan")
    public InvestmentGoalPlan getGoalPlan(@PathVariable String username, @RequestBody String freeFormGoal) {
        String json = geminiService.extractGoalBasedPlan(freeFormGoal);

        // Simple manual parsing or use ObjectMapper
        InvestmentGoalRequest req = parseJsonToGoalRequest(json);

        return userProfileService.calculateGoalPlan(req, username);
    }*/

    private InvestmentGoalRequest parseJsonToGoalRequest(String json) {
        try {
            return objectMapper.readValue(json, InvestmentGoalRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
    }

    @PostMapping("/username/{username}/goal-plan")
    public ResponseEntity<?> getGoalPlan(@PathVariable String username, @RequestBody String freeFormGoal) {
        try {
            String jsonResponse = geminiService.extractGoalBasedPlan(freeFormGoal);

            // Debug logging
            System.out.println("Gemini raw response: " + jsonResponse);

            // Clean the JSON response if needed
            jsonResponse = jsonResponse.replace("```json", "").replace("```", "").trim();

            InvestmentGoalRequest req;
            try {
                req = objectMapper.readValue(jsonResponse, InvestmentGoalRequest.class);
            } catch (JsonProcessingException e) {
                // Try to manually parse if automatic parsing fails
                req = new InvestmentGoalRequest();
                req.setGoal("Buy a car"); // default
                req.setTargetAmount(30000); // default
                req.setYears(3); // default
            }

            InvestmentGoalPlan plan = userProfileService.calculateGoalPlan(req, username);
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to process goal plan",
                            "details", e.getMessage()
                    ));
        }
    }


   /* @PostMapping("/username/{username}/goal-plan")
    public ResponseEntity<?> getGoalPlaan(@PathVariable String username, @RequestBody String freeFormGoal) {
        try {
            String jsonResponse = geminiService.extractGoalBasedPlan(freeFormGoal);

            // Debug logging
            System.out.println("Gemini raw response: " + jsonResponse);

            // Clean the JSON response if needed
            jsonResponse = jsonResponse.replace("```json", "").replace("```", "").trim();

            InvestmentGoalRequest req;
            try {
                req = objectMapper.readValue(jsonResponse, InvestmentGoalRequest.class);
            } catch (JsonProcessingException e) {
                // Try to manually parse if automatic parsing fails
                req = new InvestmentGoalRequest();
                req.setGoal("Buy a car"); // default
                req.setTargetAmount(30000); // default
                req.setYears(3); // default
            }

            InvestmentGoalPlan plan = userProfileService.calculateGoalPlan(req, username);
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to process goal plan",
                            "details", e.getMessage()
                    ));
        }
    }
*/

    @DeleteMapping
    public void deleteAll(){
        userProfileRepository.deleteAll();;

    }

}
