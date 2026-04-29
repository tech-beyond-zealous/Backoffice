package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.dto.SubscriptionSaveRequest;
import com.gosmart.backoffice.dto.SubscriptionView;
import com.gosmart.backoffice.dto.UserFunctionPermission;
import com.gosmart.backoffice.service.MedicalPackageService;
import com.gosmart.backoffice.service.MedicalProviderService;
import com.gosmart.backoffice.service.PackageSubscriptionService;
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
    private static final Logger log = LoggerFactory.getLogger(SubscriptionController.class);
    private static final String SUBSCRIPTION_FUNCTION_PATH = "/patient/subscription";

    private final ProtectedPageModelService protectedPageModelService;
    private final MedicalProviderService medicalProviderService;
    private final MedicalPackageService medicalPackageService;
    private final PatientRegistrationService patientRegistrationService;
    private final PackageSubscriptionService packageSubscriptionService;
    private final PermissionService permissionService;

    public SubscriptionController(
            ProtectedPageModelService protectedPageModelService,
            MedicalProviderService medicalProviderService,
            MedicalPackageService medicalPackageService,
            PatientRegistrationService patientRegistrationService,
            PackageSubscriptionService packageSubscriptionService,
            PermissionService permissionService
    ) {
        this.protectedPageModelService = protectedPageModelService;
        this.medicalProviderService = medicalProviderService;
        this.medicalPackageService = medicalPackageService;
        this.patientRegistrationService = patientRegistrationService;
        this.packageSubscriptionService = packageSubscriptionService;
        this.permissionService = permissionService;
    }

    @GetMapping("/patient/subscription")
    public String subscriptionPage(HttpServletRequest request, Model model) {
        protectedPageModelService.apply(model, request, null);
        UserFunctionPermission permission =
                (UserFunctionPermission) request.getAttribute(AuthInterceptor.REQ_ATTR_PERMISSION);
        String currentUserId = requireCurrentUserId(request);
        var medicalProviders = medicalProviderService.findAccessibleByUserId(currentUserId);
        Set<Integer> allowedProviderIds = medicalProviders.stream()
                .map(provider -> provider.getId())
                .collect(Collectors.toSet());
        Set<Long> allowedProviderIdsLong = allowedProviderIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toSet());
        model.addAttribute("medicalProviders", medicalProviders);
        model.addAttribute("patients", patientRegistrationService.findAll().stream()
                .filter(patient -> patient.getMedicalProviderId() != null)
                .filter(patient -> allowedProviderIdsLong.contains(patient.getMedicalProviderId()))
                .toList());
        model.addAttribute("subscriptionPackages", medicalPackageService.getAllPackages());
        try {
            model.addAttribute("subscriptions", packageSubscriptionService.findAllActiveSubscriptions().stream()
                    .filter(subscription -> subscription.getMedicalProviderId() != null)
                    .filter(subscription -> allowedProviderIds.contains(subscription.getMedicalProviderId()))
                    .toList());
        } catch (RuntimeException ex) {
            log.error("Unable to load patient subscription page data", ex);
            model.addAttribute("subscriptions", Collections.emptyList());
            model.addAttribute(
                    "subscriptionLoadError",
                    "Subscription data could not be loaded. Please verify the database changes for package subscriptions are applied."
            );
        }
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
        if (!medicalProviderService.hasAccessibleProvider(currentUserId, request.getMedicalProviderId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have access to this medical provider.");
        }
        UserFunctionPermission permission =
                permissionService.resolve(httpRequest, currentUserId, SUBSCRIPTION_FUNCTION_PATH);
        boolean isEdit = request.getId() != null;
        if (isEdit && (permission == null || !permission.isEdit())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to edit subscriptions.");
        }
        if (!isEdit && (permission == null || !permission.isCreate())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to create subscriptions.");
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
        String currentUserId = requireCurrentUserId(request);
        permissionService.requireDelete(
                request,
                currentUserId,
                SUBSCRIPTION_FUNCTION_PATH,
                "You do not have permission to delete subscriptions."
        );
        packageSubscriptionService.deleteSubscription(id);
    }

    private String requireCurrentUserId(HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        return currentUserId;
    }
}
