package org.example.back_challengeai.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.back_challengeai.entity.User;
import org.example.back_challengeai.entity.UserPreferences;
import org.example.back_challengeai.repository.UserPreferencesRepository;
import org.example.back_challengeai.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;

    public User registerUser(String email, String password) {

        if (userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Email deja utilise");
        }

        User user = User.builder()
                .email(email)
                .password(password)
                .build();

        User savedUser = userRepository.save(user);

         UserPreferences prefs = UserPreferences.builder()
             .user(savedUser)
             .dailyTime(10)
             .interests(new ArrayList<>())
             .goals(new ArrayList<>())
             .build();

        userPreferencesRepository.save(prefs);

       return savedUser;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
    }

    public UserPreferences updatePreferences(UUID userId, List<String> interests, List<String> goals, Integer dailyTime) {

        UserPreferences prefs = userPreferencesRepository.findByUser_Id(userId).orElseThrow(()->new RuntimeException("Preferences not found"));

        if(interests != null){
            prefs.setInterests(interests);
        }
        if(goals != null){
            prefs.setGoals(goals);
        }
        if (dailyTime != null){
            prefs.setDailyTime(dailyTime);
        }

        return userPreferencesRepository.save(prefs);
    }
}
