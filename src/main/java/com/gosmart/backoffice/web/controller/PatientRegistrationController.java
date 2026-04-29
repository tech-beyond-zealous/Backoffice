package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.domain.PatientRegistration;
import com.gosmart.backoffice.dto.UserFunctionPermission;
import com.gosmart.backoffice.service.MedicalProviderService;
import com.gosmart.backoffice.service.PatientRegistrationService;
import com.gosmart.backoffice.service.PermissionService;
import com.gosmart.backoffice.service.ProtectedPageModelService;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PatientRegistrationController {
    private static final String PATIENT_REGISTRATION_FUNCTION_PATH = "/patient/registration";
    private static final Pattern E164_MOBILE_PATTERN = Pattern.compile("^\\+[1-9]\\d{6,14}$");

    private final ProtectedPageModelService protectedPageModelService;
    private final PatientRegistrationService patientRegistrationService;
    private final MedicalProviderService medicalProviderService;
    private final PermissionService permissionService;

    public PatientRegistrationController(
            ProtectedPageModelService protectedPageModelService,
            PatientRegistrationService patientRegistrationService,
            MedicalProviderService medicalProviderService,
            PermissionService permissionService
    ) {
        this.protectedPageModelService = protectedPageModelService;
        this.patientRegistrationService = patientRegistrationService;
        this.medicalProviderService = medicalProviderService;
        this.permissionService = permissionService;
    }

    @GetMapping("/patient/registration")
    public String registrationPage(HttpServletRequest request, Model model) {
        protectedPageModelService.apply(model, request, null);
        UserFunctionPermission permission =
                (UserFunctionPermission) request.getAttribute(AuthInterceptor.REQ_ATTR_PERMISSION);
        String currentUserId = requireCurrentUserId(request);
        var medicalProviders = medicalProviderService.findAccessibleByUserId(currentUserId);
        Set<Long> allowedProviderIds = medicalProviders.stream()
                .map(provider -> provider.getId().longValue())
                .collect(Collectors.toSet());
        model.addAttribute("patients", patientRegistrationService.findAll().stream()
                .filter(patient -> patient.getMedicalProviderId() != null)
                .filter(patient -> allowedProviderIds.contains(patient.getMedicalProviderId()))
                .toList());
        model.addAttribute("medicalProviders", medicalProviders);
        model.addAttribute("permission", permission);
        model.addAttribute("requestPath", request.getRequestURI());
        model.addAttribute("functionCode", request.getAttribute(AuthInterceptor.REQ_ATTR_FUNCTION_CODE));
        return "patient-registration";
    }

    @PostMapping("/patient/save")
    @ResponseBody
    public PatientRegistration savePatient(@RequestBody PatientRegistration patient, HttpServletRequest request) {
        // Get logged-in user_id from auth interceptor attribute
        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        requireMedicalProviderAccess(currentUserId, patient.getMedicalProviderId());
        permissionService.requireCreate(
                request,
                currentUserId,
                PATIENT_REGISTRATION_FUNCTION_PATH,
                "You do not have permission to create patients."
        );

        // Set create_by and modify_by automatically
        patient.setMobileNo(normalizeMobileNo(patient.getMobileNo()));
        patient.setCreateBy(currentUserId);
        patient.setModifyBy(currentUserId);

        return patientRegistrationService.save(patient);
    }

    @PutMapping("/patient/{id}")
    @ResponseBody
    public PatientRegistration updatePatient(@PathVariable Long id, @RequestBody PatientRegistration patient, HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        requireMedicalProviderAccess(currentUserId, patient.getMedicalProviderId());
        permissionService.requireEdit(
                request,
                currentUserId,
                PATIENT_REGISTRATION_FUNCTION_PATH,
                "You do not have permission to edit patients."
        );
        Optional<PatientRegistration> existing = patientRegistrationService.findById(id);
        if (existing.isPresent()) {
            PatientRegistration toUpdate = existing.get();
            toUpdate.setName(patient.getName());
            toUpdate.setAge(patient.getAge());
            toUpdate.setGender(patient.getGender());
            toUpdate.setRace(patient.getRace());
            toUpdate.setIcPassportNo(patient.getIcPassportNo());
            toUpdate.setMobileNo(normalizeMobileNo(patient.getMobileNo()));
            toUpdate.setEmergencyContactName(patient.getEmergencyContactName());
            toUpdate.setEmergencyContactNo(patient.getEmergencyContactNo());
            toUpdate.setRelationship(patient.getRelationship());
            toUpdate.setAddress(patient.getAddress());
            toUpdate.setArea(patient.getArea());
            toUpdate.setPostcode(patient.getPostcode());
            toUpdate.setState(patient.getState());
            toUpdate.setCity(patient.getCity());
            toUpdate.setHasChronicDisease(patient.getHasChronicDisease());
            toUpdate.setChronicDisease(patient.getChronicDisease());
            toUpdate.setMedicineTakenNow(patient.getMedicineTakenNow());
            toUpdate.setHasAllergies(patient.getHasAllergies());
            toUpdate.setAllergyDetails(patient.getAllergyDetails());
            toUpdate.setRemark(patient.getRemark());
            toUpdate.setMedicalProviderId(patient.getMedicalProviderId());

            // Set modify_by automatically
            toUpdate.setModifyBy(currentUserId);

            return patientRegistrationService.save(toUpdate);
        }
        return null;
    }

    @DeleteMapping("/patient/{id}")
    @ResponseBody
    public void deletePatient(@PathVariable Long id, HttpServletRequest request) {

        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        permissionService.requireDelete(
                request,
                currentUserId,
                PATIENT_REGISTRATION_FUNCTION_PATH,
                "You do not have permission to delete patients."
        );

        patientRegistrationService.deleteById(id, currentUserId);
    }

    @GetMapping("/patient/{id}")
    @ResponseBody
    public Optional<PatientRegistration> getPatient(@PathVariable Long id, HttpServletRequest request) {
        requireView(request, "You do not have permission to view patients.");
        String currentUserId = requireCurrentUserId(request);
        Set<Long> allowedProviderIds = accessibleProviderIds(currentUserId);
        return patientRegistrationService.findById(id)
                .filter(patient -> patient.getMedicalProviderId() != null)
                .filter(patient -> allowedProviderIds.contains(patient.getMedicalProviderId()));
    }

    @GetMapping("/patient/all")
    @ResponseBody
    public java.util.List<PatientRegistration> getAllPatients(HttpServletRequest request) {
        requireView(request, "You do not have permission to view patients.");
        String currentUserId = requireCurrentUserId(request);
        Set<Long> allowedProviderIds = accessibleProviderIds(currentUserId);
        return patientRegistrationService.findAll().stream()
                .filter(patient -> patient.getMedicalProviderId() != null)
                .filter(patient -> allowedProviderIds.contains(patient.getMedicalProviderId()))
                .toList();
    }

    private void requireView(HttpServletRequest request, String message) {
        String currentUserId = requireCurrentUserId(request);
        permissionService.requireView(request, currentUserId, PATIENT_REGISTRATION_FUNCTION_PATH, message);
    }

    private String requireCurrentUserId(HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        return currentUserId;
    }

    private Set<Long> accessibleProviderIds(String currentUserId) {
        return medicalProviderService.findAccessibleByUserId(currentUserId).stream()
                .map(provider -> provider.getId().longValue())
                .collect(Collectors.toSet());
    }

    private void requireMedicalProviderAccess(String currentUserId, Long medicalProviderId) {
        Integer providerId = medicalProviderId == null ? null : Math.toIntExact(medicalProviderId);
        if (!medicalProviderService.hasAccessibleProvider(currentUserId, providerId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You do not have access to this medical provider."
            );
        }
    }

    private String normalizeMobileNo(String mobileNo) {
        String normalized = mobileNo == null ? "" : mobileNo.replaceAll("\\s+", "");
        if (!E164_MOBILE_PATTERN.matcher(normalized).matches()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Mobile number must use E.164 format, for example +60123857583."
            );
        }
        return normalized;
    }
}
