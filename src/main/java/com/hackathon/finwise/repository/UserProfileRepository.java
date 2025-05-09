package com.hackathon.finwise.repository;

import com.hackathon.finwise.model.UserProfile;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile,Long> {
    public UserProfile findUserProfileByemail(String email);
    Optional<UserProfile> findByUsername(String username);


}
