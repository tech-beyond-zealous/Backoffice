package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.dto.UserFunctionPermission;
import com.gosmart.backoffice.service.ProtectedPageModelService;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestFunctionController {
    private final ProtectedPageModelService protectedPageModelService;

    public TestFunctionController(ProtectedPageModelService protectedPageModelService) {
        this.protectedPageModelService = protectedPageModelService;
    }

    @GetMapping("/testfunction")
    public String testFunction(
            HttpServletRequest request,
            @RequestParam(name = "applicationSystemId", required = false) Long applicationSystemId,
            Model model
    ) {
        protectedPageModelService.apply(model, request, applicationSystemId);

        UserFunctionPermission permission = (UserFunctionPermission) request.getAttribute(AuthInterceptor.REQ_ATTR_PERMISSION);
        model.addAttribute("permission", permission);
        model.addAttribute("requestPath", request.getRequestURI());
        model.addAttribute("functionCode", request.getAttribute(AuthInterceptor.REQ_ATTR_FUNCTION_CODE));
        return "testfunction";
    }
}
