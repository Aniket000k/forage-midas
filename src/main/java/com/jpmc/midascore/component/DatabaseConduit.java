package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConduit {

    @Autowired
    private UserRepository userRepository;

    public void saveUserToDatabase(UserRecord user) {
        try {
            userRepository.save(user);
            System.out.println("✅ User saved to database: " + user.getUsername());
        } catch (Exception e) {
            System.out.println("❌ Failed to save user to database: " + e.getMessage());
            // Add retry logic or error handling here
        }
    }
}