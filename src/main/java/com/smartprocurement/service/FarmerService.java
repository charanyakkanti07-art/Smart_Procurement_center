package com.smartprocurement.service;

import com.smartprocurement.dto.CropDTO;
import com.smartprocurement.dto.CropRequest;
import com.smartprocurement.dto.FarmerDTO;
import com.smartprocurement.dto.RegisterRequest;
import com.smartprocurement.entity.Crop;
import com.smartprocurement.entity.Farmer;
import com.smartprocurement.entity.Role;
import com.smartprocurement.entity.User;
import com.smartprocurement.exception.BadRequestException;
import com.smartprocurement.exception.ResourceNotFoundException;
import com.smartprocurement.repository.CropRepository;
import com.smartprocurement.repository.FarmerRepository;
import com.smartprocurement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FarmerService {

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CropRepository cropRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public FarmerDTO createFarmer(RegisterRequest request) {
        if (farmerRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Farmer already exists with phone: " + request.getPhone());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Farmer farmer = Farmer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .password(encodedPassword)
                .language(request.getLanguage() != null ? request.getLanguage() : "Telugu")
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        Farmer saved = farmerRepository.save(farmer);

        if (!userRepository.existsByPhone(request.getPhone())) {
            User user = User.builder()
                    .name(request.getName())
                    .phone(request.getPhone())
                    .password(encodedPassword)
                    .role(Role.FARMER)
                    .build();
            userRepository.save(user);
        }

        return mapToDTO(saved);
    }

    public FarmerDTO getFarmerById(Long id) {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + id));
        return mapToDTO(farmer);
    }

    @Transactional
    public FarmerDTO updateFarmer(Long id, RegisterRequest request) {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + id));

        if (request.getName() != null) farmer.setName(request.getName());
        if (request.getLanguage() != null) farmer.setLanguage(request.getLanguage());
        if (request.getLatitude() != null) farmer.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) farmer.setLongitude(request.getLongitude());

        Farmer updated = farmerRepository.save(farmer);
        return mapToDTO(updated);
    }

    @Transactional
    public FarmerDTO updateLanguage(Long id, String language) {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + id));

        if (language != null && !language.trim().isEmpty()) {
            farmer.setLanguage(language.trim());
        }

        Farmer updated = farmerRepository.save(farmer);
        return mapToDTO(updated);
    }

    @Transactional
    public CropDTO addCrop(Long farmerId, CropRequest request) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id: " + farmerId));

        Crop crop = Crop.builder()
                .cropType(request.getCropType())
                .quantity(request.getQuantity())
                .farmer(farmer)
                .build();

        Crop saved = cropRepository.save(crop);

        return CropDTO.builder()
                .cropId(saved.getCropId())
                .cropType(saved.getCropType())
                .quantity(saved.getQuantity())
                .farmerId(farmer.getFarmerId())
                .build();
    }

    public List<CropDTO> getCropsByFarmer(Long farmerId) {
        if (!farmerRepository.existsById(farmerId)) {
            throw new ResourceNotFoundException("Farmer not found with id: " + farmerId);
        }
        return cropRepository.findByFarmerFarmerId(farmerId).stream()
                .map(crop -> CropDTO.builder()
                        .cropId(crop.getCropId())
                        .cropType(crop.getCropType())
                        .quantity(crop.getQuantity())
                        .farmerId(crop.getFarmer().getFarmerId())
                        .build())
                .collect(Collectors.toList());
    }

    private FarmerDTO mapToDTO(Farmer farmer) {
        return FarmerDTO.builder()
                .farmerId(farmer.getFarmerId())
                .name(farmer.getName())
                .phone(farmer.getPhone())
                .language(farmer.getLanguage())
                .latitude(farmer.getLatitude())
                .longitude(farmer.getLongitude())
                .createdAt(farmer.getCreatedAt())
                .build();
    }
}
