package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.config.AuthProperties;
import com.gosmart.backoffice.service.MenuService;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    private final AuthProperties authProperties;
    private final MenuService menuService;
    private final String appVersion;

    public DashboardController(
            AuthProperties authProperties,
            MenuService menuService,
            @Value("${app.version:}") String appVersion
    ) {
        this.authProperties = authProperties;
        this.menuService = menuService;
        this.appVersion = appVersion;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        Object userId = request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        model.addAttribute("userId", userId);
        model.addAttribute("idleTimeoutMinutes", authProperties.getIdleTimeoutMinutes());
        model.addAttribute("appVersion", appVersion == null || appVersion.isBlank() ? null : appVersion);
        model.addAttribute("menuGroups", menuService.buildMenuGroups(userId == null ? null : userId.toString()));
        return "dashboard";
    }
}
