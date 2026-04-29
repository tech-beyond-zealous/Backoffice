package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.dto.PatientMedicalRecordSaveRequest;
import com.gosmart.backoffice.dto.UserFunctionPermission;
import com.gosmart.backoffice.service.MedicalProviderService;
import com.gosmart.backoffice.service.PatientMedicalRecordService;
import com.gosmart.backoffice.service.PatientRegistrationService;
import com.gosmart.backoffice.service.PermissionService;
import com.gosmart.backoffice.service.ProtectedPageModelService;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class PatientMedicalRecordController {
    private static final Logger log = LoggerFactory.getLogger(PatientMedicalRecordController.class);
    private static final String MEDICAL_RECORD_FUNCTION_PATH = "/patient/medical-record";

    private final ProtectedPageModelService protectedPageModelService;
    private final MedicalProviderService medicalProviderService;
    private final PatientRegistrationService patientRegistrationService;
    private final PatientMedicalRecordService patientMedicalRecordService;
    private final PermissionService permissionService;

    public PatientMedicalRecordController(
            ProtectedPageModelService protectedPageModelService,
            MedicalProviderService medicalProviderService,
            PatientRegistrationService patientRegistrationService,
            PatientMedicalRecordService patientMedicalRecordService,
            PermissionService permissionService
    ) {
        this.protectedPageModelService = protectedPageModelService;
        this.medicalProviderService = medicalProviderService;
        this.patientRegistrationService = patientRegistrationService;
        this.patientMedicalRecordService = patientMedicalRecordService;
        this.permissionService = permissionService;
    }

    @GetMapping("/patient/medical-record")
    public String patientMedicalRecordPage(HttpServletRequest request, Model model) {
        protectedPageModelService.apply(model, request, null);
        UserFunctionPermission permission =
                (UserFunctionPermission) request.getAttribute(AuthInterceptor.REQ_ATTR_PERMISSION);
        String currentUserId = requireCurrentUserId(request);
        var medicalProviders = medicalProviderService.findAccessibleByUserId(currentUserId);
        Set<Long> allowedProviderIds = medicalProviders.stream()
                .map(provider -> provider.getId().longValue())
                .collect(Collectors.toSet());

        model.addAttribute("medicalProviders", medicalProviders);
        model.addAttribute("patients", patientRegistrationService.findAll().stream()
                .filter(patient -> "A".equalsIgnoreCase(patient.getStatus()))
                .filter(patient -> patient.getMedicalProviderId() != null)
                .filter(patient -> allowedProviderIds.contains(patient.getMedicalProviderId()))
                .toList());
        try {
            Set<Integer> allowedProviderIdsInt = medicalProviders.stream()
                    .map(provider -> provider.getId())
                    .collect(Collectors.toSet());
            model.addAttribute("medicalRecords", patientMedicalRecordService.findAllActiveRecords().stream()
                    .filter(record -> record.getMedicalProviderId() != null)
                    .filter(record -> allowedProviderIdsInt.contains(record.getMedicalProviderId()))
                    .toList());
        } catch (RuntimeException ex) {
            log.error("Unable to load patient medical records page data", ex);
            model.addAttribute("medicalRecords", Collections.emptyList());
            model.addAttribute(
                    "medicalRecordLoadError",
                    "Medical record data could not be loaded. Please verify the database changes for patient medical records are applied."
            );
        }
        model.addAttribute("permission", permission);
        model.addAttribute("requestPath", request.getRequestURI());
        model.addAttribute("functionCode", request.getAttribute(AuthInterceptor.REQ_ATTR_FUNCTION_CODE));
        return "patient-medical-record";
    }

    @PostMapping("/patient/medical-record/save")
    @ResponseBody
    public ResponseEntity<?> savePatientMedicalRecord(
            @RequestBody PatientMedicalRecordSaveRequest request,
            HttpServletRequest httpRequest
    ) {
        String currentUserId = (String) httpRequest.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        if (!medicalProviderService.hasAccessibleProvider(currentUserId, request.getMedicalProviderId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this medical provider.");
        }
        UserFunctionPermission permission =
                permissionService.resolve(httpRequest, currentUserId, MEDICAL_RECORD_FUNCTION_PATH);
        boolean isEdit = request.getId() != null;
        if (isEdit && (permission == null || !permission.isEdit())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to edit medical records.");
        }
        if (!isEdit && (permission == null || !permission.isCreate())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to create medical records.");
        }

        try {
            return ResponseEntity.ok(patientMedicalRecordService.saveRecord(request, currentUserId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (RuntimeException ex) {
            log.error("Unable to save patient medical record", ex);
            String message = ex.getMessage() == null || ex.getMessage().isBlank()
                    ? "Unable to save medical record due to a server error."
                    : ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

    @DeleteMapping("/patient/medical-record/{id}")
    @ResponseBody
    public void deletePatientMedicalRecord(@PathVariable Integer id, HttpServletRequest request) {
        String currentUserId = requireCurrentUserId(request);
        permissionService.requireDelete(
                request,
                currentUserId,
                MEDICAL_RECORD_FUNCTION_PATH,
                "You do not have permission to delete medical records."
        );
        patientMedicalRecordService.deleteRecord(id, currentUserId);
    }

    private String requireCurrentUserId(HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        return currentUserId;
    }
}
