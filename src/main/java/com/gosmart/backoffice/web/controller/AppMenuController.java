package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.service.ProtectedPageModelService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppMenuController {
    private final ProtectedPageModelService protectedPageModelService;

    public AppMenuController(ProtectedPageModelService protectedPageModelService) {
        this.protectedPageModelService = protectedPageModelService;
    }

    @GetMapping({
            "/gosmart/**",
            "/2nd-medical/**"
    })
    public String genericMenuPage(
            HttpServletRequest request,
            @RequestParam(name = "applicationSystemId", required = false) Long applicationSystemId,
            @RequestParam(name = "functionId", required = false) Long functionId,
            Model model
    ) {
        protectedPageModelService.apply(model, request, applicationSystemId);

        String requestPath = request.getRequestURI();
        model.addAttribute("requestPath", requestPath);

        // Function code is set for display; authorization may be empty if no explicit function mapping is present.
        String functionCode = requestPath.replaceAll("[^a-zA-Z0-9]", "_").toUpperCase();
        model.addAttribute("functionCode", functionCode);

        // Reuse the existing 'testfunction' template for stubbed routes.
        return "testfunction";
    }
}
