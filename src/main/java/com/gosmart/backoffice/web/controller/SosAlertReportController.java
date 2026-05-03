package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.dto.UserFunctionPermission;
import com.gosmart.backoffice.service.MedicalProviderService;
import com.gosmart.backoffice.service.PermissionService;
import com.gosmart.backoffice.service.ProtectedPageModelService;
import com.gosmart.backoffice.service.SosAlertReportService;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SosAlertReportController {
    private static final String SOS_ALERT_REPORT_FUNCTION_PATH = "/report/sos-alert";

    private final ProtectedPageModelService protectedPageModelService;
    private final MedicalProviderService medicalProviderService;
    private final SosAlertReportService sosAlertReportService;
    private final PermissionService permissionService;

    public SosAlertReportController(
            ProtectedPageModelService protectedPageModelService,
            MedicalProviderService medicalProviderService,
            SosAlertReportService sosAlertReportService,
            PermissionService permissionService
    ) {
        this.protectedPageModelService = protectedPageModelService;
        this.medicalProviderService = medicalProviderService;
        this.sosAlertReportService = sosAlertReportService;
        this.permissionService = permissionService;
    }

    @GetMapping("/report/sos-alert")
    public String sosAlertReportPage(HttpServletRequest request, Model model) {
        protectedPageModelService.apply(model, request, null);
        String currentUserId = requireCurrentUserId(request);
        permissionService.requireView(
                request,
                currentUserId,
                SOS_ALERT_REPORT_FUNCTION_PATH,
                "You do not have permission to view SOS alert report."
        );
        UserFunctionPermission permission =
                (UserFunctionPermission) request.getAttribute(AuthInterceptor.REQ_ATTR_PERMISSION);
        var medicalProviders = medicalProviderService.findAccessibleByUserId(currentUserId);
        Set<Integer> allowedProviderIds = medicalProviders.stream()
                .map(provider -> provider.getId())
                .collect(Collectors.toSet());

        model.addAttribute("medicalProviders", medicalProviders);
        model.addAttribute("sosAlerts", sosAlertReportService.findAlerts(allowedProviderIds));
        model.addAttribute("permission", permission);
        model.addAttribute("requestPath", request.getRequestURI());
        model.addAttribute("functionCode", request.getAttribute(AuthInterceptor.REQ_ATTR_FUNCTION_CODE));
        return "sos-alert-report";
    }

    @GetMapping("/report/sos-alert/{id}/push-alerts")
    @ResponseBody
    public Object sosAlertPushAlerts(@PathVariable Integer id, HttpServletRequest request) {
        String currentUserId = requireCurrentUserId(request);
        permissionService.requireView(
                request,
                currentUserId,
                SOS_ALERT_REPORT_FUNCTION_PATH,
                "You do not have permission to view SOS alert report."
        );
        Set<Integer> allowedProviderIds = medicalProviderService.findAccessibleByUserId(currentUserId).stream()
                .map(provider -> provider.getId())
                .collect(Collectors.toSet());
        return sosAlertReportService.findCaregiverPushAlerts(id, allowedProviderIds);
    }

    private String requireCurrentUserId(HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        return currentUserId;
    }
}
