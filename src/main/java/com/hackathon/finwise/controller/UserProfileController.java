package com.hackathon.finwise.controller;


import com.hackathon.finwise.model.Investment;
import com.hackathon.finwise.model.UserProfile;
import com.hackathon.finwise.repository.UserProfileRepository;
import com.hackathon.finwise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userProfileService;
    private final UserProfileRepository userProfileRepository;

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


    @DeleteMapping
    public void deleteAll(){
        userProfileRepository.deleteAll();;

    }

}
