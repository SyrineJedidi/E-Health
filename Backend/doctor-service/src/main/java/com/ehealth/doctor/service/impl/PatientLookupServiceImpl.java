package com.ehealth.doctor.service.impl;

import com.ehealth.doctor.client.PatientListResponse;
import com.ehealth.doctor.client.PatientServiceClient;
import com.ehealth.doctor.dto.PatientSummaryDTO;
import com.ehealth.doctor.service.PatientLookupService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientLookupServiceImpl implements PatientLookupService {

    private final PatientServiceClient patientServiceClient;

    @Override
    public List<PatientSummaryDTO> listPatientsViaPatientService() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null || attrs.getRequest().getHeader("Authorization") == null) {
            throw new IllegalStateException("Jeton d'authentification requis pour consulter les patients.");
        }
        try {
            PatientListResponse body = patientServiceClient.getAllPatients();
            if (body == null || body.getData() == null) {
                return Collections.emptyList();
            }
            return body.getData();
        } catch (FeignException.Unauthorized e) {
            log.warn("patient-service a refusé l'accès (401) : {}", e.getMessage());
            throw new IllegalStateException("Accès aux patients refusé : reconnectez-vous ou vérifiez vos droits.");
        } catch (FeignException.Forbidden e) {
            throw new IllegalStateException("Accès interdit au service patients.");
        } catch (FeignException e) {
            log.error("Erreur Feign vers patient-service : {}", e.status(), e);
            throw new IllegalStateException("Service patients temporairement indisponible.");
        }
    }
}
