package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.dto.SubscriptionSaveRequest;
import com.gosmart.backoffice.dto.SubscriptionView;
import com.gosmart.backoffice.dto.UserFunctionPermission;
import com.gosmart.backoffice.service.MedicalPackageService;
import com.gosmart.backoffice.service.MedicalProviderService;
import com.gosmart.backoffice.service.PackageSubscriptionService;
import com.gosmart.backoffice.service.PatientRegistrationService;
import com.gosmart.backoffice.service.ProtectedPageModelService;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SubscriptionController {

    private final ProtectedPageModelService protectedPageModelService;
    private final MedicalProviderService medicalProviderService;
    private final MedicalPackageService medicalPackageService;
    private final PatientRegistrationService patientRegistrationService;
    private final PackageSubscriptionService packageSubscriptionService;

    public SubscriptionController(
            ProtectedPageModelService protectedPageModelService,
            MedicalProviderService medicalProviderService,
            MedicalPackageService medicalPackageService,
            PatientRegistrationService patientRegistrationService,
            PackageSubscriptionService packageSubscriptionService
    ) {
        this.protectedPageModelService = protectedPageModelService;
        this.medicalProviderService = medicalProviderService;
        this.medicalPackageService = medicalPackageService;
        this.patientRegistrationService = patientRegistrationService;
        this.packageSubscriptionService = packageSubscriptionService;
    }

    @GetMapping("/patient/subscription")
    public String subscriptionPage(HttpServletRequest request, Model model) {
        protectedPageModelService.apply(model, request, null);
        UserFunctionPermission permission =
                (UserFunctionPermission) request.getAttribute(AuthInterceptor.REQ_ATTR_PERMISSION);
        model.addAttribute("medicalProviders", medicalProviderService.findAll());
        model.addAttribute("patients", patientRegistrationService.findAll());
        model.addAttribute("subscriptions", packageSubscriptionService.findAllActiveSubscriptions());
        model.addAttribute("subscriptionPackages", medicalPackageService.getAllPackages());
        model.addAttribute("permission", permission);
        model.addAttribute("requestPath", request.getRequestURI());
        model.addAttribute("functionCode", request.getAttribute(AuthInterceptor.REQ_ATTR_FUNCTION_CODE));
        return "patient-subscription";
    }

    @PostMapping("/patient/subscription/save")
    @ResponseBody
    public ResponseEntity<?> saveSubscription(
            @RequestBody SubscriptionSaveRequest request,
            HttpServletRequest httpRequest
    ) {
        String currentUserId = (String) httpRequest.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        try {
            SubscriptionView savedSubscription = packageSubscriptionService.saveSubscription(request, currentUserId);
            return ResponseEntity.ok(savedSubscription);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/patient/subscription/{id}")
    @ResponseBody
    public void deleteSubscription(@PathVariable int id, HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        packageSubscriptionService.deleteSubscription(id);
    }
}
