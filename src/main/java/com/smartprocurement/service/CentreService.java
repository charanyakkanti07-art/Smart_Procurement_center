package com.smartprocurement.service;

import com.smartprocurement.dto.CapacityUpdateRequest;
import com.smartprocurement.dto.CentreDTO;
import com.smartprocurement.entity.CentreStatus;
import com.smartprocurement.entity.ProcurementCentre;
import com.smartprocurement.exception.ResourceNotFoundException;
import com.smartprocurement.repository.CentreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CentreService {

    @Autowired
    private CentreRepository centreRepository;

    @Transactional
    public CentreDTO createCentre(ProcurementCentre centre) {
        if (centre.getCurrentLoad() == null) {
            centre.setCurrentLoad(0.0);
        }
        if (centre.getStatus() == null) {
            centre.setStatus(CentreStatus.ACTIVE);
        }
        ProcurementCentre saved = centreRepository.save(centre);
        return mapToDTO(saved);
    }

    public List<CentreDTO> getAllCentres() {
        return centreRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CentreDTO getCentreById(Long id) {
        ProcurementCentre centre = centreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement Centre not found with id: " + id));
        return mapToDTO(centre);
    }

    @Transactional
    public CentreDTO updateCapacity(Long id, CapacityUpdateRequest request) {
        ProcurementCentre centre = centreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement Centre not found with id: " + id));

        centre.setTotalCapacity(request.getTotalCapacity());
        if (centre.getCurrentLoad() >= centre.getTotalCapacity()) {
            centre.setStatus(CentreStatus.OVERLOADED);
        } else {
            centre.setStatus(CentreStatus.ACTIVE);
        }

        ProcurementCentre updated = centreRepository.save(centre);
        return mapToDTO(updated);
    }

    public CentreDTO mapToDTO(ProcurementCentre centre) {
        return CentreDTO.builder()
                .centreId(centre.getCentreId())
                .name(centre.getName())
                .location(centre.getLocation())
                .latitude(centre.getLatitude())
                .longitude(centre.getLongitude())
                .totalCapacity(centre.getTotalCapacity())
                .currentLoad(centre.getCurrentLoad())
                .availableCapacity(centre.getAvailableCapacity())
                .status(centre.getStatus())
                .build();
    }
}
