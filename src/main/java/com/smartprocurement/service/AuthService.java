package com.smartprocurement.service;

import com.smartprocurement.dto.AuthResponse;
import com.smartprocurement.dto.LoginRequest;
import com.smartprocurement.dto.RegisterRequest;
import com.smartprocurement.entity.Farmer;
import com.smartprocurement.entity.ProcurementCentre;
import com.smartprocurement.entity.Role;
import com.smartprocurement.entity.User;
import com.smartprocurement.entity.UserStatus;
import com.smartprocurement.exception.BadRequestException;
import com.smartprocurement.repository.CentreRepository;
import com.smartprocurement.repository.FarmerRepository;
import com.smartprocurement.repository.UserRepository;
import com.smartprocurement.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private CentreRepository centreRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone number already registered: " + request.getPhone());
        }

        Role userRole = request.getRole() != null ? request.getRole() : Role.FARMER;
        if (userRole == Role.ADMIN) {
            throw new BadRequestException("Admin registration is forbidden via public registration.");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        UserStatus initialStatus = (userRole == Role.OWNER) ? UserStatus.PENDING : UserStatus.ACTIVE;

        ProcurementCentre assignedCentre = null;
        if (userRole == Role.OWNER) {
            assignedCentre = centreRepository.findAll().stream().findFirst().orElseGet(() -> {
                ProcurementCentre newCentre = ProcurementCentre.builder()
                        .name("ABC Procurement Centre")
                        .location("Kondapur Main Road, Medak")
                        .latitude(17.3912)
                        .longitude(78.4920)
                        .totalCapacity(1000.0)
                        .currentLoad(0.0)
                        .build();
                return centreRepository.save(newCentre);
            });
        }

        User user = User.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .password(encodedPassword)
                .role(userRole)
                .status(initialStatus)
                .centre(assignedCentre)
                .build();
        userRepository.save(user);

        Long farmerId = null;
        if (userRole == Role.FARMER) {
            Farmer farmer = Farmer.builder()
                    .name(request.getName())
                    .phone(request.getPhone())
                    .password(encodedPassword)
                    .language(request.getLanguage() != null ? request.getLanguage() : "Telugu")
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .build();
            Farmer savedFarmer = farmerRepository.save(farmer);
            farmerId = savedFarmer.getFarmerId();
        }

        String token = (initialStatus == UserStatus.ACTIVE)
                ? jwtUtils.generateToken(user.getPhone(), user.getRole().name())
                : null;

        String msg = (initialStatus == UserStatus.PENDING)
                ? "Mandi Owner registration submitted successfully. Your account is PENDING approval by District Administrator."
                : "User registered successfully";

        return AuthResponse.builder()
                .token(token)
                .message(msg)
                .userId(user.getId())
                .farmerId(farmerId)
                .centreId(assignedCentre != null ? assignedCentre.getCentreId() : null)
                .centreName(assignedCentre != null ? assignedCentre.getName() : null)
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BadRequestException("Invalid phone number or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid phone number or password");
        }

        if (user.getRole() == Role.OWNER) {
            if (user.getStatus() == UserStatus.PENDING) {
                throw new BadRequestException("Your Mandi Owner account is PENDING approval by District Administrator.");
            }
            if (user.getStatus() == UserStatus.REJECTED) {
                throw new BadRequestException("Your Mandi Owner registration request was REJECTED by District Administrator.");
            }
        }

        Long farmerId = null;
        if (user.getRole() == Role.FARMER) {
            Farmer farmer = farmerRepository.findByPhone(user.getPhone()).orElse(null);
            if (farmer != null) {
                farmerId = farmer.getFarmerId();
            }
        }

        ProcurementCentre assignedCentre = user.getCentre();
        if (user.getRole() == Role.OWNER && assignedCentre == null) {
            assignedCentre = centreRepository.findAll().stream().findFirst().orElse(null);
            if (assignedCentre != null) {
                user.setCentre(assignedCentre);
                userRepository.save(user);
            }
        }

        String token = jwtUtils.generateToken(user.getPhone(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .message("Login successful")
                .userId(user.getId())
                .farmerId(farmerId)
                .centreId(assignedCentre != null ? assignedCentre.getCentreId() : null)
                .centreName(assignedCentre != null ? assignedCentre.getName() : null)
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
