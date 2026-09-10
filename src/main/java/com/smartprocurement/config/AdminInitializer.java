package com.smartprocurement.config;

import com.smartprocurement.entity.Role;
import com.smartprocurement.entity.User;
import com.smartprocurement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String adminPhone = "9999999999";
        if (userRepository.findByPhone(adminPhone).isEmpty()) {
            User admin = User.builder()
                    .name("District Administrator")
                    .phone(adminPhone)
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println(">>> Initialized default Admin Account: Phone=" + adminPhone + " [Role=ADMIN]");
        }
    }
}
