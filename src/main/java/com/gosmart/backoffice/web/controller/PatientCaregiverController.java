package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.dto.PatientCaregiverSaveRequest;
import com.gosmart.backoffice.dto.PatientCaregiverView;
import com.gosmart.backoffice.service.CaregiverService;
import com.gosmart.backoffice.service.MedicalProviderService;
import com.gosmart.backoffice.service.PatientCaregiverService;
import com.gosmart.backoffice.service.PatientRegistrationService;
import com.gosmart.backoffice.service.ProtectedPageModelService;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PatientCaregiverController {
    private final ProtectedPageModelService protectedPageModelService;
    private final MedicalProviderService medicalProviderService;
    private final PatientRegistrationService patientRegistrationService;
    private final CaregiverService caregiverService;
    private final PatientCaregiverService patientCaregiverService;

    public PatientCaregiverController(
            ProtectedPageModelService protectedPageModelService,
            MedicalProviderService medicalProviderService,
            PatientRegistrationService patientRegistrationService,
            CaregiverService caregiverService,
            PatientCaregiverService patientCaregiverService
    ) {
        this.protectedPageModelService = protectedPageModelService;
        this.medicalProviderService = medicalProviderService;
        this.patientRegistrationService = patientRegistrationService;
        this.caregiverService = caregiverService;
        this.patientCaregiverService = patientCaregiverService;
    }

    @GetMapping("/patient/patient-caregiver")
    public String patientCaregiverPage(HttpServletRequest request, Model model) {
        protectedPageModelService.apply(model, request, null);
        model.addAttribute("medicalProviders", medicalProviderService.findAll().stream()
                .filter(provider -> "A".equalsIgnoreCase(provider.getStatus()))
                .toList());
        model.addAttribute("patients", patientRegistrationService.findAll().stream()
                .filter(patient -> "A".equalsIgnoreCase(patient.getStatus()))
                .toList());
        model.addAttribute("caregivers", caregiverService.findAll().stream()
                .filter(caregiver -> "A".equalsIgnoreCase(caregiver.getStatus()))
                .toList());
        model.addAttribute("patientCaregivers", patientCaregiverService.findAllCurrentAssignments());
        return "patient-caregiver";
    }

    @PostMapping("/patient/patient-caregiver/save")
    @ResponseBody
    public ResponseEntity<?> savePatientCaregiver(
            @RequestBody PatientCaregiverSaveRequest request,
            HttpServletRequest httpRequest
    ) {
        String currentUserId = (String) httpRequest.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        try {
            Optional<PatientCaregiverView> savedView = patientCaregiverService.saveAssignments(request, currentUserId);
            if (savedView.isPresent()) {
                return ResponseEntity.ok(savedView.get());
            }
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/patient/patient-caregiver/{id}")
    @ResponseBody
    public void deletePatientCaregiver(@PathVariable Integer id, HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        patientCaregiverService.deleteAssignmentGroup(id, currentUserId);
    }
}
