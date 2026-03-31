package com.gosmart.backoffice.service;

import com.gosmart.backoffice.config.AuthProperties;
import com.gosmart.backoffice.service.MenuService.ApplicationSystem;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class ProtectedPageModelService {
    private final AuthProperties authProperties;
    private final MenuService menuService;
    private final String appVersion;

    public ProtectedPageModelService(
            AuthProperties authProperties,
            MenuService menuService,
            @Value("${app.version:}") String appVersion
    ) {
        this.authProperties = authProperties;
        this.menuService = menuService;
        this.appVersion = appVersion;
    }

    public Long apply(Model model, HttpServletRequest request, Long applicationSystemId) {
        Object userId = request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        String userIdStr = userId == null ? null : userId.toString();
        model.addAttribute("userId", userIdStr);
        model.addAttribute("idleTimeoutMinutes", authProperties.getIdleTimeoutMinutes());
        model.addAttribute("appVersion", appVersion == null || appVersion.isBlank() ? null : appVersion);

        List<ApplicationSystem> applicationSystems = menuService.buildApplicationSystems(userIdStr);
        Long requestedApplicationSystemId = applicationSystemId;
        boolean hasRequestedApplicationSystem = requestedApplicationSystemId != null
                && applicationSystems.stream().anyMatch(s -> s.applicationSystemId().equals(requestedApplicationSystemId));
        Long selectedApplicationSystemId = hasRequestedApplicationSystem
                ? requestedApplicationSystemId
                : (applicationSystems.isEmpty() ? null : applicationSystems.get(0).applicationSystemId());

        model.addAttribute("applicationSystems", applicationSystems);
        model.addAttribute("selectedApplicationSystemId", selectedApplicationSystemId);
        model.addAttribute("menuGroups", menuService.buildMenuGroups(userIdStr, selectedApplicationSystemId));

        return selectedApplicationSystemId;
    }
}
