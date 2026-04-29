package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.domain.CaregiverEntity;
import com.gosmart.backoffice.dto.UserFunctionPermission;
import com.gosmart.backoffice.service.CaregiverService;
import com.gosmart.backoffice.service.MedicalProviderService;
import com.gosmart.backoffice.service.PermissionService;
import com.gosmart.backoffice.service.ProtectedPageModelService;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CaregiverController {
    private static final String CAREGIVER_REGISTRATION_FUNCTION_PATH = "/caregiver/registration";
    private static final Pattern E164_MOBILE_PATTERN = Pattern.compile("^\\+[1-9]\\d{6,14}$");

    private final ProtectedPageModelService protectedPageModelService;
    private final CaregiverService caregiverService;
    private final MedicalProviderService medicalProviderService;
    private final PermissionService permissionService;

    public CaregiverController(
            ProtectedPageModelService protectedPageModelService,
            CaregiverService caregiverService,
            MedicalProviderService medicalProviderService,
            PermissionService permissionService
    ) {
        this.protectedPageModelService = protectedPageModelService;
        this.caregiverService = caregiverService;
        this.medicalProviderService = medicalProviderService;
        this.permissionService = permissionService;
    }

    @GetMapping("/caregiver/registration")
    public String registrationPage(HttpServletRequest request, Model model) {
        protectedPageModelService.apply(model, request, null);
        UserFunctionPermission permission = (UserFunctionPermission) request.getAttribute(AuthInterceptor.REQ_ATTR_PERMISSION);
        String userId = requireCurrentUserId(request);
        var medicalProviders = medicalProviderService.findAccessibleByUserId(userId);
        Set<Integer> allowedProviderIds = medicalProviders.stream()
                .map(provider -> provider.getId())
                .collect(Collectors.toSet());
        model.addAttribute("caregivers", caregiverService.findAll().stream()
                .filter(caregiver -> caregiver.getMedicalProviderId() != null)
                .filter(caregiver -> allowedProviderIds.contains(caregiver.getMedicalProviderId()))
                .toList());
        model.addAttribute("medicalProviders", medicalProviders);
        model.addAttribute("permission", permission);
        model.addAttribute("requestPath", request.getRequestURI());
        model.addAttribute("functionCode", request.getAttribute(AuthInterceptor.REQ_ATTR_FUNCTION_CODE));
        return "caregiver-registration";
    }

    @PostMapping("/caregiver/save")
    @ResponseBody
    public CaregiverEntity saveCaregiver(@RequestBody CaregiverEntity caregiver, HttpServletRequest request) {
        String userId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (userId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        requireMedicalProviderAccess(userId, caregiver.getMedicalProviderId());
        permissionService.requireCreate(
                request,
                userId,
                CAREGIVER_REGISTRATION_FUNCTION_PATH,
                "You do not have permission to create caregivers."
        );
        caregiver.setMobileNumber(normalizeMobileNumber(caregiver.getMobileNumber()));
        caregiver.setCreateBy(userId);
        caregiver.setModifyBy(userId);
        return caregiverService.save(caregiver);
    }

    @PutMapping("/caregiver/{id}")
    @ResponseBody
    public CaregiverEntity updateCaregiver(@PathVariable Long id, @RequestBody CaregiverEntity caregiver, HttpServletRequest request) {
        String userId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (userId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        requireMedicalProviderAccess(userId, caregiver.getMedicalProviderId());
        permissionService.requireEdit(
                request,
                userId,
                CAREGIVER_REGISTRATION_FUNCTION_PATH,
                "You do not have permission to edit caregivers."
        );
        Optional<CaregiverEntity> existing = caregiverService.findById(id);
        if (existing.isPresent()) {
            CaregiverEntity toUpdate = existing.get();
            toUpdate.setMedicalProviderId(caregiver.getMedicalProviderId());
            toUpdate.setName(caregiver.getName());
            toUpdate.setEmail(caregiver.getEmail());
            toUpdate.setMobileNumber(normalizeMobileNumber(caregiver.getMobileNumber()));
            toUpdate.setUserId(caregiver.getUserId());
            toUpdate.setModifyBy(userId);
            toUpdate.setStatus(caregiver.getStatus());
            // Following your established pattern of updating specific entity fields manually
            return caregiverService.save(toUpdate);
        }
        return null;
    }

    @DeleteMapping("/caregiver/{id}")
    @ResponseBody
    public void deleteCaregiver(@PathVariable Long id, HttpServletRequest request) {
        String userId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (userId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        permissionService.requireDelete(
                request,
                userId,
                CAREGIVER_REGISTRATION_FUNCTION_PATH,
                "You do not have permission to delete caregivers."
        );
        caregiverService.softDelete(id, userId);
    }

    @GetMapping("/caregiver/{id}")
    @ResponseBody
    public Optional<CaregiverEntity> getCaregiver(@PathVariable Long id, HttpServletRequest request) {
        requireView(request);
        String userId = requireCurrentUserId(request);
        Set<Integer> allowedProviderIds = accessibleProviderIds(userId);
        return caregiverService.findById(id)
                .filter(caregiver -> caregiver.getMedicalProviderId() != null)
                .filter(caregiver -> allowedProviderIds.contains(caregiver.getMedicalProviderId()));
    }

    @GetMapping("/caregiver/all")
    @ResponseBody
    public List<CaregiverEntity> getAllCaregivers(HttpServletRequest request) {
        requireView(request);
        String userId = requireCurrentUserId(request);
        Set<Integer> allowedProviderIds = accessibleProviderIds(userId);
        return caregiverService.findAll().stream()
                .filter(caregiver -> caregiver.getMedicalProviderId() != null)
                .filter(caregiver -> allowedProviderIds.contains(caregiver.getMedicalProviderId()))
                .toList();
    }

    private void requireView(HttpServletRequest request) {
        String userId = requireCurrentUserId(request);
        permissionService.requireView(
                request,
                userId,
                CAREGIVER_REGISTRATION_FUNCTION_PATH,
                "You do not have permission to view caregivers."
        );
    }

    private String requireCurrentUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (userId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        return userId;
    }

    private Set<Integer> accessibleProviderIds(String userId) {
        return medicalProviderService.findAccessibleByUserId(userId).stream()
                .map(provider -> provider.getId())
                .collect(Collectors.toSet());
    }

    private void requireMedicalProviderAccess(String userId, Integer medicalProviderId) {
        if (!medicalProviderService.hasAccessibleProvider(userId, medicalProviderId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.FORBIDDEN,
                    "You do not have access to this medical provider."
            );
        }
    }

    private String normalizeMobileNumber(String mobileNumber) {
        String normalized = mobileNumber == null ? "" : mobileNumber.replaceAll("\\s+", "");
        if (!E164_MOBILE_PATTERN.matcher(normalized).matches()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Mobile number must use E.164 format, for example +60123857583."
            );
        }
        return normalized;
    }
}
