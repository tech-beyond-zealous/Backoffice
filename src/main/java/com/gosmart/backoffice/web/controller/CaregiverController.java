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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CaregiverController {
    private static final String CAREGIVER_REGISTRATION_FUNCTION_PATH = "/caregiver/registration";

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
        model.addAttribute("caregivers", caregiverService.findAll());
        model.addAttribute("medicalProviders", medicalProviderService.findAll());
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
        permissionService.requireCreate(
                request,
                userId,
                CAREGIVER_REGISTRATION_FUNCTION_PATH,
                "You do not have permission to create caregivers."
        );
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
            toUpdate.setMobileNumber(caregiver.getMobileNumber());
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
        return caregiverService.findById(id);
    }

    @GetMapping("/caregiver/all")
    @ResponseBody
    public List<CaregiverEntity> getAllCaregivers(HttpServletRequest request) {
        requireView(request);
        return caregiverService.findAll();
    }

    private void requireView(HttpServletRequest request) {
        String userId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (userId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        permissionService.requireView(
                request,
                userId,
                CAREGIVER_REGISTRATION_FUNCTION_PATH,
                "You do not have permission to view caregivers."
        );
    }
}
