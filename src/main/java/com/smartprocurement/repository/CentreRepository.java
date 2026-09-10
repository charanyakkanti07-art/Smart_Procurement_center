package com.smartprocurement.repository;

import com.smartprocurement.entity.ProcurementCentre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentreRepository extends JpaRepository<ProcurementCentre, Long> {
}
