package com.smartprocurement.config;

import com.smartprocurement.entity.ProcurementCentre;
import com.smartprocurement.entity.Role;
import com.smartprocurement.entity.User;
import com.smartprocurement.entity.UserStatus;

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

    @Autowired
    private com.smartprocurement.repository.CentreRepository centreRepository;


    @Autowired
    private com.smartprocurement.repository.FarmerRepository farmerRepository;

    @Override
    public void run(String... args) throws Exception {
        String adminPhone = "9999999999";
        if (userRepository.findByPhone(adminPhone).isEmpty()) {
            User admin = User.builder()
                    .name("District Administrator")
                    .phone(adminPhone)
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(admin);
            System.out.println(">>> Initialized default Admin Account: Phone=" + adminPhone + " [Role=ADMIN, Status=ACTIVE]");
        }

        String ownerPhone = "9876543211";
        if (userRepository.findByPhone(ownerPhone).isEmpty()) {
            User owner = User.builder()
                    .name("Mandi Owner (Ravi Kumar)")
                    .phone(ownerPhone)
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.OWNER)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(owner);
            System.out.println(">>> Initialized default Mandi Owner Account: Phone=" + ownerPhone + " [Role=OWNER, Status=ACTIVE]");
        }

        String farmerPhone = "9876543210";
        if (userRepository.findByPhone(farmerPhone).isEmpty()) {
            User farmer = User.builder()
                    .name("Ramesh Kumar")
                    .phone(farmerPhone)
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.FARMER)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(farmer);
            System.out.println(">>> Initialized default Farmer Account: Phone=" + farmerPhone + " [Role=FARMER, Status=ACTIVE]");
        }

        if (!farmerRepository.existsByPhone(farmerPhone)) {
            com.smartprocurement.entity.Farmer defaultFarmer = com.smartprocurement.entity.Farmer.builder()
                    .name("Ramesh Kumar")
                    .phone(farmerPhone)
                    .password(passwordEncoder.encode("123456"))
                    .language("Telugu")
                    .latitude(17.3850)
                    .longitude(78.4867)
                    .build();
            farmerRepository.save(defaultFarmer);
            System.out.println(">>> Initialized default Farmer Entity in farmerRepository: Phone=" + farmerPhone);
        }

        ProcurementCentre centre = centreRepository.findAll().stream().findFirst().orElseGet(() -> {
            ProcurementCentre defaultCentre = ProcurementCentre.builder()
                    .name("ABC Procurement Centre")
                    .location("Kondapur Main Road, Medak")
                    .latitude(17.3912)
                    .longitude(78.4920)
                    .totalCapacity(10000.0)
                    .currentLoad(0.0)
                    .status(com.smartprocurement.entity.CentreStatus.ACTIVE)
                    .build();
            return centreRepository.save(defaultCentre);
        });

        User ownerUser = userRepository.findByPhone(ownerPhone).orElse(null);
        if (ownerUser != null && ownerUser.getCentre() == null) {
            ownerUser.setCentre(centre);
            userRepository.save(ownerUser);
        }
        System.out.println(">>> Initialized default Procurement Centre: " + centre.getName() + " [ID=" + centre.getCentreId() + "]");
    }



}
