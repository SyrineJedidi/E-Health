package com.ehealth.prescription.repository;

import com.ehealth.prescription.model.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long> {}
